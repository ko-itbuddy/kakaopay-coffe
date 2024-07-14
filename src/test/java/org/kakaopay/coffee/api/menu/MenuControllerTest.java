package org.kakaopay.coffee.api.menu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kakaopay.coffee.api.menu.request.MenuListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("1. 커피메뉴목록조회API")
    class MenuList{
        @Test
        @DisplayName("정상 요청")
        void withSuccessCase() throws Exception {
            // given
            MenuListRequest request = MenuListRequest.builder()
                                                     .page(1)
                                                     .count(10)
                                                     .sort(MenuListSort.NAME_ASC)
                                                     .build();

            // when // then
            mockMvc.perform(
                       get("/api/menus")
                           .content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   )
                   .andDo(print())
                   .andExpect(status().isOk());


        }

        @Test
        @DisplayName("메뉴 조회시 틀린 정렬과 함께 조회")
        void withIncorrectSort() throws Exception {
            // given
            Map<String, String> request = new HashMap<>();
            request.put("page", "1");
            request.put("count", "10");
            request.put("sort", "INCORRECT_SORT");

            // when // then
            mockMvc.perform(
                       get("/api/menus")
                           .content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   )
                   .andDo(print())
                   .andExpect(status().isBadRequest());


        }

        @Test
        @DisplayName("메뉴 조회시 page 음수")
        void withMinusPage() throws Exception {
            // given
            MenuListRequest request = MenuListRequest.builder()
                                                     .page(-1)
                                                     .count(10)
                                                     .build();

            // when // then
            mockMvc.perform(
                       get("/api/menus")
                           .content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   )
                   .andDo(print())
                   .andExpect(status().isBadRequest());


        }

        @Test
        @DisplayName("메뉴 조회시 count 음수")
        void withMinusCount() throws Exception {
            // given
            MenuListRequest request = MenuListRequest.builder()
                                                     .page(1)
                                                     .count(-10)
                                                     .build();

            // when // then
            mockMvc.perform(
                       get("/api/menus")
                           .content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   )
                   .andDo(print())
                   .andExpect(status().isBadRequest());


        }
    }

}