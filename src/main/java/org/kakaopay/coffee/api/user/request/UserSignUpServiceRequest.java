package org.kakaopay.coffee.api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpServiceRequest {

    private String phone;
    private String name;
    private String password;

    @Builder
    private UserSignUpServiceRequest(String phone, String name, String password) {
        this.phone = phone;
        this.name = name;
        this.password = password;
    }
}
