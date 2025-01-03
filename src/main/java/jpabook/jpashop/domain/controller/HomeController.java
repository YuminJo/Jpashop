package jpabook.jpashop.domain.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j // Log를 사용하기 위한 Annotation
public class HomeController {
    
    @RequestMapping("/")
    public String home() {
        log.info("home controller");
        return "home";
    }
}
