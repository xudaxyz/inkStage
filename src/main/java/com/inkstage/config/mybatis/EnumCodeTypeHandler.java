package com.inkstage.config.mybatis;

import com.inkstage.enums.EnumCode;
import com.inkstage.enums.*;
import com.inkstage.enums.VerificationStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 整数类型枚举处理器，处理带code字段的枚举
 * 支持：INSERT、UPDATE、SELECT操作
 * 映射：枚举.code → 数据库INT/TINYINT字段
 * 适用于：gender、status、privacy等整数类型的枚举字段
 */
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

    private final Class<E> type;

    /**
     * 构造方法
     *
     * @param type 枚举类型
     */
    public EnumCodeTypeHandler(Class<E> type) {
        this.type = type;
    }

    /**
     * 设置参数，用于INSERT/UPDATE操作
     * 将枚举转换为数据库字段值
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    /**
     * 从ResultSet获取值，用于SELECT操作（按列名）
     * 将数据库字段值转换为枚举
     */
    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : getEnumByCode(code);
    }

    /**
     * 从ResultSet获取值，用于SELECT操作（按列索引）
     */
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : getEnumByCode(code);
    }

    /**
     * 从CallableStatement获取值，用于存储过程
     */
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : getEnumByCode(code);
    }

    /**
     * 根据code获取枚举实例
     * 支持通过fromCode方法或遍历枚举常量查找
     */
    private E getEnumByCode(int code) {
        for (E enumValue : type.getEnumConstants()) {
            if (enumValue.getCode() == code) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code + " for enum: " + type.getName());
    }
}