package org.kakaopay.coffee.api.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kakaopay.coffee.api.order.OrderVo;
import org.kakaopay.coffee.api.user.request.UserLoginServiceRequest;
import org.kakaopay.coffee.api.user.request.UserRechargePointServiceRequest;
import org.kakaopay.coffee.api.user.request.UserSignUpServiceRequest;
import org.kakaopay.coffee.api.user.response.UserLoginResponse;
import org.kakaopay.coffee.api.user.response.UserRechargePointResponse;
import org.kakaopay.coffee.db.user.UserEntity;
import org.kakaopay.coffee.db.user.UserJpaManager;
import org.kakaopay.coffee.db.userpointhistory.UserPointHistoryJpaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaManager userJpaManager;

    @Autowired
    private UserPointHistoryJpaManager userPointHistoryJpaManager;

    @AfterEach
    void tearDown() throws Exception {
        userPointHistoryJpaManager.deleteAllInBatch();
        userJpaManager.deleteAllInBatch();
    }

    @Nested
    @DisplayName("2. 포인트 충전하기 API")
    class RechargePoint {

        @Test
        @DisplayName("존재하지 않는 아이디에 포인트 충전")
        void withNotExistUser() throws Exception {
            // give
            UserEntity user1 = createUserEntity("010-1111-1111", "울버린", "123456");
            UserEntity user2 = createUserEntity("010-1234-1234", "매그니토", "123456");
            userJpaManager.saveAll(List.of(user1, user2));

            UserRechargePointServiceRequest request = UserRechargePointServiceRequest.builder()
                                                                                     .point(5000)
                                                                                     .userId(-1L)
                                                                                     .build();

            // when // then
            assertThatThrownBy(() -> userService.rechargeUserPoint(request))
                .isInstanceOf(
                    IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
        }

        @Test
        @DisplayName("정상 충전")
        void withSuccessCase() throws Exception {
            // give
            UserEntity user1 = createUserEntity("010-1111-1111", "울버린", "123456");
            userJpaManager.save(user1);

            UserLoginResponse loginResponse = userService.login(UserLoginServiceRequest.builder()
                                                                         .phone("010-1111-1111").password("123456")
                                                                         .build());

            UserRechargePointServiceRequest request = UserRechargePointServiceRequest.builder()
                                                                                     .point(5000)
                                                                                     .userId(loginResponse.getId())
                                                                                     .build();
            // when
            UserRechargePointResponse response = userService.rechargeUserPoint(request);

            // then
            assertThat(response.getPoint()).isEqualTo(5000);


        }
    }

    @Nested
    @DisplayName("5. 사용자 가입 API (추가)")
    class SignUp {

        @Test
        @DisplayName("이미 존재하는 전화번호")
        void withExistUserPhone() throws Exception {
            // given
            UserEntity user1 = createUserEntity("010-1111-1111", "울버린", "123456");
            userJpaManager.save(user1);

            UserSignUpServiceRequest request = UserSignUpServiceRequest.builder()
                                                                       .phone("010-1111-1111")
                                                                       .name("데드풀")
                                                                       .password("123456")
                                                                       .build();

            // when // then
            assertThatThrownBy(() -> userService.signUp(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입한 전화번호 입니다.");
        }

    }

    @Nested
    @DisplayName("6. 사용자 로그인 API (추가)")
    class Login {

        @Test
        @DisplayName("틀린 비밀번호")
        void withWrongPassword() throws Exception {
            // given
            UserEntity user2 = createUserEntity("010-1234-1234", "매그니토", "123456");
            userJpaManager.saveAll(List.of(user2));

            UserLoginServiceRequest request = UserLoginServiceRequest.builder()
                                                                     .phone("010-1234-1234")
                                                                     .password("000000")
                                                                     .build();

            // when // then
            assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("전화번호와 비밀번호를 다시 확인해주세요.");
        }

        @Test
        @DisplayName("존재하지 않는 전화번호")
        void withWrongPhone() throws Exception {
            // given
            UserEntity user1 = createUserEntity("010-1111-1111", "울버린", "123456");
            UserEntity user2 = createUserEntity("010-1234-1234", "매그니토", "123456");
            userJpaManager.saveAll(List.of(user1, user2));

            UserLoginServiceRequest request = UserLoginServiceRequest.builder()
                                                                     .phone("010-9999-9999")
                                                                     .password("123456")
                                                                     .build();

            // when // then
            assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("전화번호와 비밀번호를 다시 확인해주세요.");
        }

    }

    private UserEntity createUserEntity(String phone, String name, String password) {
        return UserEntity.builder()
                         .phone(phone)
                         .name(name)
                         .password(password)
                         .point(0)
                         .build();
    }

    private OrderVo makeOrderVo(Long menuId, int quantity) {
        return OrderVo.builder()
                      .menuCode(menuId)
                      .quantity(quantity)
                      .build();
    }
}