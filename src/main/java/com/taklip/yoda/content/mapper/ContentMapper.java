package com.taklip.yoda.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taklip.yoda.content.model.Content;
import com.taklip.yoda.content.vo.ContentSearchVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContentMapper extends BaseMapper<Content> {

    /**
     * Search contents with pagination
     */
    IPage<Content> searchContents(Page<Content> page, @Param("search") ContentSearchVO searchVO);

    /**
     * Get contents by category
     */
    List<Content> getContentsByCategory(@Param("categoryId") Long categoryId, @Param("limit") Integer limit);

    /**
     * Get featured contents
     */
    IPage<Content> getContentsByFeature(@Param("featureData") Boolean featureData, @Param("limit") Integer limit,
            @Param("offset") Integer offset);

    /**
     * Get published contents
     */
    IPage<Content> getPublishedContents(@Param("limit") Integer limit, @Param("offset") Integer offset);

    /**
     * Get contents by tags
     */
    List<Content> getContentsByTags(@Param("tags") String tags, @Param("limit") Integer limit);

    /**
     * Update hit counter
     */
    int updateHitCounter(@Param("id") Long id);

    /**
     * Increase hit counter
     */
    void increaseHitCounter(@Param("id") Long id);

    /**
     * Reset hit counter
     */
    void resetHitCounter(@Param("id") Long id);
}
