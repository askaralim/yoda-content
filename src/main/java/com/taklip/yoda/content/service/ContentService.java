package com.taklip.yoda.content.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.taklip.yoda.content.dto.ContentDTO;
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
    Page<ContentDTO> getContentsByCategory(Long categoryId, Integer limit);

    /**
     * Get featured contents
     */
    Page<ContentDTO> getFeaturedContents(Integer offset, Integer limit);

    /**
     * Get no featured contents
     */
    Page<ContentDTO> getNoFeaturedContents(Integer offset, Integer limit);

    /**
     * Get published contents
     */
    Page<ContentDTO> getPublishedContents(Integer offset, Integer limit);

    /**
     * Get contents by tags
     */
    Page<ContentDTO> getContentsByTags(String tags, Integer limit);

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
    Page<ContentDTO> getContentByPage(Integer offset, Integer limit);

    /**
     * Get contents by user
     */
    Page<ContentDTO> getContentsByUser(Long userId, Integer offset, Integer limit);
}
