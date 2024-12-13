package com.example.slideshow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class IllegalMediaTypeException extends RuntimeException {

  public IllegalMediaTypeException(String message) {
    super(message);
  }

}
