package org.kakaopay.coffee.api.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MenuController {

    @GetMapping("api/menus")
    public MenuResponse getMenu(@RequestBody MenuGetRequest request) {
        return null;
    }

}
