package com.taklip.yoda.content.dto;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Custom response DTO for content pagination that can be safely cached
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentPageResponse {
    
    private List<ContentDTO> records;
    private long total;
    private long current;
    private long size;
    private long pages;
    
    /**
     * Convert to MyBatis Plus Page
     */
    public Page<ContentDTO> toPage() {
        Page<ContentDTO> page = 
            new Page<>(current, size);
        page.setRecords(records);
        page.setTotal(total);
        page.setPages(pages);
        return page;
    }
    
    /**
     * Create from MyBatis Plus Page
     */
    public static ContentPageResponse fromPage(Page<ContentDTO> page) {
        return ContentPageResponse.builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .current(page.getCurrent())
                .size(page.getSize())
                .pages(page.getPages())
                .build();
    }
}
