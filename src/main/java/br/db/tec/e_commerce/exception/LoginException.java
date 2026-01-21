package br.db.tec.e_commerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class LoginException extends RuntimeException {

  public LoginException(String message) {
    super(message);

  }

  public LoginException(String message, Throwable cause) {
    super(message);

  }
}
