package com.inkstage.mapper;

import com.inkstage.dto.front.ColumnQueryDTO;
import com.inkstage.entity.model.Column;
import com.inkstage.vo.front.ColumnDetailVO;
import com.inkstage.vo.front.ColumnListVO;
import com.inkstage.vo.front.MyColumnVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 专栏数据访问接口
 * 提供专栏相关的数据库操作方法
 */
@Mapper
public interface ColumnMapper {

    /**
     * 根据ID查询专栏
     *
     * @param id 专栏ID
     * @return 专栏实体，如果不存在返回null
     */
    Column findById(Long id);

    /**
     * 根据ID查询专栏详情VO
     *
     * @param id 专栏ID
     * @return 专栏详情VO（包含作者信息），如果不存在返回null
     */
    ColumnDetailVO findDetailById(Long id);

    /**
     * 分页查询专栏列表
     *
     * @param queryDTO  查询条件（包含关键词、用户ID）
     * @param offset    偏移量
     * @param pageSize  每页大小
     * @return 专栏列表VO
     */
    List<ColumnListVO> findColumnList(@Param("query") ColumnQueryDTO queryDTO, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /**
     * 查询热门专栏列表
     *
     * @param limit 返回数量限制
     * @return 热门专栏列表（按阅读量降序排序）
     */
    List<ColumnListVO> findHotColumns(@Param("limit") Integer limit);

    /**
     * 查询指定用户的专栏列表
     *
     * @param userId 用户ID
     * @return 用户创建的所有专栏列表
     */
    List<MyColumnVO> findMyColumns(@Param("userId") Long userId);

    /**
     * 根据slug查询专栏
     *
     * @param slug 专栏别名
     * @return 专栏实体，如果不存在返回null
     */
    Column findBySlug(@Param("slug") String slug);

    /**
     * 根据名称和用户ID查询专栏
     * 用于校验同一用户是否已创建同名专栏
     *
     * @param name   专栏名称
     * @param userId 用户ID
     * @return 专栏实体，如果不存在返回null
     */
    Column findByNameAndUserId(@Param("name") String name, @Param("userId") Long userId);

    /**
     * 插入专栏记录
     *
     * @param column 专栏实体
     * @return 影响的行数
     */
    int insert(Column column);

    /**
     * 更新专栏信息
     *
     * @param column 专栏实体（包含要更新的字段）
     * @return 影响的行数
     */
    int update(Column column);

    /**
     * 更新专栏文章数量
     *
     * @param id     专栏ID
     * @param offset 增量（正数增加，负数减少）
     * @return 影响的行数
     */
    int updateArticleCount(@Param("id") Long id, @Param("offset") int offset);

    /**
     * 更新专栏阅读量
     *
     * @param id     专栏ID
     * @param offset 增量（正数增加，负数减少）
     * @return 影响的行数
     */
    int updateReadCount(@Param("id") Long id, @Param("offset") int offset);

    /**
     * 软删除专栏（仅限用户删除自己的专栏）
     *
     * @param id     专栏ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 统计专栏列表数量
     *
     * @param queryDTO 查询条件（包含关键词、用户ID）
     * @return 符合条件的专栏数量
     */
    long countColumnList(@Param("query") ColumnQueryDTO queryDTO);
}
