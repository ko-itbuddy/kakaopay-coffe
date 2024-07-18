package org.kakaopay.coffee.api.menu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.kakaopay.coffee.api.common.BaseEntity;
import org.kakaopay.coffee.api.order.OrderMenuEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TB_MENU")
public class MenuEntity extends BaseEntity implements Comparable<MenuEntity>{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long menuKey;

    @Column
    @NotNull
    private Long menuId;

    @Column
    @NotNull
    private String name;

    @Column
    @NotNull
    private Integer inventory;

    @Column
    @NotNull
    private Integer price;

    @ToString.Exclude
    @OneToMany
    @JoinColumn(
        name = "menuKey",
        referencedColumnName = "menuKey",
        insertable = false,
        updatable = false
    )
    private SortedSet<OrderMenuEntity> orderMenus = new TreeSet<>();

    @Override
    public int compareTo(MenuEntity o) {
        return 1;
    }
}
