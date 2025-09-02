package com.taklip.yoda.content.model;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("content")
public class Content extends BaseEntity {
    private boolean featureData = false;
    private boolean published = true;

    @TableField(exist = false)
    private boolean homePage = true;

    private Long categoryId;

    // @TableField(typeHandler = LocalDateTimeTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime publishDate = LocalDateTime.now();

    // @TableField(typeHandler = LocalDateTimeTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime expireDate;

    private Integer hitCounter = 0;
    private Integer score = 0;
    private Integer siteId;
    private String naturalKey;
    private String title;
    private String shortDescription;
    private String description;
    private String pageTitle;
    private String featuredImage;

    // Additional fields for content management
    // private String contentType;
    // private String status;
    // private String tags;
    // private String metaKeywords;
    // private String metaDescription;
    // private String canonicalUrl;
    // private Integer sortOrder = 0;
    // private Boolean allowComments = true;
    // private String language = "en";
    // private String seoTitle;
    // private String seoDescription;
}
