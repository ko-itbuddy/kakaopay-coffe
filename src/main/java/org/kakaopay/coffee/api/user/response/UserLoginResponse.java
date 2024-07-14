package org.kakaopay.coffee.api.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginResponse {

    private Long id;
    private String phone;
    private String name;
    private int point;

    @Builder
    private UserLoginResponse(Long id, String phone, String name, int point) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.point = point;
    }

}
