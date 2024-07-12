package org.kakaopay.coffee.api.order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.kakaopay.coffee.api.order.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

    @Nested
    @DisplayName("3. 커피 주문/결제하기 API")
    class OrderAndPay {

        @Test
        @DisplayName("정상 요청")
        void withSuccessCase() throws Exception {
            // given

            List<Order> orderList = new ArrayList<>();
            orderList.add(
                Order.builder()
                     .menuId(1L)
                     .quantity(1)
                     .build()
            );
            orderList.add(
                Order.builder()
                     .menuId(2L)
                     .quantity(2)
                     .build())
            ;
            orderList.add(
                Order.builder()
                     .menuId(3L)
                     .quantity(3)
                     .build()
            );

            OrderRequest request = OrderRequest.builder()
                                               .userPhone("010-1111-1111")
                                               .orders(orderList)
                                               .build();

            // when // then
            mockMvc.perform(
                       get("/api/order")
                           .content(objectMapper.writeValueAsString(request))
                           .contentType(MediaType.APPLICATION_JSON)
                   )
                   .andDo(print())
                   .andExpect(status().isOk());


        }
    }

}