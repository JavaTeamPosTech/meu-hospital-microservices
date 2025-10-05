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

import com.postechfiap.meuhospital.autenticacao.exception.RecursoNaoEncontradoException;
import com.postechfiap.meuhospital.autenticacao.mapper.UsuarioMapper;
import com.postechfiap.meuhospital.autenticacao.security.JwtService;
import com.postechfiap.meuhospital.autenticacao.service.UsuarioService;
import com.postechfiap.meuhospital.contracts.core.LoginRequest;
import com.postechfiap.meuhospital.contracts.core.LoginResponse;
import com.postechfiap.meuhospital.contracts.core.UsuarioRegisterRequest;
import com.postechfiap.meuhospital.contracts.core.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller responsável pelos endpoints de autenticação e geração de tokens.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação e Cadastro", description = "Endpoints de acesso público para login e criação de contas.")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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
     */
    @PostMapping("/register")
    @Operation(summary = "Cadastro de Novo Usuário",
            description = "Cria uma nova conta (Médico, Enfermeiro ou Paciente), criptografando a senha.")
    @ApiResponse(responseCode = "201", description = "Usuário criado e persistido. Retorna dados do usuário.")
    @ApiResponse(responseCode = "400", description = "Falha na validação de campos ou regras de negócio (e.g., campo obrigatório ausente).")
    @ApiResponse(responseCode = "409", description = "Conflito. E-mail ou CPF já cadastrado.")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioRegisterRequest request) {
        log.info("INICIANDO: POST /auth/register para E-mail: {} e Role: {}", request.email(), request.role());

        UsuarioResponse response = usuarioService.criarUsuario(request);

        log.info("SUCESSO: Usuário ID {} criado.", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint de login que gera um Token JWT válido.
     */
    @PostMapping("/login")
    @Operation(summary = "Login e Geração de JWT",
            description = "Autentica o usuário com e-mail e senha e retorna um Token JWT.")
    @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida. Retorna o token e os dados do usuário.")
    @ApiResponse(responseCode = "401", description = "Não Autorizado. Credenciais inválidas.")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("INICIANDO: POST /auth/login para E-mail: {}", request.email());

        // 1. Autenticação (Verificação de senha)
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        Authentication authentication = authenticationManager.authenticate(authToken);

        // 2. Geração do Token
        String token = jwtService.generateToken(authentication);
        log.debug("JWT gerado para o usuário: {}", request.email());

        // 3. Busca de Detalhes para Resposta
        UsuarioResponse usuarioResponse = usuarioService.buscarUsuarioPorEmail(request.email())
                .map(usuarioMapper::toResponse)
                .orElseThrow(() -> {
                    log.error("ERRO GRAVE: Usuário autenticado ({}) não encontrado no banco.", request.email());
                    return new RecursoNaoEncontradoException("Usuário não encontrado após autenticação.");
                });

        log.info("SUCESSO: Login concluído. JWT e dados do usuário retornados.");
        return ResponseEntity.ok(new LoginResponse(token, usuarioResponse));
    }
}