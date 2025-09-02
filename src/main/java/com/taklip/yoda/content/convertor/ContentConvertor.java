package com.taklip.yoda.content.convertor;

import com.taklip.yoda.content.dto.ContentDTO;
import com.taklip.yoda.content.model.Content;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ContentConvertor {
    ContentConvertor INSTANCE = Mappers.getMapper(ContentConvertor.class);

    /**
     * Convert Content entity to ContentDTO
     */
    // @Mapping(target = "publishDate", source = "publishDate", qualifiedByName = "formatDate")
    // @Mapping(target = "expireDate", source = "expireDate", qualifiedByName = "formatDate")
    ContentDTO toDTO(Content content);

    /**
     * Convert ContentDTO to Content entity
     */
    // @Mapping(target = "createTime", ignore = true)
    // @Mapping(target = "updateTime", ignore = true)
    // @Mapping(target = "createBy", ignore = true)
    // @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Content toEntity(ContentDTO dto);

    /**
     * Update existing Content entity with ContentDTO data
     */
    // @Mapping(target = "id", ignore = true)
    // @Mapping(target = "createTime", ignore = true)
    // @Mapping(target = "createBy", ignore = true)
    // @Mapping(target = "updateTime", ignore = true)
    // @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntity(@MappingTarget Content content, ContentDTO dto);

    /**
     * Convert list of Content entities to ContentDTO list
     */
    List<ContentDTO> toDTOList(List<Content> contents);

    /**
     * Convert list of ContentDTO to Content entities list
     */
    List<Content> toEntityList(List<ContentDTO> dtos);

    /**
     * Format date to string
     */
    @Named("formatDate")
    default String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Custom mapping for description field
     * You can add custom logic here, like the img src replacement you mentioned
     */
    @Named("processDescription")
    default String processDescription(String description) {
        if (description == null) {
            return null;
        }
        // Add your custom logic here, e.g.:
        // return description.replace("img src", "img data-src");
        return description;
    }
}
