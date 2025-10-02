package com.postechfiap.meuhospital.agendamento.dto;

import java.util.UUID;

/**
 * DTO interno para mapear a resposta simplificada do ms-autenticacao (UsuarioResponse).
 */
public record PacienteDetails(
        UUID id,
        String nome,
        String cpf,
        String email,
        String telefone
) {}