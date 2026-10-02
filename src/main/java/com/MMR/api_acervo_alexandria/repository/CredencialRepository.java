package com.MMR.api_acervo_alexandria.repository;

import com.MMR.api_acervo_alexandria.model.Credencial;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredencialRepository extends CrudRepository<Credencial, Long> {
}
