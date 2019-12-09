package com.javaedge.contentcenter.feignclient.fallback;

import com.javaedge.contentcenter.domain.dto.user.UserAddBonseDTO;
import com.javaedge.contentcenter.domain.dto.user.UserDTO;
import com.javaedge.contentcenter.feignclient.UserCenterFeignClient;
import org.springframework.stereotype.Component;

/**
 * @author JavaEdge
 */
@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {
    @Override
    public UserDTO findById(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setWxNickname("流控/降级返回的用户");
        return userDTO;
    }

    @Override
    public UserDTO addBonus(UserAddBonseDTO userAddBonseDTO) {
        return null;
    }
}
