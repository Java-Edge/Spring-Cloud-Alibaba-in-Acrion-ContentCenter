package com.javaedge.contentcenter.controller.content;

import com.javaedge.contentcenter.auth.CheckAuthorization;
import com.javaedge.contentcenter.domain.dto.content.ShareAuditDTO;
import com.javaedge.contentcenter.domain.entity.content.Share;
import com.javaedge.contentcenter.service.content.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员
 *
 * @author JavaEdge
 */
@RestController
@RequestMapping("/admin/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareAdminController {
    // 包括了admin的功能
    private final ShareService shareService;

    @PutMapping("/audit/{id}")
    @CheckAuthorization("admin")
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDTO auditDTO) {
        return this.shareService.auditById(id, auditDTO);
    }

}

