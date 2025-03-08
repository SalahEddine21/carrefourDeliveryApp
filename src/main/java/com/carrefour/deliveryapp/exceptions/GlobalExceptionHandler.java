package com.carrefour.deliveryapp.exceptions;

import com.carrefour.deliveryapp.exceptions.model.ErrorBody;
import jakarta.persistence.PessimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleAllExceptions(Exception ex) {
        ErrorBody error = ErrorBody.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(String.format("[%s: %s] ", error.getField(), error.getDefaultMessage()))
        );

        ErrorBody error = ErrorBody.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage.toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorBody> handleAccessDeniedExceptions(AccessDeniedException ex) {
        ErrorBody error = ErrorBody.builder()
                .code(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorBody> handleNotFoundException(NotFoundException ex) {
        ErrorBody error = ErrorBody.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientQtyException.class)
    public ResponseEntity<ErrorBody> handleInsufficientQtyException(InsufficientQtyException ex) {
        ErrorBody error = ErrorBody.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DSMaxReservationException.class)
    public ResponseEntity<ErrorBody> handleDeliveySlotMaxReservationException(DSMaxReservationException ex) {
        ErrorBody error = ErrorBody.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DeliveryNotEligibleToUpdateException.class)
    public ResponseEntity<ErrorBody> handleDeliveryNotEligibleToUpdateException(DeliveryNotEligibleToUpdateException ex) {
        ErrorBody error = ErrorBody.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(PessimisticLockException.class)
    public ResponseEntity<ErrorBody> handlePessimisticLockException(PessimisticLockException ex) {
        ErrorBody error = ErrorBody.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Delivery slot is currently being booked by another customer. Please try again.")
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}