package com.haocai.management.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus字段自动填充处理器
 * 用于自动填充create_time、update_time、create_by、update_by等字段
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时自动填充创建时间和更新时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        
        // 插入时自动填充创建人和更新人（这里暂时设置为1，实际应该从当前登录用户获取）
        this.strictInsertFill(metaObject, "createBy", Long.class, 1L);
        this.strictInsertFill(metaObject, "updateBy", Long.class, 1L);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时自动填充更新时间和更新人
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", Long.class, 1L);
    }
}
