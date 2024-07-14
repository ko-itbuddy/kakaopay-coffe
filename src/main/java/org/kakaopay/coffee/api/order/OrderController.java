package org.kakaopay.coffee.api.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kakaopay.coffee.api.common.ApiResponse;
import org.kakaopay.coffee.api.order.request.OrderRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class OrderController {


    /*
    * 3. 커피 주문/결제 하기 API
    * */
    @PostMapping("api/order")
    public ApiResponse<Object> order(@Valid @RequestBody OrderRequest request){

        return ApiResponse.of(HttpStatus.OK, null);
    }

}
