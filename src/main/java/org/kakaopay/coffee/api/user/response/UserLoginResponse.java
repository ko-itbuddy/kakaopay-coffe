package org.kakaopay.coffee.api.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kakaopay.coffee.api.user.UserEntity;

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

    public static UserLoginResponse of(UserEntity userEntity) {
        return UserLoginResponse.builder()
                                .id(userEntity.getId())
                                .phone(userEntity.getPhone())
                                .name(userEntity.getName())
                                .point(userEntity.getPointSum())
                                .build();
    }

}
