package org.kakaopay.coffee.api.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserRechargePointResponse {

    private int point;

    @Builder
    private UserRechargePointResponse(int point) {
        this.point = point;
    }
}
