package com.taklip.yoda.content.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taklip.yoda.content.dto.ContentDTO;
import com.taklip.yoda.content.dto.ContentPageResponse;
import com.taklip.yoda.content.service.ContentService;
import com.taklip.yoda.content.vo.ContentSearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/content")
@Tag(name = "Content Management", description = "Content management API endpoints")
@Slf4j
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping
    @Operation(summary = "Create new content", description = "Create a new content item")
    public ResponseEntity<ContentDTO> createContent(@Valid @RequestBody ContentDTO contentDTO) {
        log.info("Creating content: {}", contentDTO);
        ContentDTO createdContent = contentService.createContent(contentDTO);
        log.info("Created content: {}", createdContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContent);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update content", description = "Update an existing content item")
    public ResponseEntity<ContentDTO> updateContent(
            @Parameter(description = "Content ID") @PathVariable Long id,
            @Valid @RequestBody ContentDTO contentDTO) {
        ContentDTO updatedContent = contentService.updateContent(id, contentDTO);
        return ResponseEntity.ok(updatedContent);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get content by ID", description = "Retrieve content by its ID")
    public ResponseEntity<ContentDTO> getContentById(
            @Parameter(description = "Content ID") @PathVariable Long id) {
        ContentDTO content = contentService.getContentById(id);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/page")
    @Operation(summary = "Get content by page offset and limit", description = "Retrieve content by its page offset and limit")
    public ResponseEntity<ContentPageResponse> getContentByPage(
            @Parameter(description = "Offset") @RequestParam(defaultValue = "0") Integer offset,
            @Parameter(description = "Limit") @RequestParam(defaultValue = "10") Integer limit) {
        ContentPageResponse content = contentService.getContentByPage(offset, limit);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete content", description = "Soft delete a content item")
    public ResponseEntity<Void> deleteContent(
            @Parameter(description = "Content ID") @PathVariable Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    @Operation(summary = "Search contents", description = "Search contents with pagination and filters")
    public ResponseEntity<Page<ContentDTO>> searchContents(@RequestBody ContentSearchVO searchVO) {
        Page<ContentDTO> results = contentService.searchContents(searchVO);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get contents by category", description = "Retrieve contents by category ID")
    public ResponseEntity<ContentPageResponse> getContentsByCategory(
            @Parameter(description = "Category ID") @PathVariable Long categoryId,
            @Parameter(description = "Limit number of results") @RequestParam(defaultValue = "20") Integer limit) {
        ContentPageResponse contents = contentService.getContentsByCategory(categoryId, limit);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get contents by user", description = "Retrieve contents by user ID")
    public ResponseEntity<ContentPageResponse> getContentsByUser(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Offset") @RequestParam(defaultValue = "0") Integer offset,
            @Parameter(description = "Limit") @RequestParam(defaultValue = "10") Integer limit) {
        ContentPageResponse contents = contentService.getContentsByUser(userId, offset, limit);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured contents", description = "Retrieve featured contents")
    public ResponseEntity<ContentPageResponse> getFeaturedContents(
            @Parameter(description = "Feature data") @RequestParam(defaultValue = "true") Boolean featureData,
            @Parameter(description = "Limit number of results") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "Offset") @RequestParam(defaultValue = "0") Integer offset) {
        log.info("🔍 Getting {} contents for limit: {} and offset: {}", featureData ? "featured" : "no featured", limit, offset);
        ContentPageResponse contents = null;
        if (featureData) {
            contents = contentService.getFeaturedContents(offset, limit);
        } else {
            contents = contentService.getNoFeaturedContents(offset, limit);
        }

        log.info("🔍 Got {} contents size: {}", featureData ? "featured" : "no featured", contents.getRecords().size());

        return ResponseEntity.ok(contents);
    }

    @GetMapping("/published")
    @Operation(summary = "Get published contents", description = "Retrieve published contents")
    public ResponseEntity<ContentPageResponse> getPublishedContents(
            @Parameter(description = "Limit number of results") @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "Offset") @RequestParam(defaultValue = "0") Integer offset) {
        return ResponseEntity.ok(contentService.getPublishedContents(offset, limit));
    }

    @GetMapping("/tags")
    @Operation(summary = "Get contents by tags", description = "Retrieve contents by tags")
    public ResponseEntity<ContentPageResponse> getContentsByTags(
            @Parameter(description = "Tags (comma-separated)") @RequestParam String tags,
            @Parameter(description = "Limit number of results") @RequestParam(defaultValue = "20") Integer limit) {
        ContentPageResponse contents = contentService.getContentsByTags(tags, limit);
        return ResponseEntity.ok(contents);
    }

    // @GetMapping("/all")
    // @Operation(summary = "Get contents", description = "Retrieve contents")
    // public ResponseEntity<List<ContentDTO>> getContents() {
    //     List<ContentDTO> contents = contentService.getContents();
    //     return ResponseEntity.ok(contents);
    // }

    @PostMapping("/{id}/increase-hit-counter")
    @Operation(summary = "Increase hit counter", description = "Increment the view counter for content")
    public ResponseEntity<Void> increaseHitCounter(
            @Parameter(description = "Content ID") @PathVariable Long id) {
        contentService.increaseHitCounter(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reset-hit-counter")
    @Operation(summary = "Reset hit counter", description = "Reset the view counter for content")
    public ResponseEntity<Void> resetHitCounter(
            @Parameter(description = "Content ID") @PathVariable Long id) {
        contentService.resetHitCounter(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/hit-counter")
    @Operation(summary = "Get hit counter", description = "Get the view counter for content")
    public ResponseEntity<Integer> getHitCounter(
            @Parameter(description = "Content ID") @PathVariable Long id) {
        Integer hitCounter = contentService.getHitCounter(id);
        return ResponseEntity.ok(hitCounter);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish content", description = "Publish a content item")
    public ResponseEntity<Void> publishContent(
            @Parameter(description = "Content ID") @PathVariable Long id) {
        contentService.publishContent(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unpublish")
    @Operation(summary = "Unpublish content", description = "Unpublish a content item")
    public ResponseEntity<Void> unpublishContent(
            @Parameter(description = "Content ID") @PathVariable Long id) {
        contentService.unpublishContent(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/feature")
    @Operation(summary = "Feature content", description = "Mark content as featured")
    public ResponseEntity<Void> featureContent(
            @Parameter(description = "Content ID") @PathVariable Long id) {
        contentService.featureContent(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unfeature")
    @Operation(summary = "Unfeature content", description = "Remove featured status from content")
    public ResponseEntity<Void> unfeatureContent(
            @Parameter(description = "Content ID") @PathVariable Long id) {
        contentService.unfeatureContent(id);
        return ResponseEntity.ok().build();
    }
}
