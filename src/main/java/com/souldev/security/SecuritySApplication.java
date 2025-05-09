package com.souldev.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.crypto.SecretKey;
import java.util.Base64;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class SecuritySApplication {

	public static void main(String[] args) {
		//SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
		// Encoder en Base64
//		String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
//		System.out.println("Clé secrète Base64 : " + base64Key);
//		System.out.println("Longueur décodée : " + Base64.getDecoder().decode(base64Key).length + " octets");

		SpringApplication.run(SecuritySApplication.class, args);
	}

}
