package org.kakaopay.coffee.api.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
