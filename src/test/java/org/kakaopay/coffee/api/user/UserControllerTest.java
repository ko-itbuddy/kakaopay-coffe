package org.kakaopay.coffee.api.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kakaopay.coffee.api.user.request.UserLoginRequest;
import org.kakaopay.coffee.api.user.request.UserRechargePointRequest;
import org.kakaopay.coffee.api.user.request.UserSignUpRequest;
import org.kakaopay.coffee.api.user.response.UserLoginResponse;
import org.kakaopay.coffee.api.user.response.UserRechargePointResponse;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    UserService userService;

    @Nested
    @DisplayName("2. 포인트 충전하기 API")
    class RechargePoint {

        @Test
        @DisplayName("정상 요청")
        void withSuccessCase() throws Exception {
            // given
            UserRechargePointRequest request = UserRechargePointRequest.builder()
                                                                       .point(5000)
                                                                       .userId(1L).build();

            // when
            mockMvc.perform(
                       post("/api/users/point").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value("200"))
                   .andExpect(jsonPath("$.status").value("OK"))
                   .andExpect(jsonPath("$.msg").value("OK"))
                   .andExpect(jsonPath("$.result").isEmpty())

            ;

            // then
        }

        @Test
        @DisplayName("음수로 포인트 충전")
        void withMinusPoint() throws Exception {
            // given
            UserRechargePointRequest request = UserRechargePointRequest.builder()
                                                                       .point(-5000)
                                                                       .userId(1L).build();

            // when
            mockMvc.perform(
                       post("/api/users/point").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.code").value("400"))
                   .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                   .andExpect(jsonPath("$.msg").value("포인트 충전은 양수만 가능합니다."))
                   .andExpect(jsonPath("$.result").isEmpty())
            ;

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
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value("200"))
                   .andExpect(jsonPath("$.status").value("OK"))
                   .andExpect(jsonPath("$.msg").value("OK"))
                   .andExpect(jsonPath("$.result").isEmpty())
            ;


        }


        @Test
        @DisplayName("틀린 양식의 전화번호")
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
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.code").value("400"))
                   .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                   .andExpect(jsonPath("$.msg").value("전화번호 양식에 맞지 않습니다."))
                   .andExpect(jsonPath("$.result").isEmpty());

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


        @Test
        @DisplayName("비밀번호 8자리 입력")
        void withOverDigitPassword() throws Exception {
            // given
            UserSignUpRequest request = UserSignUpRequest.builder()
                                                         .phone("010-1234-1234")
                                                         .name("데드풀")
                                                         .password("12345678")
                                                         .build();

            // when // then
            mockMvc.perform(
                       post("/api/users").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.code").value("400"))
                   .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                   .andExpect(jsonPath("$.msg").value("비밀번호는 숫자 6자리 입니다."));
        }

        @Test
        @DisplayName("비밀번호 4자리 입력")
        void withUnderDigitPassword() throws Exception {
            // given
            UserSignUpRequest request = UserSignUpRequest.builder()
                                                         .phone("010-1234-1234")
                                                         .name("데드풀")
                                                         .password("1234")
                                                         .build();

            // when // then
            mockMvc.perform(
                       post("/api/users").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.code").value("400"))
                   .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                   .andExpect(jsonPath("$.msg").value("비밀번호는 숫자 6자리 입니다."));
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
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.code").value("200"))
                   .andExpect(jsonPath("$.status").value("OK"))
                   .andExpect(jsonPath("$.msg").value("OK"));
        }

        @Test
        @DisplayName("비밀번호 8자리 입력")
        void withOverDigitPassword() throws Exception {
            // given
            UserLoginRequest request = UserLoginRequest.builder()
                                                       .phone("010-1234-1234")
                                                       .password("12345678")
                                                       .build();

            // when // then
            mockMvc.perform(
                       post("/api/users/login").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.code").value("400"))
                   .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                   .andExpect(jsonPath("$.msg").value("비밀번호는 숫자 6자리 입니다."));
        }

        @Test
        @DisplayName("비밀번호 4자리 입력")
        void withUnderDigitPassword() throws Exception {
            // given
            UserLoginRequest request = UserLoginRequest.builder()
                                                       .phone("010-1234-1234")
                                                       .password("1234")
                                                       .build();

            // when // then
            mockMvc.perform(
                       post("/api/users/login").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.code").value("400"))
                   .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                   .andExpect(jsonPath("$.msg").value("비밀번호는 숫자 6자리 입니다."));
        }

        @Test
        @DisplayName("전화번호 양식 틀림 1")
        void withWrongPhone1() throws Exception {
            // given
            UserLoginRequest request = UserLoginRequest.builder()
                                                       .phone("010-1234444-1234")
                                                       .password("123456")
                                                       .build();

            // when // then
            mockMvc.perform(
                       post("/api/users/login").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.code").value("400"))
                   .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                   .andExpect(jsonPath("$.msg").value("전화번호 양식에 맞지 않습니다."));
        }

        @Test
        @DisplayName("전화번호 양식 틀림 2")
        void withWrongPhone2() throws Exception {
            // given
            UserLoginRequest request = UserLoginRequest.builder()
                                                       .phone("01000-123-1234")
                                                       .password("123456")
                                                       .build();

            // when // then
            mockMvc.perform(
                       post("/api/users/login").
                           content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   ).andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.code").value("400"))
                   .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                   .andExpect(jsonPath("$.msg").value("전화번호 양식에 맞지 않습니다."));
        }

    }

}