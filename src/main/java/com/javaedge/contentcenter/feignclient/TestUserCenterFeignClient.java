package com.javaedge.contentcenter.feignclient;

import com.javaedge.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-center")
public interface TestUserCenterFeignClient {
    @GetMapping("/q")
    UserDTO query(@SpringQueryMap UserDTO userDTO);
}
