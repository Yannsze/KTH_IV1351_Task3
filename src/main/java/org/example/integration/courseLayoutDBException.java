package org.example.integration;

// Thrown when call to database fails

// Create new instance thrown because of specific reason
public class courseLayoutDBException extends Exception {
    public courseLayoutDBException(String reason) {
        super(reason);
    }

    // Create new instance thrown because of specified reason and cause
    public courseLayoutDBException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
