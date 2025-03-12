package com.focuslibrary.focus_library.exeptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    private CustomErrorType defaultCustomErrorTypeConstruct(String message) {
        return CustomErrorType.builder()
                .timestamp(LocalDateTime.now())
                .errors(new ArrayList<>())
                .message(message)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        CustomErrorType customErrorType = defaultCustomErrorTypeConstruct(
                "Erros de validacao encontrados"
        );
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            customErrorType.getErrors().add(fieldError.getDefaultMessage());
        }
        return customErrorType;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onConstraintViolation(ConstraintViolationException e) {
        CustomErrorType customErrorType = defaultCustomErrorTypeConstruct(
                "Erros de validacao encontrados"
        );
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            customErrorType.getErrors().add(violation.getMessage());
        }
        return customErrorType;
    }

    @ExceptionHandler(FocusLibraryExeption.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onCommerceException(FocusLibraryExeption e) {
        return defaultCustomErrorTypeConstruct(
                e.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CustomErrorType onDataIntegrityViolation(DataIntegrityViolationException e) {
        String errorMessage = "Erro de integridade de dados. Verifique se há dados duplicados.";
        String message = e.getCause() != null ? e.getCause().getMessage() : "";
        if (message.contains("duplicate key value violates unique constraint")) {
            errorMessage = "Violação de chave única encontrada. Verifique os dados fornecidos.";
        }

        return defaultCustomErrorTypeConstruct(errorMessage);
    }

}
