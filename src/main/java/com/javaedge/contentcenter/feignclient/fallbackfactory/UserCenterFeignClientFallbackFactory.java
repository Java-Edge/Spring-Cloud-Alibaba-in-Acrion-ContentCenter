package com.javaedge.contentcenter.feignclient.fallbackfactory;

import com.javaedge.contentcenter.domain.dto.user.UserAddBonseDTO;
import com.javaedge.contentcenter.domain.dto.user.UserDTO;
import com.javaedge.contentcenter.feignclient.UserCenterFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author JavaEdge
 */
@Component
@Slf4j
public class UserCenterFeignClientFallbackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable cause) {
        return new UserCenterFeignClient() {
            @Override
            public UserDTO findById(Integer id) {
                log.warn("远程调用被限流/降级了", cause);
                UserDTO userDTO = new UserDTO();
                userDTO.setWxNickname("流控/降级返回的用户");
                return userDTO;
            }

            @Override
            public UserDTO addBonus(UserAddBonseDTO userAddBonseDTO) {
                log.warn("远程调用被限流/降级了", cause);
                return null;
            }
        };
    }
}
