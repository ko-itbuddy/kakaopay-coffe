package org.kakaopay.coffee.api.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kakaopay.coffee.api.user.request.UserLoginRequest;
import org.kakaopay.coffee.api.user.request.UserRechargePointRequest;
import org.kakaopay.coffee.api.user.request.UserSignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("2. 포인트 충전하기 API")
    class RechargePoint {

        @Test
        @DisplayName("정상 요청")
        void withSuccessCase() throws Exception {
            // given
            UserRechargePointRequest request = UserRechargePointRequest.builder()
                                                                       .point(5000)
                                                                       .userKey(1L).build();

            // when
            mockMvc.perform(
                       post("/api/users/point").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isOk());

            // then
        }

        @Test
        @DisplayName("음수로 포인트 충전")
        void withMinusPoint() throws Exception {
            // given
            UserRechargePointRequest request = UserRechargePointRequest.builder()
                                                                       .point(-5000)
                                                                       .userKey(1L).build();

            // when
            mockMvc.perform(
                       post("/api/users/point").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest());

            // then
        }

        @Test
        @DisplayName("존재하지 않는 아이디에 포인트 충전")
        void withNotExistUser() throws Exception {
            // give
            UserRechargePointRequest request = UserRechargePointRequest.builder()
                                                                       .point(5000)
                                                                       .userKey(-1L).build();

            // when
            mockMvc.perform(
                       post("/api/users/point").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest());

            // then
        }


    }

    @Nested
    @DisplayName("5. 사용자 가입 API (추가)")
    class SignUp {

        @Test
        @DisplayName("정상 요청")
        void withSuccessCase() throws Exception {
            // given
            UserSignUpRequest request = UserSignUpRequest.builder()
                                                         .phone("010-1234-1234")
                                                         .name("데드풀")
                                                         .password("123456")
                                                         .build();

            // when // then
            mockMvc.perform(
                       post("/api/users").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isOk());


        }

        @Test
        @DisplayName("이미 존재하는 전화번호")
        void withExistUserPhone() throws Exception {
            // given
            UserSignUpRequest request = UserSignUpRequest.builder()
                                                         .phone("010-1111-1111")
                                                         .name("데드풀")
                                                         .password("123456")
                                                         .build();

            // when // then
            mockMvc.perform(
                       post("/api/users").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("틀린 전화번호")
        void withIncorrectPhone() throws Exception {
            // given
            UserSignUpRequest request = UserSignUpRequest.builder()
                                                         .phone("010-11234")
                                                         .name("데드풀")
                                                         .password("123456")
                                                         .build();

            // when // then
            mockMvc.perform(
                       post("/api/users").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("2글자 이하의 이름")
        void withIncorrectNameUnderSize2() throws Exception {
            // given
            UserSignUpRequest request = UserSignUpRequest.builder()
                                                         .phone("010-11234")
                                                         .name("데")
                                                         .password("123456")
                                                         .build();

            // when // then
            mockMvc.perform(
                       post("/api/users").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest());

        }

        @Test
        @DisplayName("15글자 이상의 이름")
        void withIncorrectNameOverSize15() throws Exception {
            // given
            UserSignUpRequest request = UserSignUpRequest.builder()
                                                         .phone("010-11234")
                                                         .name("1234567890123456")
                                                         .password("123456")
                                                         .build();

            // when // then
            mockMvc.perform(
                       post("/api/users").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest());

        }

    }

    @Nested
    @DisplayName("6. 사용자 로그인 API (추가)")
    class Login {

        @Test
        @DisplayName("정상 요청")
        void withSuccessCase() throws Exception {
            // given
            UserLoginRequest request = UserLoginRequest.builder()
                                                       .phone("010-1234-1234")
                                                       .password("123456")
                                                       .build();

            // when // then
            mockMvc.perform(
                       post("/api/users/login").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isOk());


        }

        @Test
        @DisplayName("틀린 비밀번호")
        void withWrongPassword() throws Exception {
            // given
            UserLoginRequest request = UserLoginRequest.builder()
                                                       .phone("010-1234-1234")
                                                       .password("000000")
                                                       .build();

            // when // then
            mockMvc.perform(
                       post("/api/users/login").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest());


        }

        @Test
        @DisplayName("존재하지 않는 전화번호")
        void withWrongPhone() throws Exception {
            // given
            UserLoginRequest request = UserLoginRequest.builder()
                                                       .phone("010-9999-9999")
                                                       .password("123456")
                                                       .build();

            // when // then
            mockMvc.perform(
                       post("/api/users/login").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest());


        }

    }

}