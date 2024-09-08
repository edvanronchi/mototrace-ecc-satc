package dev.edvanronchi.mototraceapi.infra;

import dev.edvanronchi.mototraceapi.application.dtos.ExceptionDto;
import dev.edvanronchi.mototraceapi.domain.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleGeneralException(Exception exception) {
        ExceptionDto exceptionDto = new ExceptionDto(exception.getMessage());
        return ResponseEntity.badRequest().body(exceptionDto);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(NotFoundException exception) {
        ExceptionDto exceptionDto = new ExceptionDto(exception.getMessage());
        return ResponseEntity.badRequest().body(exceptionDto);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ExceptionDto> handleNotFoundException(TransactionSystemException exception) {
        Throwable cause = exception.getRootCause();

        if (cause instanceof ConstraintViolationException constraintViolationException) {
            ConstraintViolation<?> constraintViolation = constraintViolationException.getConstraintViolations().stream().findFirst().get();
            ExceptionDto exceptionDto = new ExceptionDto(constraintViolation.getMessage());
            return ResponseEntity.badRequest().body(exceptionDto);
        }
        return ResponseEntity.badRequest().body(new ExceptionDto("Erro de transação"));
    }
}
