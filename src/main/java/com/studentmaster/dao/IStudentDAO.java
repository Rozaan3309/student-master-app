package com.studentmaster.dao;

import com.studentmaster.model.Student;

import java.util.List;

public interface IStudentDAO {

    void addStudent(Student student);

    List<Student> getAllStudents();

    List<Student> findByIdRange(int fromId, int toId);

    boolean updateStudent(Student student);

    boolean deleteStudent(int studentId);
}
