package org.kakaopay.coffee.api.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kakaopay.coffee.api.menu.MenuEntity;
import org.kakaopay.coffee.api.menu.MenuRepository;
import org.kakaopay.coffee.api.order.request.OrderRequest;
import org.kakaopay.coffee.api.user.UserEntity;
import org.kakaopay.coffee.api.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderService orderService;


    @Nested
    @DisplayName("3. 커피 주문/결제하기 API")
    class OrderAndPay {

        @Test
        @DisplayName("정상 요청")
        void withSuccessCase() throws Exception {
            // given
            List<OrderVo> orderVoList = new ArrayList<>();
            orderVoList.add(
                OrderVo.builder()
                       .menuId(1L)
                       .quantity(1)
                       .build()
            );
            orderVoList.add(
                OrderVo.builder()
                       .menuId(2L)
                       .quantity(2)
                       .build())
            ;
            orderVoList.add(
                OrderVo.builder()
                       .menuId(3L)
                       .quantity(3)
                       .build()
            );

            OrderRequest request = OrderRequest.builder()
                                               .userId(1L)
                                               .orderVos(orderVoList)
                                               .build();

            // when // then
            mockMvc.perform(
                       post("/api/order")
                           .content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   )
                   .andDo(print())
                   .andExpect(status().isOk());


        }

        @Test
        @DisplayName("0개의 주문")
        void withZeroSizeOrder() throws Exception {
            // given
            List<OrderVo> orderVoList = new ArrayList<>();

            OrderRequest request = OrderRequest.builder()
                                               .userId(1L)
                                               .orderVos(orderVoList)
                                               .build();

            // when // then
            mockMvc.perform(
                       post("/api/order")
                           .content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   )
                   .andDo(print())
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.code").value("400"))
                   .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                   .andExpect(jsonPath("$.msg").value("주문은 최소 1개 이상입니다."))
                   .andExpect(jsonPath("$.result").isEmpty());

        }
    }

}