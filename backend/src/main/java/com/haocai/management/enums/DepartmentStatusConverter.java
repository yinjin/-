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
 * <p>功能：将枚举类型与数据库TINYINT类型进行双向转换</p>
 *
 * <p>遵循规范：</p>
 * <ul>
 *   <li>实体类设计规范-第2.2条：类型转换器规范</li>
 *   <li>数据库设计规范-第1.2条：枚举类型字段使用TINYINT存储</li>
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
     * <p>将枚举值转换为整数存储到数据库</p>
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
        // 遵循：实体类设计规范-第2.2条（将枚举值转换为整数存储）
        ps.setInt(i, parameter.getCode());
    }

    /**
     * 从ResultSet获取枚举值（按列名）
     *
     * @param rs        ResultSet
     * @param columnName 列名
     * @return 枚举值
     * @throws SQLException SQL异常
     */
    @Override
    public DepartmentStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Integer value = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return DepartmentStatus.getByCode(value);
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
        Integer value = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return DepartmentStatus.getByCode(value);
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
        Integer value = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return DepartmentStatus.getByCode(value);
    }
}
