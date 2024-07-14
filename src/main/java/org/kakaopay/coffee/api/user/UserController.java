package org.kakaopay.coffee.api.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.kakaopay.coffee.api.common.ApiResponse;
import org.kakaopay.coffee.api.user.request.UserSignUpRequest;
import org.kakaopay.coffee.api.user.request.UserLoginRequest;
import org.kakaopay.coffee.api.user.request.UserRechargePointRequest;
import org.kakaopay.coffee.api.user.response.UserLoginResponse;
import org.kakaopay.coffee.api.user.response.UserRechargePointResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {


    /*
     * 2. 포인트 충전하기 API
     * */
    @PostMapping("api/users/point")
    public ApiResponse<UserRechargePointResponse> rechargeUserPoint(
        @Valid @RequestBody UserRechargePointRequest request) {

        UserRechargePointResponse result = UserRechargePointResponse.builder().point(5000).build();

        return ApiResponse.of(HttpStatus.OK, result);
    }


    /*
     * 5. 사용자 가입 API (추가)
     * */
    @PostMapping("api/users")
    public ApiResponse<Object> signUp(
        @Valid @RequestBody UserSignUpRequest request) throws BadRequestException {

        if(request.getPhone().equals("010-1111-1111"))
            throw new BadRequestException("이미 가입한 전화번호 입니다");

        return ApiResponse.of(HttpStatus.OK, null);
    }

    /*
     * 6. 사용자 로그인 API (추가)
     * */
    @PostMapping("api/users/login")
    public ApiResponse<UserLoginResponse> login(
        @Valid @RequestBody UserLoginRequest request) {

        UserLoginResponse result = UserLoginResponse.builder()
                                                    .phone("010-1234-1234")
                                                    .name("데드풀")
                                                    .userKey(1L)
                                                    .build();

        return ApiResponse.of(HttpStatus.OK, result);
    }

}
