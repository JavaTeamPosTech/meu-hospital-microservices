package com.postechfiap.meuhospital.autenticacao.controller;

import com.postechfiap.meuhospital.autenticacao.service.UsuarioService;
import com.postechfiap.meuhospital.contracts.core.UsuarioResponse;
import com.postechfiap.meuhospital.contracts.usuario.PacienteResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelos usuários (Consulta, etc.).
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Endpoint protegido: permite a busca de um usuário por ID.
     * Somente Médicos e Enfermeiros podem buscar qualquer usuário. Pacientes só podem buscar a si mesmos.
     * * Regra de autorização:
     * - Permite se a Role for MEDICO ou ENFERMEIRO (busca qualquer um).
     * - OU Permite se o ID do caminho (#id) for IGUAL ao ID do usuário autenticado (authentication.principal.id).
     */
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        UsuarioResponse response = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * NOVO: Endpoint protegido para listar todos os pacientes cadastrados.
     * Requer autoridade: MÉDICO ou ENFERMEIRO.
     */
    @GetMapping("/pacientes")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'ENFERMEIRO')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<PacienteResponse>> listarPacientes() {
        List<PacienteResponse> response = usuarioService.listarPacientes();
        return ResponseEntity.ok(response);
    }


}