package com.postechfiap.meuhospital.autenticacao.controller;

import com.postechfiap.meuhospital.autenticacao.exception.RecursoNaoEncontradoException;
import com.postechfiap.meuhospital.autenticacao.mapper.UsuarioMapper;
import com.postechfiap.meuhospital.autenticacao.security.JwtService;
import com.postechfiap.meuhospital.autenticacao.service.UsuarioService;
import com.postechfiap.meuhospital.contracts.core.LoginRequest;
import com.postechfiap.meuhospital.contracts.core.LoginResponse;
import com.postechfiap.meuhospital.contracts.core.UsuarioRegisterRequest;
import com.postechfiap.meuhospital.contracts.core.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelos endpoints de autenticação e geração de tokens.
 * A rota /login deve ser permitida publicamente no SecurityConfig.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    /**
     * Endpoint público para cadastro de novos usuários.
     * Não requer autenticação.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioRegisterRequest request) {
        UsuarioResponse response = usuarioService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint de login que gera um Token JWT válido.
     * @param request DTO com email e senha.
     * @return LoginResponse contendo o JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());

        Authentication authentication = authenticationManager.authenticate(authToken);

        String token = jwtService.generateToken(authentication);

        UsuarioResponse usuarioResponse = usuarioService.buscarUsuarioPorEmail(request.email())
                .map(usuarioMapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado após autenticação."));

        return ResponseEntity.ok(new LoginResponse(token, usuarioResponse));
    }
}