package org.kakaopay.coffee.api.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.common.BaseEntity;
import org.kakaopay.coffee.api.user.request.UserRechargePointServiceRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TB_POINT_HISTORY")
public class UserPointHistoryEntity extends BaseEntity implements
    Comparable<UserPointHistoryEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    @NotNull
    private Long userId;

    @Column
    @NotNull
    private Integer point;

    @Builder
    private UserPointHistoryEntity(Long userId, Integer point) {
        this.userId = userId;
        this.point = point;
    }

    public static UserPointHistoryEntity of(UserRechargePointServiceRequest request) {
        return UserPointHistoryEntity.builder()
                                     .userId(request.getUserId())
                                     .point(request.getPoint())
                                     .build();
    }

    @Override
    public int compareTo(UserPointHistoryEntity o) {
        return 1;
    }
}
