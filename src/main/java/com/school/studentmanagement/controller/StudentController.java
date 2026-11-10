package com.school.studentmanagement.controller;

import com.school.studentmanagement.exception.DatabaseException;
import com.school.studentmanagement.exception.DuplicateEmailException;
import com.school.studentmanagement.model.Student;
import com.school.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    // Constructor injection
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // GET /student/register -> register.html
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("student", new Student());
        return "register";
    }

    // POST /student/register -> save, then show register.html with success message
    @PostMapping("/register")
    public String registerStudent(@Valid @ModelAttribute("student") Student student,
                                  BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            studentService.registerStudent(student);
            model.addAttribute("successMessage", "Student " + student.getFullName() + " registered successfully!");
            model.addAttribute("student", new Student()); // clear the form
        } catch (DuplicateEmailException e) {
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
        } catch (DatabaseException e) {
            model.addAttribute("errorMessage", "Database error. Please try again later.");
        }
        return "register";
    }

    // GET /student/all -> students.html (all students)
    @GetMapping("/all")
    public String showAllStudents(Model model) {
        try {
            List<Student> students = studentService.getAllStudents();
            model.addAttribute("students", students);
        } catch (DatabaseException e) {
            model.addAttribute("errorMessage", "Could not load students from the database.");
        }
        return "students";
    }

    // BONUS: GET /student/{id} -> reuses students.html
    @GetMapping("/{id}")
    public String showStudentById(@PathVariable("id") int id, Model model) {
        try {
            Optional<Student> student = studentService.getStudentById(id);
            if (student.isPresent()) {
                model.addAttribute("students", List.of(student.get()));
            } else {
                model.addAttribute("errorMessage", "Student with ID " + id + " was not found.");
            }
        } catch (DatabaseException e) {
            model.addAttribute("errorMessage", "Could not load the student from the database.");
        }
        return "students";
    }
}
