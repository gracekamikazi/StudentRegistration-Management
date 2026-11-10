package com.school.studentmanagement.service;

import com.school.studentmanagement.model.Student;
import com.school.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Constructor injection: Spring supplies the StudentRepository bean
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void registerStudent(Student student) {
        studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(int id) {
        return studentRepository.findById(id);
    }
}
