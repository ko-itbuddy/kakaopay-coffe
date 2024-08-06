package org.kakaopay.coffee.db.menu;

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
@Table(name = "TB_MENU")
public class MenuEntity extends BaseEntity implements Comparable<MenuEntity>{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long menuKey;

    @Column
    private Long menuId;

    @Column
    private String name;

    @Column
    private Integer inventory;

    @Column
    private Integer price;

    @Builder
    private MenuEntity(Long menuKey, Long menuId, String name, Integer inventory, Integer price,
        SortedSet<OrderMenuEntity> orderMenus) {
        this.menuKey = menuKey;
        this.menuId = menuId;
        this.name = name;
        this.inventory = inventory;
        this.price = price;
        this.orderMenus = orderMenus;
    }

    @ToString.Exclude
    @OneToMany
    @JoinColumn(
        name = "menuKey",
        referencedColumnName = "menuKey",
        insertable = false,
        updatable = false,
        foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private SortedSet<OrderMenuEntity> orderMenus = new TreeSet<>();

    @Override
    public int compareTo(MenuEntity o) {
        return 1;
    }

    public void decreaseInventory(Integer val){
        if(this.inventory < val){
            throw new IllegalArgumentException("재고가 충분히자 않습니다. 상품재고 : {}, val : {}");
        }
        this.inventory = this.inventory - val;
    }
    public void increaseInventory(Integer val){
        this.inventory = this.inventory + val;
    }
}
