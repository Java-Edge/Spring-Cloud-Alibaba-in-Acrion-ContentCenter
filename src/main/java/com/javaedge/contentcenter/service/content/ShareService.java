package com.javaedge.contentcenter.service.content;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.javaedge.contentcenter.dao.content.MidUserShareMapper;
import com.javaedge.contentcenter.dao.content.ShareMapper;
import com.javaedge.contentcenter.dao.messaging.RocketmqTransactionLogMapper;
import com.javaedge.contentcenter.domain.dto.content.ShareAuditDTO;
import com.javaedge.contentcenter.domain.dto.content.ShareDTO;
import com.javaedge.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import com.javaedge.contentcenter.domain.dto.user.UserAddBonseDTO;
import com.javaedge.contentcenter.domain.dto.user.UserDTO;
import com.javaedge.contentcenter.domain.entity.content.MidUserShare;
import com.javaedge.contentcenter.domain.entity.content.Share;
import com.javaedge.contentcenter.domain.entity.messaging.RocketmqTransactionLog;
import com.javaedge.contentcenter.domain.enums.AuditStatusEnum;
import com.javaedge.contentcenter.feignclient.UserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author JavaEdge
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {
    private final ShareMapper shareMapper;
    private final UserCenterFeignClient userCenterFeignClient;
    private final RocketMQTemplate rocketMQTemplate;
    private final RocketmqTransactionLogMapper rocketmqTransactionLogMapper;
    private final Source source;
    private final MidUserShareMapper midUserShareMapper;

    public ShareDTO findById(Integer id) {
        // 获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        // 发布人id
        Integer userId = share.getUserId();

        // 1. 代码不可读
        // 2. 复杂的url难以维护：https://user-center/s?ie={ie}&f={f}&rsv_bp=1&rsv_idx=1&tn=baidu&wd=a&rsv_pq=c86459bd002cfbaa&rsv_t=edb19hb%2BvO%2BTySu8dtmbl%2F9dCK%2FIgdyUX%2BxuFYuE0G08aHH5FkeP3n3BXxw&rqlang=cn&rsv_enter=1&rsv_sug3=1&rsv_sug2=0&inputT=611&rsv_sug4=611
        // 3. 难以相应需求的变化，变化很没有幸福感
        // 4. 编程体验不统一
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);

        ShareDTO shareDTO = new ShareDTO();
        // 消息的装配
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        // 用HTTP GET方法去请求，并且返回一个对象
        ResponseEntity<String> forEntity = restTemplate.getForEntity(
            "http://localhost:8080/users/{id}",
            String.class, 2
        );

        System.out.println(forEntity.getBody());
        // 200 OK
        // 500
        // 502 bad gateway...
        System.out.println(forEntity.getStatusCode());
    }

    public Share auditById(Integer id, ShareAuditDTO auditDTO) {
        // 1. 查询share是否存在，不存在或者当前的audit_status != NOT_YET，那么抛异常
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法！该分享不存在！");
        }
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法！该分享已审核通过或审核不通过！");
        }

        // 3. 如果是PASS，那么发送消息给rocketmq，让用户中心去消费，并为发布人添加积分
        if (AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())) {
            // 发送半消息。。
            String transactionId = UUID.randomUUID().toString();

            this.source.output()
                .send(
                    MessageBuilder
                        .withPayload(
                            UserAddBonusMsgDTO.builder()
                                .userId(share.getUserId())
                                .bonus(50)
                                .build()
                        )
                        .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                        .setHeader("share_id", id)
                        .setHeader("dto", JSON.toJSONString(auditDTO))
                        .build()
                );
        // reject
        } else {
            this.auditByIdInDB(id, auditDTO);
        }
        return share;
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO auditDTO) {
        Share share = Share.builder()
            .id(id)
            .auditStatus(auditDTO.getAuditStatusEnum().toString())
            .reason(auditDTO.getReason())
            .build();
        this.shareMapper.updateByPrimaryKeySelective(share);

        // 4. 把share写到缓存
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO auditDTO, String transactionId) {
        this.auditByIdInDB(id, auditDTO);

        this.rocketmqTransactionLogMapper.insertSelective(
            RocketmqTransactionLog.builder()
                .transactionId(transactionId)
                .log("审核分享...")
                .build()
        );
    }

    public PageInfo<Share> q(String title, Integer pageNo, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNo, pageSize);
        List<Share> shares = this.shareMapper.selectByParam(title);
        List<Share> sharesDeal;
        // 1. 如果用户未登录，那么downloadUrl全部设为null
        if (userId == null) {
            sharesDeal = shares.stream()
                .peek(share -> {
                    share.setDownloadUrl(null);
                })
                .collect(Collectors.toList());
        }
        // 2. 如果用户登录了，那么查询一下mid_user_share，如果没有数据，那么这条share的downloadUrl也设为null
        else {
            sharesDeal = shares.stream()
                .peek(share -> {
                    MidUserShare midUserShare = this.midUserShareMapper.selectOne(
                        MidUserShare.builder()
                            .userId(userId)
                            .shareId(share.getId())
                            .build()
                    );
                    if (midUserShare == null) {
                        share.setDownloadUrl(null);
                    }
                })
                .collect(Collectors.toList());
        }
        return new PageInfo<>(sharesDeal);
    }

    public Share exchangeById(Integer id, HttpServletRequest request) {
        Object userId = request.getAttribute("id");
        Integer integerUserId = (Integer) userId;

        // 1. 根据id查询share，校验是否存在
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("该分享不存在！");
        }
        Integer price = share.getPrice();

        // 2. 如果当前用户已经兑换过该分享，则直接返回
        MidUserShare midUserShare = this.midUserShareMapper.selectOne(
            MidUserShare.builder()
                .shareId(id)
                .userId(integerUserId)
                .build()
        );
        if (midUserShare != null) {
            return share;
        }

        // 3. 根据当前登录的用户id，查询积分是否够
        UserDTO userDTO = this.userCenterFeignClient.findById(integerUserId);
        if (price > userDTO.getBonus()) {
            throw new IllegalArgumentException("用户积分不够用！");
        }

        // 4. 扣减积分 & 往mid_user_share里插入一条数据
        this.userCenterFeignClient.addBonus(
            UserAddBonseDTO.builder()
                .userId(integerUserId)
                .bonus(0 - price)
                .build()
        );
        this.midUserShareMapper.insert(
            MidUserShare.builder()
                .userId(integerUserId)
                .shareId(id)
                .build()
        );
        return share;
    }
}

