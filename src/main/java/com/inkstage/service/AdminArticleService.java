package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.dto.admin.AdminArticleQueryDTO;
import com.inkstage.dto.admin.AdminArticleUpdateDTO;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.vo.admin.AdminArticleDetailVO;
import com.inkstage.vo.admin.AdminArticleVO;

/**
 * 管理员文章服务接口
 * 提供文章管理相关的功能，如审核、状态管理、置顶推荐等
 */
public interface AdminArticleService {

    /**
     * 分页获取所有文章（管理员）
     *
     * @param queryDTO 文章查询DTO，包含状态、关键词、分页等参数
     * @return 分页结果
     */
    PageResult<AdminArticleVO> getAdminArticlesByPage(AdminArticleQueryDTO queryDTO);

    /**
     * 更新文章状态（管理员）
     *
     * @param id     文章ID
     * @param status 文章状态
     * @return 更新后的文章
     */
    boolean updateArticleStatus(Long id, ArticleStatus status);

    /**
     * 审核通过文章（管理员）
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean approveArticle(Long id);

    /**
     * 审核拒绝文章（管理员）
     *
     * @param id     文章ID
     * @param reason 拒绝原因
     * @return 是否成功
     */
    boolean rejectArticle(Long id, String reason);

    /**
     * 重新审核文章（管理员）
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean reprocessArticle(Long id);

    /**
     * 获取管理员文章详情（包含完整信息）
     *
     * @param id 文章ID
     * @return 管理员文章详情VO
     */
    AdminArticleDetailVO getAdminArticleDetail(Long id);

    /**
     * 置顶文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean topArticle(Long id);

    /**
     * 取消置顶文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean cancelTopArticle(Long id);

    /**
     * 推荐文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean recommendArticle(Long id);

    /**
     * 取消推荐文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean cancelRecommendArticle(Long id);

    /**
     * 管理员更新文章
     *
     * @param id        文章ID
     * @param updateDTO 更新文章DTO
     * @return 是否成功
     */
    boolean updateArticleByAdmin(Long id, AdminArticleUpdateDTO updateDTO);

    /**
     * 删除文章(管理员)
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean deleteArticleByAdmin(Long id);

    /**
     * 彻底删除文章(管理员)
     *
     * @param id 文章ID
     * @return 是否成功
     */
    boolean deleteArticlePermanentlyByAdmin(Long id);
}
