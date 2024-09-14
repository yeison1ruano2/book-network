package com.alibou.book.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name="Authentication")
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ResponseEntity<?> register (@RequestBody @Valid RegistrationRequest request) throws MessagingException {
    service.register(request);
    return ResponseEntity.accepted().build();
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
          @RequestBody @Valid AuthenticationRequest request
  ){
    return ResponseEntity.ok(service.authenticate(request));
  }

  @GetMapping("/activate-account")
  public void confirm(
          @RequestParam String token
  ) throws MessagingException {
    service.activateAccount(token);
  }

  @GetMapping("/current")
  public ResponseEntity<Map<String,String>> getCurrentUsername(Authentication authentication){
    Map<String,String> response = new HashMap<>();
    String username = service.getCurrentUsername(authentication);
    response.put("firstname",username);
    return ResponseEntity.ok(response);
  }
}
