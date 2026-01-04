package com.material.system.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回结果类
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 时间戳
     */
    private Long timestamp;

    public PageResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public PageResult(Long current, Long size, Long total, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
        this.pages = (total + size - 1) / size;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(Long current, Long size, Long total, List<T> records) {
        return new PageResult<>(current, size, total, records);
    }

    /**
     * 构建分页结果（默认参数）
     */
    public static <T> PageResult<T> of(Long total, List<T> records) {
        return new PageResult<>(1L, 10L, total, records);
    }
}
