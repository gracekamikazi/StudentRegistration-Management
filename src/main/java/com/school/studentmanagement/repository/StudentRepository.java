package com.school.studentmanagement.repository;

import com.school.studentmanagement.exception.DatabaseException;
import com.school.studentmanagement.exception.DuplicateEmailException;
import com.school.studentmanagement.model.Student;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

    private static final String INSERT_SQL = "INSERT INTO students (full_name, age, email) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT id, full_name, age, email FROM students ORDER BY id";
    private static final String SELECT_BY_ID_SQL = "SELECT id, full_name, age, email FROM students WHERE id = ?";

    private final DataSource dataSource;

    // Constructor injection
    public StudentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Student student) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, student.getFullName()); // firstName + lastName -> full_name
            ps.setInt(2, student.getAge());
            ps.setString(3, student.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // unique violation
                throw new DuplicateEmailException("A student with this email already exists.");
            }
            throw new DatabaseException("Could not save student: " + e.getMessage(), e);
        }
    }

    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not retrieve students: " + e.getMessage(), e);
        }
        return students;
    }

    public Optional<Student> findById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not retrieve student: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /** Splits full_name back into first and last name for display. */
    private Student mapRow(ResultSet rs) throws SQLException {
        String fullName = rs.getString("full_name").trim();
        int space = fullName.indexOf(' ');
        String first = space > 0 ? fullName.substring(0, space) : fullName;
        String last = space > 0 ? fullName.substring(space + 1).trim() : "";
        return new Student(rs.getInt("id"), first, last, rs.getString("email"), rs.getInt("age"));
    }
}
