package com.akvelon.planningsystem.exception;

import java.sql.SQLException;

public class DaoOperationException extends RuntimeException {

    public DaoOperationException(String message) {
        super(message);
    }


    public DaoOperationException(String format, Exception e) {

    }
}
