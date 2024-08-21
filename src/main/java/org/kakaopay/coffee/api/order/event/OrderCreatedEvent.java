package org.kakaopay.coffee.api.order.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.kakaopay.coffee.api.order.OrderVo;
import org.kakaopay.coffee.db.order.OrderEntity;
import org.springframework.data.domain.jaxb.SpringDataJaxb.OrderDto;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderCreatedEvent {
    OrderEntity order;
}

