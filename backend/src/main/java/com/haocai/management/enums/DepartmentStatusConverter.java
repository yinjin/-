package com.haocai.management.enums;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 部门状态枚举类型转换器
 *
 * <p>功能：将枚举类型与数据库VARCHAR类型进行双向转换</p>
 *
 * <p>遵循规范：</p>
 * <ul>
 *   <li>实体类设计规范-第2.2条：类型转换器规范</li>
 *   <li>数据库设计规范-第1.2条：枚举类型字段使用VARCHAR存储</li>
 * </ul>
 *
 * <p>使用场景：</p>
 * <ul>
 *   <li>MyBatis-Plus查询结果转换为枚举</li>
 *   <li>枚举值插入/更新到数据库</li>
 *   <li>API参数绑定到枚举字段</li>
 * </ul>
 *
 * @author 开发团队
 * @since 2026-01-08
 * @version 1.0
 */
@MappedTypes(DepartmentStatus.class)
public class DepartmentStatusConverter extends BaseTypeHandler<DepartmentStatus> {

    /**
     * 设置枚举参数到PreparedStatement
     *
     * <p>将枚举值转换为字符串存储到数据库</p>
     *
     * @param ps        PreparedStatement
     * @param i         参数索引
     * @param parameter 枚举参数值
     * @param jdbcType  JDBC类型
     * @throws SQLException SQL异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DepartmentStatus parameter, JdbcType jdbcType)
            throws SQLException {
        // 遵循：实体类设计规范-第2.2条（将枚举值转换为字符串存储）
        ps.setString(i, parameter.name());
    }

    /**
     * 从ResultSet获取枚举值（按列名）
     *
     * @param rs        ResultSet
     * columnName 列名
     * @return 枚举值
     * @throws SQLException SQL异常
     */
    @Override
    public DepartmentStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToEnum(value);
    }

    /**
     * 从ResultSet获取枚举值（按列索引）
     *
     * @param rs            ResultSet
     * @param columnIndex   列索引
     * @return 枚举值
     * @throws SQLException SQL异常
     */
    @Override
    public DepartmentStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToEnum(value);
    }

    /**
     * 从CallableStatement获取枚举值
     *
     * @param cs            CallableStatement
     * @param columnIndex   列索引
     * @return 枚举值
     * @throws SQLException SQL异常
     */
    @Override
    public DepartmentStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToEnum(value);
    }

    /**
     * 将数据库字符串值转换为枚举值
     *
     * <p>安全处理：</p>
     * <ul>
     *   <li>空值返回null</li>
     *   <li>无效值返回null（不抛出异常）</li>
     *   <li>区分大小写匹配</li>
     * </ul>
     *
     * @param value 数据库存储的字符串值
     * @return 对应的枚举值，不存在则返回null
     */
    private DepartmentStatus convertToEnum(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            // 使用valueOf进行大小写敏感的枚举值转换
            return DepartmentStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            // 无效的枚举值，记录警告日志并返回null
            // 遵循：数据访问层规范-第3.2条（异常处理）
            return null;
        }
    }
}
