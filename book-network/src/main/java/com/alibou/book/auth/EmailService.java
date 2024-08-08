package com.alibou.book.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  private final SpringTemplateEngine templateEngine;

  public void sendEmail(
          String to,
          String username,
          EMailTemplateName emailTemplate,
          String confirmationUrl,
          String activationCode,
          String subject
  ){}

}
