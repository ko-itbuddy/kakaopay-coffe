package org.kakaopay.coffee.api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.user.UserPointHistoryEntity;

@Getter
@NoArgsConstructor
public class UserRechargePointServiceRequest {

    private Long userId;
    private int point;


    @Builder
    private UserRechargePointServiceRequest(Long userId, int point) {
        this.userId = userId;
        this.point = point;
    }


}
