package com.javaedge.contentcenter.feignclient;

import com.javaedge.contentcenter.domain.dto.user.UserAddBonseDTO;
import com.javaedge.contentcenter.domain.dto.user.UserDTO;
import com.javaedge.contentcenter.feignclient.fallbackfactory.UserCenterFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author JavaEdge
 */
//@FeignClient(name = "user-center", configuration = GlobalFeignConfiguration.class)
@FeignClient(name = "user-center",
//    fallback = UserCenterFeignClientFallback.class,
    fallbackFactory = UserCenterFeignClientFallbackFactory.class
)
public interface UserCenterFeignClient {
    /**
     * http://user-center/users/{id}
     *
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);

    /**
     * 添加
     *
     * @param userAddBonseDTO
     * @return
     */
    @PutMapping("/users/add-bonus")
    UserDTO addBonus(@RequestBody UserAddBonseDTO userAddBonseDTO);
}
