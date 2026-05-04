package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ColumnCreateDTO;
import com.inkstage.dto.front.ColumnQueryDTO;
import com.inkstage.entity.model.ArticleColumn;
import com.inkstage.vo.front.ColumnDetailVO;
import com.inkstage.vo.front.ColumnListVO;
import com.inkstage.vo.front.MyColumnVO;

import java.util.List;

/**
 * 专栏服务接口
 * 提供专栏的创建、查询、更新、删除以及文章与专栏关联的操作
 */
public interface ColumnService {

    /**
     * 创建专栏
     *
     * @param dto 专栏创建DTO
     * @return 创建成功返回专栏ID，失败返回null
     */
    Long createColumn(ColumnCreateDTO dto);

    /**
     * 更新专栏信息
     *
     * @param columnId 专栏ID
     * @param dto      专栏更新DTO
     * @return 更新成功返回true，失败返回false
     */
    boolean updateColumn(Long columnId, ColumnCreateDTO dto);

    /**
     * 删除专栏（软删除）
     *
     * @param columnId 专栏ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteColumn(Long columnId);

    /**
     * 获取专栏列表（分页）
     *
     * @param queryDTO 查询条件（包含关键词、用户ID、页码、每页大小）
     * @return 分页的专栏列表
     */
    PageResult<ColumnListVO> getColumns(ColumnQueryDTO queryDTO);

    /**
     * 获取专栏详情
     *
     * @param columnId 专栏ID
     * @return 专栏详情（包含作者信息和文章列表），如果专栏不存在返回null
     */
    ColumnDetailVO getColumnDetail(Long columnId);

    /**
     * 获取热门专栏列表
     *
     * @param limit 返回数量限制
     * @return 热门专栏列表（按阅读量排序）
     */
    List<ColumnListVO> getHotColumns(Integer limit);

    /**
     * 获取当前用户的专栏列表
     *
     * @return 当前用户创建的所有专栏
     */
    List<MyColumnVO> getMyColumns();

    /**
     * 添加文章到专栏
     *
     * @param columnId  专栏ID
     * @param articleId 文章ID
     * @param sortOrder 文章在专栏内的排序位置（可选，不传则追加到末尾）
     * @return 添加成功返回true，失败返回false
     */
    boolean addArticleToColumn(Long columnId, Long articleId, Integer sortOrder);

    /**
     * 从专栏移除文章
     *
     * @param columnId  专栏ID
     * @param articleId 文章ID
     * @return 移除成功返回true，失败返回false
     */
    boolean removeArticleFromColumn(Long columnId, Long articleId);

    /**
     * 更新专栏内文章的排序
     *
     * @param columnId  专栏ID
     * @param articleId 文章ID
     * @param sortOrder 新的排序位置
     * @return 更新成功返回true，失败返回false
     */
    boolean updateArticleSort(Long columnId, Long articleId, Integer sortOrder);

    /**
     * 检查文章是否已在指定专栏中
     *
     * @param columnId  专栏ID
     * @param articleId 文章ID
     * @return 文章在专栏中返回true，否则返回false
     */
    boolean isArticleInColumn(Long columnId, Long articleId);

    /**
     * 获取文章所属的专栏信息
     *
     * @param articleId 文章ID
     * @return 文章所属的专栏关联信息，如果文章不在任何专栏中则返回null
     */
    ArticleColumn getArticleColumn(Long articleId);

    /**
     * 增加专栏阅读量
     *
     * @param columnId 专栏ID
     */
    void incrementColumnReadCount(Long columnId);

    /**
     * 将文章移动到另一个专栏（同时处理旧专栏的移除和新专栏的添加）
     *
     * @param articleId   文章ID
     * @param newColumnId 新专栏ID（如果为null，则只是从旧专栏移除）
     * @param sortOrder   文章在专栏内的排序位置（可选，不传则追加到末尾）
     */
    void moveArticleToColumn(Long articleId, Long newColumnId, Integer sortOrder);

    /**
     * 根据文章ID解除文章与专栏的关联（用于删除文章时调用）
     *
     * @param articleId 文章ID
     */
    void removeArticleColumnRelation(Long articleId);

    /**
     * 向专栏的所有订阅者发送文章更新通知
     *
     * @param columnId  专栏ID
     * @param articleId 文章ID
     * @param articleTitle 文章标题
     */
    void sendColumnArticleUpdateNotification(Long columnId, Long articleId, String articleTitle);

    /**
     * 向专栏的所有订阅者发送专栏下线通知
     *
     * @param columnId 专栏ID
     */
    void sendColumnDisabledNotification(Long columnId);

    /**
     * 向专栏的所有订阅者发送专栏恢复通知
     *
     * @param columnId 专栏ID
     */
    void sendColumnRestoredNotification(Long columnId);
}
