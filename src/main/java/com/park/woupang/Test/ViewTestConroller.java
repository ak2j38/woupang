package com.park.woupang.Test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewTestConroller {

    @GetMapping(value = "/bootstrap")
    public String viewTest(Model model) {
        model.addAttribute("title", "Woupang");
        return "/index";
    }

}
