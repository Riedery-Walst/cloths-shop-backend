package ru.andreev.clothsshop.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreev.clothsshop.dto.LoginRequestDTO;
import ru.andreev.clothsshop.dto.LoginResponseDTO;
import ru.andreev.clothsshop.util.JwtTokenUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Логин и получение JWT токена
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtTokenUtil.generateToken(userDetails.getUsername());

            return new LoginResponseDTO(jwt);

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid login credentials");
        }
    }
}