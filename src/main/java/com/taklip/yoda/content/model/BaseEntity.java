package com.taklip.yoda.content.model;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;

import lombok.Data;

@Data
public class BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE, typeHandler = LocalDateTimeTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    // @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Long createBy;

    // @TableField(value = "create_date", fill = FieldFill.INSERT, typeHandler = LocalDateTimeTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    // @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private int deleted;
}
