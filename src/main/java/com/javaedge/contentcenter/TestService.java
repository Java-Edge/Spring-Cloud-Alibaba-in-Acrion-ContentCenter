package com.javaedge.contentcenter;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author JavaEdge
 */
@Slf4j
@Service
public class TestService {

    @SentinelResource("common")

    public String common() {
        log.info("common....");
        return "common";
    }
}
