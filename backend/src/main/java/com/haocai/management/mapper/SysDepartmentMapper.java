package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haocai.management.entity.SysDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门数据访问层接口
 *
 * <p>功能：提供部门数据的CRUD和自定义查询方法</p>
 *
 * <p>遵循规范：</p>
 * <ul>
 *   <li>数据访问层规范-第1条：继承BaseMapper获得基础CRUD方法</li>
 *   <li>数据访问层规范-第2条：自定义查询方法使用注解或XML实现</li>
 * </ul>
 *
 * @author 开发团队
 * @since 2026-01-08
 * @version 1.0
 */
@Mapper
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {

    /**
     * 根据部门编码查询部门
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>新增部门时检查编码是否重复</li>
     *   <li>根据编码查询部门信息</li>
     * </ul>
     *
     * @param code 部门编码
     * @return 部门信息，不存在则返回null
     */
    @Select("SELECT * FROM sys_department WHERE code = #{code} AND deleted = 0")
    SysDepartment selectByCode(@Param("code") String code);

    /**
     * 根据父部门ID查询子部门列表
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>查询某个部门下的所有子部门</li>
     *   <li>构建部门树形结构</li>
     * </ul>
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    @Select("SELECT * FROM sys_department WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order ASC")
    List<SysDepartment> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有顶级部门（parent_id为NULL或0）
     *
     * @return 顶级部门列表
     */
    @Select("SELECT * FROM sys_department WHERE (parent_id IS NULL OR parent_id = 0) AND deleted = 0 ORDER BY sort_order ASC")
    List<SysDepartment> selectRootDepartments();

    /**
     * 查询部门树形结构（递归查询）
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>前端部门树形组件展示</li>
     *   <li>部门层级结构展示</li>
     * </ul>
     *
     * @return 部门树形结构列表
     */
    @Select("SELECT * FROM sys_department WHERE deleted = 0 ORDER BY sort_order ASC")
    List<SysDepartment> selectAllDepartments();

    /**
     * 根据状态查询部门列表
     *
     * @param status 部门状态
     * @return 部门列表
     */
    @Select("SELECT * FROM sys_department WHERE status = #{status} AND deleted = 0 ORDER BY sort_order ASC")
    List<SysDepartment> selectByStatus(@Param("status") String status);

    /**
     * 查询部门及其直接子部门数量
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>删除部门前检查是否有子部门</li>
     *   <li>部门列表展示子部门数量</li>
     * </ul>
     *
     * @param departmentId 部门ID
     * @return 子部门数量
     */
    @Select("SELECT COUNT(*) FROM sys_department WHERE parent_id = #{departmentId} AND deleted = 0")
    int countChildren(@Param("departmentId") Long departmentId);

    /**
     * 查询部门下关联的用户数量
     *
     * <p>使用场景：</p>
     * <ul>
     *   <li>删除部门前检查是否有关联用户</li>
     *   <li>部门列表展示用户数量</li>
     * </ul>
     *
     * <p>注意：此方法需要关联sys_user表，这里只返回部门表中的统计</p>
     *
     * @param departmentId 部门ID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE department_id = #{departmentId} AND deleted = 0")
    int countUsers(@Param("departmentId") Long departmentId);
}
