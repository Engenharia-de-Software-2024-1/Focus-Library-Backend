package com.focuslibrary.focus_library.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.focuslibrary.focus_library.exceptions.CustomErrorType;
import com.focuslibrary.focus_library.exceptions.ErrorHandlingControllerAdvice;
import com.focuslibrary.focus_library.exceptions.FocusLibraryException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

public class ErrorHandlingControllerAdviceTest {
    private final ErrorHandlingControllerAdvice advice = new ErrorHandlingControllerAdvice();

    @Test
    void testOnMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "Error message");

        Mockito.when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        Mockito.when(exception.getBindingResult()).thenReturn(bindingResult);

        CustomErrorType error = advice.onMethodArgumentNotValidException(exception);

        assertNotNull(error);
        assertEquals("Erros de validacao encontrados", error.getMessage());
        assertEquals(1, error.getErrors().size());
        assertEquals("Error message", error.getErrors().get(0));
    }

    @Test
    void testOnConstraintViolationException() {
        ConstraintViolationException exception = Mockito.mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation.getMessage()).thenReturn("Constraint violation message");
        Set<ConstraintViolation<?>> violations = Set.of(violation);
        Mockito.when(exception.getConstraintViolations()).thenReturn(violations);

        CustomErrorType error = advice.onConstraintViolation(exception);

        assertNotNull(error);
        assertEquals("Erros de validacao encontrados", error.getMessage());
        assertEquals(1, error.getErrors().size());
        assertEquals("Constraint violation message", error.getErrors().get(0));
    }

    @Test
    void testOnFocusLibraryException() {
        FocusLibraryException exception = new FocusLibraryException("Focus library error");

        CustomErrorType error = advice.onCommerceException(exception);

        assertNotNull(error);
        assertEquals("Focus library error", error.getMessage());
        assertTrue(error.getErrors().isEmpty()); 
    }

    @Test
    void testOnDataIntegrityViolationException_withDuplicateKey() {
        DataIntegrityViolationException exception = Mockito.mock(DataIntegrityViolationException.class);
        Throwable cause = Mockito.mock(Throwable.class);
        
        Mockito.when(exception.getCause()).thenReturn(cause);
        Mockito.when(cause.getMessage()).thenReturn("duplicate key value violates unique constraint");

        CustomErrorType error = advice.onDataIntegrityViolation(exception);

        assertNotNull(error);
        assertEquals("Violação de chave única encontrada. Verifique os dados fornecidos.", error.getMessage());
        assertTrue(error.getErrors().isEmpty());
    }

    @Test
    void testOnDataIntegrityViolationException_withNonDuplicateKey() {
        DataIntegrityViolationException exception = Mockito.mock(DataIntegrityViolationException.class);
        Throwable cause = Mockito.mock(Throwable.class);
        
        Mockito.when(exception.getCause()).thenReturn(cause);
        Mockito.when(cause.getMessage()).thenReturn("Some other error");

        CustomErrorType error = advice.onDataIntegrityViolation(exception);

        assertNotNull(error);
        assertEquals("Erro de integridade de dados. Verifique se há dados duplicados.", error.getMessage());
        assertTrue(error.getErrors().isEmpty());
    }

    @Test
    void testOnDataIntegrityViolationException_withNullCause() {
        DataIntegrityViolationException exception = Mockito.mock(DataIntegrityViolationException.class);

        Mockito.when(exception.getCause()).thenReturn(null);

        CustomErrorType error = advice.onDataIntegrityViolation(exception);

        assertNotNull(error);
        assertEquals("Erro de integridade de dados. Verifique se há dados duplicados.", error.getMessage());
        assertTrue(error.getErrors().isEmpty()); 
    }



    @Test
    void testOnDataIntegrityViolationException_withGenericMessage() {
        DataIntegrityViolationException exception = Mockito.mock(DataIntegrityViolationException.class);
        Throwable cause = Mockito.mock(Throwable.class);
        Mockito.when(exception.getCause()).thenReturn(cause);
        Mockito.when(cause.getMessage()).thenReturn("Some other error");

        CustomErrorType error = advice.onDataIntegrityViolation(exception);

        assertNotNull(error);
        assertEquals("Erro de integridade de dados. Verifique se há dados duplicados.", error.getMessage());
        assertTrue(error.getErrors().isEmpty());
    }
}
