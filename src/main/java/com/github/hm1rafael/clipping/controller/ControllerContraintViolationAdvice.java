package com.github.hm1rafael.clipping.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerContraintViolationAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ResponseEntity<Void> inputValidationException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


}
