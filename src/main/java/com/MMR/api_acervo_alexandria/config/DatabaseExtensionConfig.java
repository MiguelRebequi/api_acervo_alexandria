package com.MMR.api_acervo_alexandria.config;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseExtensionConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseExtensionConfig.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseExtensionConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public void run(String @NonNull ... args)  {
        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS unaccent");
            log.info("Extensão PostgreSQL 'unaccent' verificada/habilitada com sucesso.");
        } catch (Exception e) {
            log.warn("Não foi possível garantir a extensão 'unaccent' automaticamente: {}", e.getMessage());
        }
    }

}
