package com.haocai.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量操作结果封装类
 * 用于返回批量更新、删除等操作的结果统计
 * 
 * 遵循：开发规范-第5条（统一响应格式）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchUpdateResult {
    
    /**
     * 成功处理的数量
     */
    private int successCount;
    
    /**
     * 失败的数量
     */
    private int failureCount;
    
    /**
     * 失败的ID列表
     */
    private List<Long> failedIds;
    
    /**
     * 失败原因列表（与failedIds一一对应）
     */
    private List<String> failureReasons;
    
    /**
     * 构造函数
     * @param successCount 成功数量
     * @param failureCount 失败数量
     */
    public BatchUpdateResult(int successCount, int failureCount) {
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.failedIds = new ArrayList<>();
        this.failureReasons = new ArrayList<>();
    }
    
    /**
     * 添加失败记录
     * @param id 失败的ID
     * @param reason 失败原因
     */
    public void addFailure(Long id, String reason) {
        this.failedIds.add(id);
        this.failureReasons.add(reason);
    }
    
    /**
     * 判断是否全部成功
     * @return true-全部成功，false-存在失败
     */
    public boolean isAllSuccess() {
        return failureCount == 0;
    }
    
    /**
     * 获取总处理数量
     * @return 总数量
     */
    public int getTotalCount() {
        return successCount + failureCount;
    }
}
