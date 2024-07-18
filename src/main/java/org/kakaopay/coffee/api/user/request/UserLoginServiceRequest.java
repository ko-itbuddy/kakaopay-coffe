package org.kakaopay.coffee.api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginServiceRequest {

    private String phone;

    private String password;


    @Builder
    private UserLoginServiceRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
