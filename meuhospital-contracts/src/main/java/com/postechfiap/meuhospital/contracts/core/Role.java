package com.postechfiap.meuhospital.contracts.core;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enum que define os perfis de acesso do usuário no sistema.
 * Implementa GrantedAuthority para integração direta com o Spring Security.
 */
public enum Role implements GrantedAuthority {
    MEDICO("Medico"),
    ENFERMEIRO("Enfermeiro"),
    PACIENTE("Paciente");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    /**
     * Retorna o nome da autoridade, formatado como "ROLE_NOME".
     * O Spring Security espera o prefixo "ROLE_" para mapear autoridades.
     * @return String com a autoridade no formato ROLE_NOME.
     */
    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
