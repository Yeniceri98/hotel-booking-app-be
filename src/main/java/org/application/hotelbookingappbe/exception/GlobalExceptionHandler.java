package org.application.hotelbookingappbe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomIsNotFoundException.class)
    public ResponseEntity<ErrorObject> handleRoomIsNotFoundException(RoomIsNotFoundException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject(
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoomIsNotAvailableException.class)
    public ResponseEntity<ErrorObject> handleRoomIsNotAvailableException(RoomIsNotAvailableException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject(
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookingIsNotFoundException.class)
    public ResponseEntity<ErrorObject> handleBookingIsNotFoundException(BookingIsNotFoundException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject(
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidBookingRequestException.class)
    public ResponseEntity<ErrorObject> handleInvalidBookingRequestException(InvalidBookingRequestException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject(
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<ErrorObject> handleRoleAlreadyExistsException(RoleAlreadyExistsException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject(
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorObject> handleRoleNotFoundException(RoleNotFoundException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject(
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorObject> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject(
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorObject> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject(
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }
}
