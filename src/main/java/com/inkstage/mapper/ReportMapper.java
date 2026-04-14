package com.inkstage.mapper;

import com.inkstage.dto.admin.AdminReportQueryDTO;
import com.inkstage.entity.model.Report;
import com.inkstage.enums.ReportStatus;
import com.inkstage.enums.ReportTargetType;
import com.inkstage.enums.ReportTypeEnum;
import com.inkstage.vo.front.ReportListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 举报Mapper接口
 */
@Mapper
public interface ReportMapper {

    /**
     * 插入举报
     *
     * @param report 举报对象
     */
    void insert(Report report);

    /**
     * 根据ID查询举报
     *
     * @param id 举报ID
     * @return 举报对象
     */
    Report selectById(Long id);


    /**
     * 根据用户ID和举报状态查询举报列表
     *
     * @param userId       用户ID
     * @param reportStatus 举报状态
     * @param limit        偏移量
     * @param offset       限制数
     * @return 举报列表
     */
    List<ReportListVO> selectList(@Param("userId") Long userId,
                                  @Param("reportStatus") ReportStatus reportStatus,
                                  @Param("pageNum") int limit,
                                  @Param("offset") int offset);

    /**
     * 根据举报人ID、被举报对象ID、相关对象ID、被举报对象类型和举报类型统计举报数量
     *
     * @param reporterId   举报人ID
     * @param reportedId   被举报对象ID
     * @param relatedId    相关对象ID
     * @param reportedType 被举报对象类型
     * @param reportType   举报类型
     * @return 举报数量
     */
    int count(@Param("reporterId") Long reporterId,
              @Param("reportedId") Long reportedId,
              @Param("relatedId") Long relatedId,
              @Param("reportedType") ReportTargetType reportedType,
              @Param("reportType") ReportTypeEnum reportType);

    /**
     * 更新举报
     *
     * @param report 举报对象
     */
    void update(Report report);


    /**
     * 根据状态统计举报数量
     *
     * @param userId       用户ID
     * @param reportStatus 举报状态
     * @return 举报数量
     */
    int countByStatus(@Param("userId") Long userId, @Param("reportStatus") ReportStatus reportStatus);

    /**
     * 根据管理员查询条件查询举报数量
     *
     * @param adminReportQueryDTO 管理员查询参数
     * @return 举报数量
     */
    int countByAdminQuery(@Param("adminQueryDTO") AdminReportQueryDTO adminReportQueryDTO);

    /**
     * 根据管理员查询举报列表
     *
     * @param adminReportQueryDTO 管理员查询参数
     * @return 举报列表
     */
    List<ReportListVO> selectAdminReportList(@Param("adminQueryDTO") AdminReportQueryDTO adminReportQueryDTO);
}
