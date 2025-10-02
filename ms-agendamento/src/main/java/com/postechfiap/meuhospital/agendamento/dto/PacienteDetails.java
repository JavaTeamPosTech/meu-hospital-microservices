package com.postechfiap.meuhospital.agendamento.dto;

import com.postechfiap.meuhospital.contracts.core.Role;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO interno para mapear a resposta completa do ms-autenticacao (UsuarioResponse).
 * Deve espelhar fielmente a estrutura do UsuarioResponse para evitar erros de deserialização.
 */
public record PacienteDetails(
        UUID id,
        String nome,
        String cpf,
        String email,
        String telefone,
        Role role,
        String numeroRegistro,
        String especialidade,
        LocalDate dataNascimento
) {}