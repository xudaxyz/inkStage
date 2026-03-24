package com.inkstage.service;

import com.inkstage.common.PageResult;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.vo.front.MyArticleListVO;

/**
 * 用户文章管理服务接口
 * 提供用户个人文章管理功能，如删除、获取个人文章列表等
 */
public interface ArticleManagementService {

    /**
     * 删除文章（软删除）
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
}
