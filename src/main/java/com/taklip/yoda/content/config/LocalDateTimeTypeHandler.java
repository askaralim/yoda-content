package com.taklip.yoda.content.config;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(LocalDateTime.class)
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter != null) {
            ps.setString(i, parameter.format(FORMATTER));
        } else {
            ps.setNull(i, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return parseLocalDateTime(value);
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return parseLocalDateTime(value);
    }

    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return parseLocalDateTime(value);
    }

    private LocalDateTime parseLocalDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // Try the standard format first
            return LocalDateTime.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            try {
                // Try parsing as date only and convert to start of day
                if (value.length() <= 10) {
                    return java.time.LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            .atStartOfDay();
                }
            } catch (DateTimeParseException e2) {
                // If all parsing fails, return null or log the error
                System.err.println("Unable to parse date: " + value + ". Error: " + e2.getMessage());
                return null;
            }
        }
        
        return null;
    }
}
