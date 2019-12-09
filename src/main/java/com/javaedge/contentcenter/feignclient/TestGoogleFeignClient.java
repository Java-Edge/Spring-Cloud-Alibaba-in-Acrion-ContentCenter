package com.javaedge.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 脱离ribbon的使用
 *
 * @author JavaEdge
 */
@FeignClient(name = "google", url = "http://www.google.com")
public interface TestGoogleFeignClient {

    @GetMapping("")
    String index();
}
