package org.kakaopay.coffee.db.order;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.kakaopay.coffee.db.common.BaseEntity;
import org.kakaopay.coffee.db.ordermenu.OrderMenuEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@ToString
@Table(name = "TB_ORDER")
public class OrderEntity extends BaseEntity implements Comparable<OrderEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private Long userId;

    @Builder
    private OrderEntity(Long userId) {
        this.userId = userId;
    }

    @ToString.Exclude
    @OneToMany
    @JoinColumn(
        name = "orderId",
        referencedColumnName = "id",
        insertable = false,
        updatable = false,
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private SortedSet<OrderMenuEntity> orderMenus = new TreeSet<>();

    @Override
    public int compareTo(OrderEntity o) {
        return 1;
    }
}
