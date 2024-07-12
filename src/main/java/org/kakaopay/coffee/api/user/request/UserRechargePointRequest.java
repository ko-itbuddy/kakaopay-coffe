package org.kakaopay.coffee.api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRechargePointRequest {

    @NotNull(message = "userKey는 필수값입니다.")
    private Long userKey;

    @NotNull(message = "포인트는 필수값입니다.")
    @Positive(message = "포인트 충전은 양수만 가능합니다.")
    private int point;


    @Builder
    private UserRechargePointRequest(Long userKey, int point) {
        this.userKey = userKey;
        this.point = point;
    }
}
