package com.postechfiap.meuhospital.autenticacao.controller;

import com.postechfiap.meuhospital.autenticacao.service.UsuarioService;
import com.postechfiap.meuhospital.contracts.core.UsuarioResponse;
import com.postechfiap.meuhospital.contracts.usuario.PacienteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelos usuários (Consulta, etc.).
 */
@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "Endpoints para consulta e gestão de dados de usuários.")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint protegido: permite a busca de um usuário por ID.
     * Somente Médicos e Enfermeiros podem buscar qualquer usuário. Pacientes só podem buscar a si mesmos.
     * * Regra de autorização:
     * - Permite se a Role for MEDICO ou ENFERMEIRO (busca qualquer um).
     * - OU Permite se o ID do caminho for IGUAL ao ID do usuário autenticado.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID (Acesso Controlado)",
            description = "Retorna detalhes de um usuário. Pacientes só acessam seus próprios dados.")
    @ApiResponse(responseCode = "200", description = "Sucesso. Retorna dados do usuário.")
    @ApiResponse(responseCode = "403", description = "Proibido. Usuário não tem permissão para acessar este ID.")
    @ApiResponse(responseCode = "404", description = "Não encontrado.")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO', 'INTERNAL_SERVICE_ACCESS') or #id == authentication.principal.id")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @Parameter(description = "UUID do usuário a ser buscado.") @PathVariable UUID id) {

        log.info("Requisição GET /usuarios/{} recebida. Iniciando busca.", id);

        UsuarioResponse response = usuarioService.buscarUsuarioPorId(id);

        log.info("Busca por usuário ID {} concluída com sucesso.", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint protegido para listar todos os pacientes cadastrados.
     * Requer autoridade: MÉDICO ou ENFERMEIRO.
     */
    @GetMapping("/pacientes")
    @Operation(summary = "Listar Pacientes",
            description = "Retorna uma lista de todos os pacientes. Acesso restrito.")
    @ApiResponse(responseCode = "200", description = "Sucesso. Retorna a lista de pacientes.")
    @ApiResponse(responseCode = "403", description = "Proibido. Apenas Médicos e Enfermeiros.")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<PacienteResponse>> listarPacientes() {

        log.info("Requisição GET /usuarios/pacientes recebida. Listando pacientes.");

        List<PacienteResponse> response = usuarioService.listarPacientes();

        log.info("Listagem de {} pacientes concluída.", response.size());
        return ResponseEntity.ok(response);
    }
}