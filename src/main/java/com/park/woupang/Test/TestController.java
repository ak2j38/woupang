package com.park.woupang.Test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping(value = "/")
    public TestDto test() {
        TestDto testDto = new TestDto();
        testDto.setAge(29);
        testDto.setName("Park");

        return testDto;
    }
}
