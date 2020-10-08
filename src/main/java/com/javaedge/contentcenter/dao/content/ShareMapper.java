package com.javaedge.contentcenter.dao.content;

import com.javaedge.contentcenter.domain.entity.content.Share;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author JavaEdge
 */
public interface ShareMapper extends Mapper<Share> {

    /**
     *
     * @param title 标题
     * @return
     */
    List<Share> selectByParam(@Param("title") String title);
}
