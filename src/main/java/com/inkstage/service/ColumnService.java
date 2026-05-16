package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.front.ColumnCreateDTO;
import com.inkstage.dto.front.ColumnQueryDTO;
import com.inkstage.entity.model.ArticleColumn;
import com.inkstage.enums.VisibleStatus;
import com.inkstage.vo.front.ArticleListVO;
import com.inkstage.vo.front.ColumnDetailVO;
import com.inkstage.vo.front.ColumnListVO;
import com.inkstage.vo.front.ColumnNeighborVO;
import com.inkstage.vo.front.ColumnOptionVO;
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
     * 更新专栏可见性
     *
     * @param columnId 专栏ID
     * @param visible 可见性状态
     * @return 更新成功返回true，失败返回false
     */
    boolean updateColumnVisible(Long columnId, VisibleStatus visible);

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
     * @return 专栏详情（包含作者信息，不含文章列表），如果专栏不存在返回null
     */
    ColumnDetailVO getColumnDetail(Long columnId);

    /**
     * 获取专栏文章分页列表
     *
     * @param columnId 专栏ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @param sortBy   排序方式：latest（最新）、earliest（最早）、readCount（阅读量）、commentCount（评论数）
     * @return 专栏文章分页列表
     */
    PageResult<ArticleListVO> getColumnArticles(Long columnId, Integer pageNum, Integer pageSize, String sortBy);

    /**
     * 获取热门专栏列表
     *
     * @param limit 返回数量限制
     * @return 热门专栏列表（按阅读量排序）
     */
    List<ColumnListVO> getHotColumns(Integer limit);

    /**
     * 搜索专栏内的文章
     *
     * @param columnId 专栏ID
     * @param keyword  搜索关键词
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 专栏文章搜索结果
     */
    PageResult<ArticleListVO> searchColumnArticles(Long columnId, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取当前用户的专栏列表
     *
     * @return 当前用户创建的所有专栏
     */
    PageResult<MyColumnVO> getMyColumns(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取当前用户的专栏选项（仅ID和名称）
     * 用于创建文章时选择专栏
     *
     * @return 当前用户创建的所有专栏选项
     */
    List<ColumnOptionVO> getMyColumnOptions();

    /**
     * 获取文章在专栏中的上下篇文章信息
     *
     * @param articleId 文章ID
     * @return 上下篇文章信息，包含专栏信息和上下篇文章详情
     */
    ColumnNeighborVO getColumnNeighborArticles(Long articleId);

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
     * 批量更新专栏文章排序（用户拖拽排序后调用）
     * 传入按新顺序排列的文章ID列表，自动计算并更新排序值
     *
     * @param columnId  专栏ID
     * @param articleIds 按新顺序排列的文章ID列表
     * @return 更新成功返回true，失败返回false
     */
    boolean batchUpdateColumnArticleSort(Long columnId, List<Long> articleIds);

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
