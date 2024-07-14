package org.kakaopay.coffee.api.user.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLoginResponse {

    private Long userKey;
    private String phone;
    private String name;

    @Builder
    private UserLoginResponse(Long userKey, String phone, String name) {
        this.userKey = userKey;
        this.phone = phone;
        this.name = name;
    }
}
