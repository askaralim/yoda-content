package com.taklip.yoda.content.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.taklip.yoda.content.convertor.ContentConvertor;
import com.taklip.yoda.content.dto.ContentDTO;
import com.taklip.yoda.content.dto.ContentPageResponse;
import com.taklip.yoda.content.mapper.ContentMapper;
import com.taklip.yoda.content.model.Content;
import com.taklip.yoda.content.service.CacheLoggingService;
import com.taklip.yoda.content.service.ContentService;
import com.taklip.yoda.content.vo.ContentSearchVO;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content> implements ContentService {

    @Autowired
    private ContentConvertor contentConvertor;

    @Autowired
    private CacheLoggingService cacheLoggingService;

    @Override
    @CacheEvict(value = { "content:by:id", "content:featured", "content:published", "content:by:category",
            "content:by:tags" }, allEntries = true)
    public ContentDTO createContent(ContentDTO contentDTO) {
        Content content = contentConvertor.toEntity(contentDTO);
        this.save(content);

        ContentDTO createdContent = contentConvertor.toDTO(content);

        return createdContent;
    }

    @Override
    @CacheEvict(value = { "content:by:id", "content:featured", "content:published", "content:by:category",
            "content:by:tags" }, allEntries = true)
    public ContentDTO updateContent(Long id, ContentDTO contentDTO) {
        Content existingContent = this.getById(id);

        if (existingContent == null) {
            throw new RuntimeException("Content not found with id: " + id);
        }

        contentConvertor.updateEntity(existingContent, contentDTO);

        this.updateById(existingContent);

        ContentDTO updatedContent = contentConvertor.toDTO(existingContent);

        return updatedContent;
    }

    @Override
    @Cacheable(value = "content:by:id", key = "#id")
    public ContentDTO getContentById(Long id) {
        Content content = this.getById(id);
        if (content == null) {
            throw new RuntimeException("Content not found with id: " + id);
        }
        return contentConvertor.toDTO(content);
    }

    @Override
    @CacheEvict(value = { "content:by:id", "content:featured", "content:published", "content:by:category",
            "content:by:tags" }, allEntries = true)
    public void deleteContent(Long id) {
        this.removeById(id);
    }

    @Override
    public Page<ContentDTO> searchContents(ContentSearchVO searchVO) {
        // Page<Content> contentPage = baseMapper.searchContents(new
        // Page<>(searchVO.getPage(), searchVO.getSize()),
        // searchVO);

        // Page<ContentDTO> dtoPage = new Page<>(searchVO.getPage(),
        // searchVO.getSize());
        // dtoPage.setRecords(contentConvertor.toDTOList(contentPage.getRecords()));
        // dtoPage.setTotal(contentPage.getTotal());
        // return dtoPage;
        return null;
    }

    @Override
    @Cacheable(value = "content:by:category", key = "#categoryId + ':' + #limit")
    public ContentPageResponse getContentsByCategory(Long categoryId, Integer limit) {
        IPage<Content> contentPage = page(new Page<>(0, limit),
                new LambdaQueryWrapper<Content>().eq(Content::getCategoryId, categoryId));
        Page<ContentDTO> dtoPage = new Page<>(0, limit);
        dtoPage.setRecords(contentConvertor.toDTOList(contentPage.getRecords()));
        dtoPage.setTotal(contentPage.getTotal());
        return ContentPageResponse.fromPage(dtoPage);
    }

    @Override
    @Cacheable(value = "content:featured", keyGenerator = "customCacheKeyGenerator")
    public ContentPageResponse getFeaturedContents(Integer offset, Integer limit) {
        IPage<Content> contentPage = page(new Page<>(offset, limit),
                new LambdaQueryWrapper<Content>().eq(Content::isFeatureData, true).orderByDesc(Content::getCreateTime));
        Page<ContentDTO> dtoPage = new Page<>(offset, limit);
        dtoPage.setRecords(contentConvertor.toDTOList(contentPage.getRecords()));
        dtoPage.setTotal(contentPage.getTotal());
        return ContentPageResponse.fromPage(dtoPage);
    }

    @Override
    @Cacheable(value = "content:no:featured", keyGenerator = "customCacheKeyGenerator")
    public ContentPageResponse getNoFeaturedContents(Integer offset, Integer limit) {
        IPage<Content> contentPage = null;
        try {
            contentPage = page(new Page<>(offset, limit),
                new LambdaQueryWrapper<Content>().eq(Content::isFeatureData, false)
                        .orderByDesc(Content::getCreateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Page<ContentDTO> dtoPage = new Page<>(offset, limit);
        dtoPage.setRecords(contentConvertor.toDTOList(contentPage.getRecords()));
        dtoPage.setTotal(contentPage.getTotal());
        return ContentPageResponse.fromPage(dtoPage);
    }

    @Override
    @Cacheable(value = "content:published", keyGenerator = "customCacheKeyGenerator")
    public ContentPageResponse getPublishedContents(Integer offset, Integer limit) {
        IPage<Content> contentPage = page(new Page<>(offset, limit),
                new LambdaQueryWrapper<Content>().eq(Content::isPublished, true));
        Page<ContentDTO> dtoPage = new Page<>(offset, limit);
        dtoPage.setRecords(contentConvertor.toDTOList(contentPage.getRecords()));
        dtoPage.setTotal(contentPage.getTotal());
        return ContentPageResponse.fromPage(dtoPage);
    }

    @Override
    @Cacheable(value = "content:by:tags", key = "'tags:' + #tags.hashCode() + ':limit:' + #limit")
    public ContentPageResponse getContentsByTags(String tags, Integer limit) {
        // IPage<Content> contentPage = page(new Page<>(0, limit),
        // new LambdaQueryWrapper<Content>().like(Content::getTags, tags));
        // Page<ContentDTO> dtoPage = new Page<>(0, limit);
        // dtoPage.setRecords(contentConvertor.toDTOList(contentPage.getRecords()));
        // dtoPage.setTotal(contentPage.getTotal());
        // return ContentPageResponse.fromPage(dtoPage);
        return null;
    }

    @Override
    public void increaseHitCounter(Long id) {
        baseMapper.increaseHitCounter(id);
    }

    @Override
    public void resetHitCounter(Long id) {
        baseMapper.resetHitCounter(id);
    }

    @Override
    @Cacheable(value = "content:by:id", key = "'id:' + #id + ':hit-counter'")
    public Integer getHitCounter(Long id) {
        Content content = this.getById(id);
        if (content == null) {
            throw new RuntimeException("Content not found with id: " + id);
        }
        return content.getHitCounter();
    }

    @Override
    @CacheEvict(value = { "content:by:id", "content:published", "content:featured" }, allEntries = true)
    public void publishContent(Long id) {
        Content content = this.getById(id);
        if (content == null) {
            throw new RuntimeException("Content not found with id: " + id);
        }

        content.setPublished(true);
        this.updateById(content);
    }

    @Override
    @CacheEvict(value = { "content:by:id", "content:published", "content:featured" }, allEntries = true)
    public void unpublishContent(Long id) {
        Content content = this.getById(id);
        if (content == null) {
            throw new RuntimeException("Content not found with id: " + id);
        }

        content.setPublished(false);
        this.updateById(content);
    }

    @Override
    @CacheEvict(value = { "content:by:id", "content:featured" }, allEntries = true)
    public void featureContent(Long id) {
        Content content = this.getById(id);
        if (content == null) {
            throw new RuntimeException("Content not found with id: " + id);
        }

        content.setFeatureData(true);
        this.updateById(content);
    }

    @Override
    @CacheEvict(value = { "content:by:id", "content:featured" }, allEntries = true)
    public void unfeatureContent(Long id) {
        Content content = this.getById(id);
        if (content == null) {
            throw new RuntimeException("Content not found with id: " + id);
        }

        content.setFeatureData(false);
        this.updateById(content);
    }

    @Override
    public void deleteContents(List<Long> ids) {
        this.removeBatchByIds(ids);
    }

    @Override
    public ContentPageResponse getContentByPage(Integer offset, Integer limit) {
        IPage<Content> contentPage = page(new Page<>(offset, limit),
                new LambdaQueryWrapper<Content>().orderByDesc(Content::getCreateTime));

        Page<ContentDTO> dtoPage = new Page<>(offset, limit);
        dtoPage.setRecords(contentConvertor.toDTOList(contentPage.getRecords()));
        dtoPage.setTotal(contentPage.getTotal());
        return ContentPageResponse.fromPage(dtoPage);
    }

    @Override
    @Cacheable(value = "content:by:user", key = "#userId + ':' + #offset + ':' + #limit")
    public ContentPageResponse getContentsByUser(Long userId, Integer offset, Integer limit) {
        IPage<Content> contentPage = page(new Page<>(offset, limit),
                new LambdaQueryWrapper<Content>().eq(Content::getCreateBy, userId).orderByDesc(Content::getCreateTime));

        Page<ContentDTO> dtoPage = new Page<>(offset, limit);
        dtoPage.setRecords(contentConvertor.toDTOList(contentPage.getRecords()));
        dtoPage.setTotal(contentPage.getTotal());
        return ContentPageResponse.fromPage(dtoPage);
    }

    // Example: Manual cache operation with custom logic
    public ContentDTO getContentWithCustomLogging(Long id) {
        // return cacheLoggingService.logCacheOperation("content:by:id",
        // String.valueOf(id), "custom", () -> {
        // Custom business logic here
        Content content = this.getById(id);
        if (content == null) {
            cacheLoggingService.logCacheMiss("content:by:id", String.valueOf(id), 0, "Custom logic - not found");
            return null;
        }

        // Additional custom logging
        if (content.isPublished()) {
            cacheLoggingService.logCacheHit("content:by:id", String.valueOf(id), 0);
        } else {
            cacheLoggingService.logCacheMiss("content:by:id", String.valueOf(id), 0, "Content not published");
        }

        return contentConvertor.toDTO(content);
        // });
    }
}
