package com.studentmaster.dao;

import com.studentmaster.db.DatabaseConnector;
import com.studentmaster.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO implements IStudentDAO {

    private static final String INSERT = "INSERT INTO students (student_id, name, email) VALUES (?, ?, ?)";
    private static final String SELECT_ALL = "SELECT student_id, name, email FROM students ORDER BY student_id";
    private static final String SELECT_RANGE =
            "SELECT student_id, name, email FROM students WHERE student_id BETWEEN ? AND ? ORDER BY student_id";
    private static final String UPDATE = "UPDATE students SET name = ?, email = ? WHERE student_id = ?";
    private static final String DELETE = "DELETE FROM students WHERE student_id = ?";

    @Override
    public void addStudent(Student student) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT)) {
            ps.setInt(1, student.getStudentId());
            ps.setString(2, student.getName());
            ps.setString(3, student.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            // SQLState 23xxx = integrity constraint violation (duplicate primary key)
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new DuplicateStudentException(student.getStudentId(), e);
            }
            throw new DataAccessException("Could not add the student: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            return readAll(rs);
        } catch (SQLException e) {
            throw new DataAccessException("Could not load students: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> findByIdRange(int fromId, int toId) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_RANGE)) {
            ps.setInt(1, fromId);
            ps.setInt(2, toId);
            try (ResultSet rs = ps.executeQuery()) {
                return readAll(rs);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load students: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStudent(Student student) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setInt(3, student.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not update the student: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteStudent(int studentId) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete the student: " + e.getMessage(), e);
        }
    }

    private static List<Student> readAll(ResultSet rs) throws SQLException {
        List<Student> students = new ArrayList<>();
        while (rs.next()) {
            students.add(new Student(rs.getInt("student_id"), rs.getString("name"), rs.getString("email")));
        }
        return students;
    }
}
