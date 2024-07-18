package org.kakaopay.coffee.api.user.response;

import lombok.Builder;
import lombok.Getter;
import org.kakaopay.coffee.api.user.UserEntity;

@Getter
public class UserRechargePointResponse {

    private int point;

    @Builder
    private UserRechargePointResponse(int point) {
        this.point = point;
    }

    public static UserRechargePointResponse of(UserEntity userEntity){
        return UserRechargePointResponse.builder()
                                        .point(userEntity.getPointSum())
                                        .build();
    }
}
