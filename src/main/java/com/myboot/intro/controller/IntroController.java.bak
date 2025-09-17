package com.myboot.intro.controller;

import com.myboot.intro.model.Restaurant;
import com.myboot.intro.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IntroController {

    private final RestaurantService restaurantService;

    // JSP 반환
    @GetMapping("/intro")
    public String intro(Model model) {

        List<Restaurant> randomRestaurants = restaurantService.getRandomRestaurants(3);
        model.addAttribute("randomRestaurants", randomRestaurants);
        return "intro/intro"; // /WEB-INF/views/intro/intro.jsp
    }

    // 검색 API (JSON 반환)
    @GetMapping("/intro/search")
    @ResponseBody
    public Page<Restaurant> search(@RequestParam("region") String region, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return restaurantService.findByRegion(region, PageRequest.of(page, size));
    }

}
