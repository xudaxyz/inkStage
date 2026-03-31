package com.inkstage.handler;

import com.inkstage.enums.*;
import com.inkstage.enums.article.ArticleStatus;
import com.inkstage.enums.article.OriginalStatus;
import com.inkstage.enums.article.RecommendStatus;
import com.inkstage.enums.article.TopStatus;
import com.inkstage.enums.common.*;
import com.inkstage.enums.notification.NotificationType;
import com.inkstage.enums.user.Gender;
import com.inkstage.enums.user.UserRoleEnum;
import com.inkstage.enums.user.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 整数类型枚举处理器, 处理带code字段的枚举
 * 支持：INSERT、UPDATE、SELECT操作
 * 映射：枚举.code → 数据库INT/TINYINT字段
 * 适用于：gender、status、privacy等整数类型的枚举字段
 */
@Slf4j
@MappedTypes(value = {
        AllowStatus.class, AnnouncementType.class, ArticleStatus.class,
        ConfigType.class, DefaultStatus.class, DeleteStatus.class, FeedbackStatus.class,
        Gender.class, HandleResultEnum.class, MessageType.class, NotificationType.class,
        OriginalStatus.class, Priority.class, PushedStatus.class, ReadStatus.class,
        RecommendStatus.class, RecommendType.class, ReportStatus.class, ReportTargetType.class,
        ReportTypeEnum.class, ReviewStatus.class, SearchType.class, StatusEnum.class,
        TemplateType.class, TopStatus.class, UserStatus.class, UserRoleEnum.class,
        VerificationStatus.class, VisibleStatus.class
})
@MappedJdbcTypes({JdbcType.TINYINT, JdbcType.INTEGER})
public class EnumCodeTypeHandler<E extends Enum<E> & EnumCode> extends BaseTypeHandler<E> {

    private final Class<E> enumClass;

    /**
     * 构造方法
     *
     * @param enumClass 枚举类型
     */
    public EnumCodeTypeHandler(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    /**
     * 设置参数, 用于INSERT/UPDATE操作
     * 将枚举转换为数据库字段值
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.getCode());
    }


    /**
     * 从ResultSet获取值, 用于SELECT操作(按列名)
     * 将数据库字段值转换为枚举
     */
    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            int code = rs.getInt(columnName);
            return rs.wasNull() ? null : getEnumByCode(code);
        } catch (Exception e) {
            log.error("获取列值失败: {}", e.getMessage(), e);
            throw new SQLException(e);
        }
    }

    /**
     * 从ResultSet获取值, 用于SELECT操作(按列索引)
     */
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return getEnumByCode(rs.getObject(columnIndex));
        } catch (Exception e) {
            log.error("获取列值失败: {}", e.getMessage(), e);
            throw new SQLException(e);
        }
    }

    /**
     * 从CallableStatement获取值, 用于存储过程
     */
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return getEnumByCode(cs.getObject(columnIndex));
        } catch (Exception e) {
            log.error("获取列值失败: {}", e.getMessage(), e);
            throw new SQLException(e);
        }
    }

    private E getEnumByCode(Object code) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

}