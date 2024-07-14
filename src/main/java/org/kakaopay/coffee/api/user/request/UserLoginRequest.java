package org.kakaopay.coffee.api.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequest {

    @NotNull(message = "휴대폰 번호는 필수값입니다.")
    @Pattern(regexp = "^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$", message = "전화번호 양식에 맞지 않습니다.")
    private String phone;

    @NotNull(message = "비밀번호는 필수값입니다.")
    @Pattern(regexp = "^[0-9]{6}$", message = "비밀번호는 숫자 6자리 입니다.")
    private String password;


    @Builder
    private UserLoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
