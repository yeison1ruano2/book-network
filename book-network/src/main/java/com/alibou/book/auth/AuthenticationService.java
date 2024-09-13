package com.alibou.book.auth;

import com.alibou.book.role.RoleRepository;
import com.alibou.book.security.JwtService;
import com.alibou.book.user.Token;
import com.alibou.book.user.TokenRepository;
import com.alibou.book.user.User;
import com.alibou.book.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final RoleRepository roleRepository;

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final TokenRepository tokenRepository;

  private final EmailService emailService;

  private final AuthenticationManager authenticationManager;

  private final JwtService jwtService;

  @Value("${application.mailing.frontend.activation-url}")
  private String activationUrl;

  public void register(RegistrationRequest request) throws MessagingException {
    var userRole = roleRepository.findByName("USER")
            .orElseThrow(()-> new IllegalStateException("Rol de usuario no inicializado"));
    var user = User.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .accountLocked(false)
            .enabled(false)
            .roles(List.of(userRole))
            .build();
    userRepository.save(user);
    sendValidationEmail(user);
  }

  private void sendValidationEmail(User user) throws MessagingException {
    var newToken = generatedAndSaveActivationToken(user);
    //send email
    emailService.sendEmail(
            user.getEmail(),
            user.fullName(),
            EMailTemplateName.ACTIVATE_ACCOUNT,
            activationUrl,
            newToken,
            "Activacion de cuenta"
    );
  }

  private String generatedAndSaveActivationToken(User user) {
    String generatedToken=generateActivation(6);
    var token = Token.builder()
            .token(generatedToken)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(15))
            .user(user)
            .build();
    tokenRepository.save(token);
    return generatedToken;
  }

  private String generateActivation(int length) {
    String characters = "0123456789";
    StringBuilder codeBuilder = new StringBuilder();
    SecureRandom secureRandom =  new SecureRandom();
    for(int i=0;i<length; i++){
      int randomIndex = secureRandom.nextInt(characters.length());
      codeBuilder.append(characters.charAt(randomIndex));
    }
    return codeBuilder.toString();
  }

  public AuthenticationResponse authenticate (AuthenticationRequest request){
    var auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
    );
    var claims = new HashMap<String,Object>();
    var user = ((User) auth.getPrincipal());
    claims.put("fullName",user.fullName());
    var jwtToken = jwtService.generateToken(claims,user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  @Transactional
  public void activateAccount(String token) throws MessagingException {
    Token savedToken = tokenRepository.findByToken(token).orElseThrow(()-> new RuntimeException("Token invalido"));
    if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
      sendValidationEmail(savedToken.getUser());
      throw  new RuntimeException("El token ha expirado, vuelve a intentarlo con otro token");
    }
    var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado"));
    user.setEnabled(true);
    userRepository.save(user);
    savedToken.setValidatedAt(LocalDateTime.now());
    tokenRepository.save(savedToken);
  }

  public String getCurrentUsername(Authentication authentication){
    if(authentication != null && authentication.isAuthenticated()){
      User user = ((User)authentication.getPrincipal());
      return user.name();
    }
    return "Desconocido";
  }
}
