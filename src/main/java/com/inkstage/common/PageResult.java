package com.inkstage.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通用分页响应结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据列表
     */
    private List<T> record;

    /**
     * 是否为首页
     */
    private Boolean isFirstPage;

    /**
     * 是否为末页
     */
    private Boolean isLastPage;

    /**
     * 前一页页码
     */
    private Integer prePage;

    /**
     * 后一页页码
     */
    private Integer nextPage;

    /**
     * 导航页码列表
     */
    private List<Integer> navigatePages;

    /**
     * 导航起始页码
     */
    private Integer navigateFirstPage;

    /**
     * 导航结束页码
     */
    private Integer navigateLastPage;

    /**
     * 构建分页响应
     *
     * @param record   数据列表
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @param <T>      数据类型
     * @return 分页响应对象
     */
    public static <T> PageResult<T> build(List<T> record, Long total, Integer pageNum, Integer pageSize) {
        // 参数校验和默认值处理
        total = total == null ? 0L : total;
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;

        // 确保pageSize在合理范围内(1-100)
        pageSize = Math.max(1, Math.min(100, pageSize));

        // 计算总页数
        int pages = total == 0 ? 0 : (int) Math.ceil((double) total / pageSize);

        // 确保页码在合理范围内
        pageNum = Math.max(1, pageNum);
        pageNum = pages == 0 ? 1 : Math.min(pages, pageNum);

        // 空列表处理
        if (record == null) {
            record = Collections.emptyList();
        }

        // 计算导航相关字段
        boolean isFirstPage = pageNum == 1;
        boolean isLastPage = pageNum == pages || pages == 0;
        int prePage = isFirstPage ? 1 : pageNum - 1;
        int nextPage = isLastPage ? pages : pageNum + 1;

        // 计算导航页码列表(显示10个页码)
        int navigatePageNums = 10;
        List<Integer> navigatePages = calculateNavigatePages(pageNum, pages, navigatePageNums);

        // 计算导航起始和结束页码
        int navigateFirstPage = navigatePages.isEmpty() ? 1 : navigatePages.getFirst();
        int navigateLastPage = navigatePages.isEmpty() ? pages : navigatePages.getLast();

        return new PageResult<>(
                total,
                pageNum,
                pageSize,
                pages,
                record,
                isFirstPage,
                isLastPage,
                prePage,
                nextPage,
                navigatePages,
                navigateFirstPage,
                navigateLastPage
        );
    }

    /**
     * 计算导航页码列表
     *
     * @param pageNum          当前页码
     * @param pages            总页数
     * @param navigatePageNums 导航页码数量
     * @return 导航页码列表
     */
    private static List<Integer> calculateNavigatePages(int pageNum, int pages, int navigatePageNums) {
        List<Integer> navigatePages = new ArrayList<>();

        if (pages == 0) {
            return navigatePages;
        }

        if (navigatePageNums >= pages) {
            // 如果总页数小于等于导航页码数量, 则显示所有页码
            for (int i = 1; i <= pages; i++) {
                navigatePages.add(i);
            }
        } else {
            // 计算导航起始和结束位置
            int startNum = pageNum - navigatePageNums / 2;
            int endNum = pageNum + navigatePageNums / 2;

            // 调整起始位置
            if (startNum < 1) {
                startNum = 1;
                endNum = startNum + navigatePageNums - 1;
            }

            // 调整结束位置
            if (endNum > pages) {
                endNum = pages;
                startNum = endNum - navigatePageNums + 1;
            }

            // 生成导航页码列表
            for (int i = startNum; i <= endNum; i++) {
                navigatePages.add(i);
            }
        }

        return navigatePages;
    }
}
