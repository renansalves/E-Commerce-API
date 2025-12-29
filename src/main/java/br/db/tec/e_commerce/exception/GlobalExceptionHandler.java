package br.db.tec.e_commerce.exception;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.db.tec.e_commerce.dto.handler.ErrorResponseDTO;
import br.db.tec.e_commerce.dto.handler.ValidationErrorDTO;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponseDTO> handleNotFound(EntityNotFoundException ex) {
    logger.error("Entity Not Found: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponseDTO(ex.getMessage(), "RESOURCE_NOT_FOUND", OffsetDateTime.now()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ValidationErrorDTO>> handleValidationErrors(MethodArgumentNotValidException ex) {
    logger.error("Validation Error: {}", ex.getMessage(), ex);
    var errors = ex.getFieldErrors().stream()
        .map(f -> new ValidationErrorDTO(f.getField(), f.getDefaultMessage()))
        .toList();
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDTO> handleBadRequest(IllegalArgumentException ex) {
    logger.error("Bad Request: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponseDTO(ex.getMessage(), "BAD_REQUEST", OffsetDateTime.now()));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDTO> handleAccessDenied(AccessDeniedException ex) {
    logger.error("Unauthorized acssess: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(new ErrorResponseDTO("Acesso não autorizado", "FORBIDDEN", OffsetDateTime.now()));
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponseDTO> handleBusiness(BusinessException ex) {
    logger.error("Business Exception: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponseDTO("Erro de negocio.", "BUSINESSEXCEPTION", OffsetDateTime.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneralError(Exception ex) {
    logger.error("Internal Server Error: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponseDTO("Erro interno no servidor", "INTERNAL_SERVER_ERROR", OffsetDateTime.now()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
    logger.error("RuntimeException caught: {}", ex.getMessage(), ex);
    java.io.StringWriter sw = new java.io.StringWriter();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sw.toString());
  }
}
