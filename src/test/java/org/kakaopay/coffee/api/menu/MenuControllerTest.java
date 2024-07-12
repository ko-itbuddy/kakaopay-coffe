package org.kakaopay.coffee.api.menu;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @DisplayName("메뉴 조회시 아무 조건 없이 조회")
    @Test
    void getMenuDefault() throws Exception {
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

    @DisplayName("메뉴 조회시 틀린 정렬과 함께 조회")
    @Test
    void getMenuWithIncorrectSort() throws Exception {
        // given
        Map<String, String> request = new HashMap<>();
        request.put("page", "1");
        request.put("count", "10");
        request.put("sort", "NAME");

        // when // then
        mockMvc.perform(
                   get("/api/menus")
                       .content(objectMapper.writeValueAsString(request))
                       .contentType(MediaType.APPLICATION_JSON)
               )
               .andDo(print())
               .andExpect(status().isBadRequest());


    }

    @DisplayName("메뉴 조회시 page 음수")
    @Test
    void getMenuWithMinusPage() throws Exception {
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

    @DisplayName("메뉴 조회시 count 음수")
    @Test
    void getMenuWithMinusCount() throws Exception {
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