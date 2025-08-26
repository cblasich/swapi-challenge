package com.starwarsapp.unit.exceptions;

import com.starwarsapp.exceptions.BadRequestException;
import com.starwarsapp.exceptions.ConflictException;
import com.starwarsapp.exceptions.GlobalExceptionHandler;
import com.starwarsapp.exceptions.dto.ApiErrorDTO;
import org.junit.jupiter.api.Test;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Test
    void handleAllOtherExceptions_returnsInternalServerError() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Exception ex = new RuntimeException("Error de prueba");

        ResponseEntity<Map<String, String>> response = handler.handleAllOtherExceptions(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Ocurrio un error inesperado.", response.getBody().get("error"));
    }

    @Test
    void handleNotFound_returnsNotFoundError() throws Exception {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ChangeSetPersister.NotFoundException ex = new ChangeSetPersister.NotFoundException();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/noexiste");

        ResponseEntity<ApiErrorDTO> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("/api/noexiste", response.getBody().getPath());
    }

    @Test
    void handleBadRequest_returnsBadRequestError() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        BadRequestException ex = new BadRequestException("Parámetro inválido");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/bad");

        ResponseEntity<ApiErrorDTO> response = handler.handleBadRequest(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Parámetro inválido", response.getBody().getMessage());
        assertEquals("/api/bad", response.getBody().getPath());
    }

    @Test
    void handleConflict_returnsConflictError() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ConflictException ex = new ConflictException("Conflicto de datos");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/conflict");

        ResponseEntity<ApiErrorDTO> response = handler.handleConflict(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("Conflicto de datos", response.getBody().getMessage());
        assertEquals("/api/conflict", response.getBody().getPath());
    }

    @Test
    void handleValidationErrors_returnsBadRequestWithFieldErrors() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("obj", "campo", "Debe ser positivo");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        /*MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);*/

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/validar");

        ResponseEntity<ApiErrorDTO> response = handler.handleValidationErrors(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Error de validacion", response.getBody().getMessage());
        assertEquals("/api/validar", response.getBody().getPath());
        Map<String, String> fieldErrors = response.getBody().getErrors();
        assertNotNull(fieldErrors);
        assertEquals("Debe ser positivo", fieldErrors.get("campo"));
    }

}