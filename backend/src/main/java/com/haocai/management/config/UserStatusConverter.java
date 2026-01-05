package com.haocai.management.config;

import com.haocai.management.entity.UserStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * UserStatus枚举转换器
 * 将String类型的值转换为UserStatus枚举
 * 支持以下格式：
 * - "0", "1", "2" (状态码)
 * - "NORMAL", "DISABLED", "LOCKED" (枚举名称)
 *
 * @author 系统开发团队
 * @since 2026-01-07
 */
@Component
public class UserStatusConverter implements Converter<String, UserStatus> {

    @Override
    public UserStatus convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        // 尝试按状态码转换
        try {
            Integer code = Integer.parseInt(source.trim());
            return UserStatus.getByCode(code);
        } catch (NumberFormatException e) {
            // 不是数字，尝试按枚举名称转换
            try {
                return UserStatus.valueOf(source.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }
    }
}
