package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.vo.admin.AdminArticleDetailVO;
import com.inkstage.vo.front.MyArticleListVO;

/**
 * 文章管理服务接口
 */
public interface ArticleManagementService {

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean deleteArticle(Long id);

    /**
     * 彻底删除文章
     * @param id 文章ID
     * @return 是否成功
     */
    boolean permanentDeleteArticle(Long id);

    /**
     * 获取当前用户的文章列表，支持按状态过滤和搜索
     *
     * @param articleStatus    文章状态
     * @param keyword   搜索关键词
     * @param page      页码
     * @param size      每页数量
     * @return 分页结果
     */
    PageResult<MyArticleListVO> getMyArticles(ArticleStatus articleStatus, String keyword, Integer page, Integer size);

    /**
     * 分页获取所有文章（管理员）
     * @param queryDTO 文章查询DTO
     * @return 分页结果
     */
    PageResult<com.inkstage.vo.admin.AdminArticleVO> getAdminArticlesByPage(AdminArticleQueryDTO queryDTO);

    /**
     * 分页获取所有文章（管理员，旧方法）
     * @param queryDTO 文章查询DTO
     * @return 分页结果
     */
    PageResult<com.inkstage.entity.model.Article> getArticlesByPage(AdminArticleQueryDTO queryDTO);

    /**
     * 根据ID获取文章（管理员）
     * @param id 文章ID
     * @return 文章信息
     */
    com.inkstage.entity.model.Article getArticleById(Long id);

    /**
     * 更新文章状态（管理员）
     * @param id 文章ID
     * @param status 文章状态
     * @return 更新后的文章
     */
    com.inkstage.entity.model.Article updateArticleStatus(Long id, ArticleStatus status);

    /**
     * 审核通过文章（管理员）
     * @param id 文章ID
     * @return 是否成功
     */
    boolean approveArticle(Long id);

    /**
     * 审核拒绝文章（管理员）
     * @param id 文章ID
     * @param reason 拒绝原因
     * @return 是否成功
     */
    boolean rejectArticle(Long id, String reason);

    /**
     * 重新审核文章（管理员）
     * @param id 文章ID
     * @return 是否成功
     */
    boolean reprocessArticle(Long id);

    /**
     * 获取管理员文章详情（包含完整信息）
     * @param id 文章ID
     * @return 管理员文章详情VO
     */
    AdminArticleDetailVO getAdminArticleDetail(Long id);
}
