package com.inkstage.controller.front;

import com.inkstage.annotation.UserAccess;
import com.inkstage.common.PageResult;
import com.inkstage.common.Result;
import com.inkstage.dto.front.ColumnCreateDTO;
import com.inkstage.dto.front.ColumnQueryDTO;
import com.inkstage.dto.front.UpdateColumnArticleDTO;
import com.inkstage.entity.model.ArticleColumn;
import com.inkstage.service.ColumnService;
import com.inkstage.vo.front.ColumnDetailVO;
import com.inkstage.vo.front.ColumnListVO;
import com.inkstage.vo.front.MyColumnVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台专栏控制器
 * 提供专栏的创建、查询、更新、删除等操作
 */
@Slf4j
@RestController
@RequestMapping("/front/column")
@RequiredArgsConstructor
public class ColumnController {

    private final ColumnService columnService;

    /**
     * 创建专栏
     *
     * @param dto 专栏创建DTO
     * @return 创建成功返回专栏ID，失败返回错误信息
     */
    @PostMapping("/create")
    @UserAccess
    public Result<Long> createColumn(@Valid @RequestBody ColumnCreateDTO dto) {
        log.info("创建专栏: {}", dto);
        Long columnId = columnService.createColumn(dto);
        return Result.success(columnId, "专栏创建成功");
    }

    /**
     * 更新专栏信息
     *
     * @param id  专栏ID
     * @param dto 专栏更新DTO
     * @return 更新成功返回true，失败返回false
     */
    @PutMapping("/update/{id}")
    @UserAccess
    public Result<Boolean> updateColumn(@PathVariable Long id, @Valid @RequestBody ColumnCreateDTO dto) {
        log.info("更新专栏: id={}, dto={}", id, dto);
        boolean success = columnService.updateColumn(id, dto);
        return success ? Result.success(true, "专栏更新成功") : Result.error("专栏更新失败");
    }

    /**
     * 删除专栏（软删除）
     *
     * @param id 专栏ID
     * @return 删除成功返回true，失败返回false
     */
    @DeleteMapping("/delete/{id}")
    @UserAccess
    public Result<Boolean> deleteColumn(@PathVariable Long id) {
        log.info("删除专栏: id={}", id);
        boolean success = columnService.deleteColumn(id);
        return success ? Result.success(true, "专栏删除成功") : Result.error("专栏删除失败");
    }

    /**
     * 获取专栏列表（分页）
     *
     * @param queryDTO 查询条件（包含关键词、用户ID、页码、每页大小）
     * @return 分页的专栏列表
     */
    @GetMapping("/list")
    public Result<PageResult<ColumnListVO>> getColumns(ColumnQueryDTO queryDTO) {
        PageResult<ColumnListVO> pageResult = columnService.getColumns(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取专栏详情
     *
     * @param id 专栏ID
     * @return 专栏详情（包含作者信息和文章列表）
     */
    @GetMapping("/{id}")
    public Result<ColumnDetailVO> getColumnDetail(@PathVariable Long id) {
        log.info("获取专栏详情: id={}", id);
        ColumnDetailVO detail = columnService.getColumnDetail(id);
        if (detail == null) {
            return Result.error("专栏不存在");
        }
        // 增加阅读量
        columnService.incrementColumnReadCount(id);
        return Result.success(detail);
    }

    /**
     * 获取热门专栏列表
     *
     * @param limit 返回数量限制（默认10）
     * @return 热门专栏列表（按阅读量排序）
     */
    @GetMapping("/hot")
    public Result<List<ColumnListVO>> getHotColumns(@RequestParam(defaultValue = "10") Integer limit) {
        List<ColumnListVO> list = columnService.getHotColumns(limit);
        return Result.success(list);
    }

    /**
     * 获取当前用户的专栏列表
     *
     * @return 当前用户创建的所有专栏
     */
    @GetMapping("/my")
    @UserAccess
    public Result<List<MyColumnVO>> getMyColumns() {
        List<MyColumnVO> list = columnService.getMyColumns();
        return Result.success(list);
    }

    /**
     * 添加文章到专栏
     *
     * @param dto 包含专栏ID、文章ID和排序位置
     * @return 添加成功返回true，失败返回false
     */
    @PostMapping("/article/add")
    @UserAccess
    public Result<Boolean> addArticleToColumn(@Valid @RequestBody UpdateColumnArticleDTO dto) {
        log.info("添加文章到专栏: {}", dto);
        boolean success = columnService.addArticleToColumn(dto.getColumnId(), dto.getArticleId(), dto.getSortOrder());
        return success ? Result.success(true, "文章添加成功") : Result.error("文章添加失败");
    }

    /**
     * 从专栏移除文章
     *
     * @param columnId 专栏ID
     * @param articleId 文章ID
     * @return 移除成功返回true，失败返回false
     */
    @DeleteMapping("/article/remove")
    @UserAccess
    public Result<Boolean> removeArticleFromColumn(@RequestParam Long columnId, @RequestParam Long articleId) {
        log.info("从专栏移除文章: columnId={}, articleId={}", columnId, articleId);
        boolean success = columnService.removeArticleFromColumn(columnId, articleId);
        return success ? Result.success(true, "文章移除成功") : Result.error("文章移除失败");
    }

    /**
     * 更新专栏内文章的排序
     *
     * @param dto 包含专栏ID、文章ID和新的排序位置
     * @return 更新成功返回true，失败返回false
     */
    @PutMapping("/article/sort")
    @UserAccess
    public Result<Boolean> updateArticleSort(@Valid @RequestBody UpdateColumnArticleDTO dto) {
        log.info("更新专栏文章排序: {}", dto);
        boolean success = columnService.updateArticleSort(dto.getColumnId(), dto.getArticleId(), dto.getSortOrder());
        return success ? Result.success(true, "排序更新成功") : Result.error("排序更新失败");
    }

    /**
     * 检查文章是否已在指定专栏中
     *
     * @param columnId 专栏ID
     * @param articleId 文章ID
     * @return 文章在专栏中返回true，否则返回false
     */
    @GetMapping("/article/check")
    @UserAccess
    public Result<Boolean> checkArticleInColumn(@RequestParam Long columnId, @RequestParam Long articleId) {
        boolean inColumn = columnService.isArticleInColumn(columnId, articleId);
        return Result.success(inColumn);
    }

    /**
     * 获取文章所属的专栏信息
     *
     * @param articleId 文章ID
     * @return 文章所属的专栏关联信息（如果文章不在任何专栏中则返回null）
     */
    @GetMapping("/article/info")
    @UserAccess
    public Result<ArticleColumn> getArticleColumn(@RequestParam Long articleId) {
        ArticleColumn articleColumn = columnService.getArticleColumn(articleId);
        return Result.success(articleColumn);
    }
}
