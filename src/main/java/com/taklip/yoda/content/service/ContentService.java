package com.taklip.yoda.content.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taklip.yoda.content.dto.ContentDTO;
import com.taklip.yoda.content.dto.ContentPageResponse;
import com.taklip.yoda.content.model.Content;
import com.taklip.yoda.content.vo.ContentSearchVO;

public interface ContentService extends IService<Content> {

    /**
     * Create new content
     */
    ContentDTO createContent(ContentDTO contentDTO);

    /**
     * Update existing content
     */
    ContentDTO updateContent(Long id, ContentDTO contentDTO);

    /**
     * Get content by ID
     */
    ContentDTO getContentById(Long id);

    /**
     * Delete content
     */
    void deleteContent(Long id);

    /**
     * Delete contents
     */
    void deleteContents(List<Long> ids);

    /**
     * Search contents with pagination
     */
    Page<ContentDTO> searchContents(ContentSearchVO searchVO);

    /**
     * Get contents by category
     */
    ContentPageResponse getContentsByCategory(Long categoryId, Integer limit);

    /**
     * Get featured contents
     */
    ContentPageResponse getFeaturedContents(Integer offset, Integer limit);

    /**
     * Get no featured contents
     */
    ContentPageResponse getNoFeaturedContents(Integer offset, Integer limit);

    /**
     * Get published contents
     */
    ContentPageResponse getPublishedContents(Integer offset, Integer limit);

    /**
     * Get contents by tags
     */
    ContentPageResponse getContentsByTags(String tags, Integer limit);

    /**
     * Increase hit counter
     */
    void increaseHitCounter(Long id);

    /**
     * Reset hit counter
     */
    void resetHitCounter(Long id);

    /**
     * Get hit counter
     */
    Integer getHitCounter(Long id);

    /**
     * Publish content
     */
    void publishContent(Long id);

    /**
     * Unpublish content
     */
    void unpublishContent(Long id);

    /**
     * Feature content
     */
    void featureContent(Long id);

    /**
     * Unfeature content
     */
    void unfeatureContent(Long id);

    /**
     * Get content by page
     */
    ContentPageResponse getContentByPage(Integer offset, Integer limit);

    /**
     * Get contents by user
     */
    ContentPageResponse getContentsByUser(Long userId, Integer offset, Integer limit);
}
