package org.kakaopay.coffee.api.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.common.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TB_ORDER_MENU")
public class OrderMenuEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private Long orderId;

    @Column
    private Long userId;

    @Column
    private Long menuKey;

    @Column
    private int orderSequence;

    @Column
    private int quantity;


    @Builder
    private OrderMenuEntity(Long userId, Long menuKey, int orderSequence, int quantity) {
        this.userId = userId;
        this.menuKey = menuKey;
        this.orderSequence = orderSequence;
        this.quantity = quantity;
    }


    public static OrderMenuEntity of(OrderVo orderVo, Long userId, int orderSequence) {
        return OrderMenuEntity.builder()
                              .userId(userId)
                              .menuKey(orderVo.getMenuId())
                              .orderSequence(orderSequence)
                              .quantity(orderVo.getQuantity())
                              .build();
    }
}
