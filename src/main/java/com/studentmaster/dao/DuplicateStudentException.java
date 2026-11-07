package com.studentmaster.dao;

public class DuplicateStudentException extends DataAccessException {
    public DuplicateStudentException(int studentId, Throwable cause) {
        super("A student with ID " + studentId + " already exists.", cause);
    }
}
