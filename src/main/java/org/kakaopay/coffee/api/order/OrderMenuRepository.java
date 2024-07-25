package org.kakaopay.coffee.api.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMenuRepository extends JpaRepository<OrderMenuEntity, Long> {

    List<OrderMenuEntity> findAllByOrderId(Long orderId);
}
