package com.MMR.api_acervo_alexandria.repository;

import com.MMR.api_acervo_alexandria.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido,Long> {
    List<ItemPedido> findByPedidoId(Long pedidoId);

}
