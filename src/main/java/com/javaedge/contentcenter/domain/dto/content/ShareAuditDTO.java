package com.javaedge.contentcenter.domain.dto.content;

import com.javaedge.contentcenter.domain.enums.AuditStatusEnum;
import lombok.Data;


/**
 * @author JavaEdge
 */
@Data
public class ShareAuditDTO {
    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatusEnum;
    /**
     * 原因
     */
    private String reason;
}
