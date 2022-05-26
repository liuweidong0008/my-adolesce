package com.adolesce.common.enums;

import com.adolesce.common.utils.enums.EnumUtils;
import com.adolesce.common.utils.enums.StrEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author liuweidong
 * 图形验证码类型
 */
public enum CaptchaType implements StrEnum {
    PENGGLE_MATH("penggle.math", "四则运算"),
    PENGGLE_CHAR("penggle.char", "字符"),
    HUTOOL_LINE_CHAR("hutool.line.char", "线段干扰字符"),
    HUTOOL_CIRCLE_CHAR("hutool.circle.char", "圆圈干扰字符"),
    HUTOOL_SHEAR_CHAR("hutool.shear.char", "扭曲干扰字符"),
    HUTOOL_RANDOM_NUMBER("hutool.random.number", "随机数字"),
    HUTOOL_MATH("hutool.math", "四则运算");

    private String typeName;
    private String typeDesc;

    private CaptchaType(String typeName, String typeDesc) {
        this.typeName = typeName;
        this.typeDesc = typeDesc;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    @Override
    public String getId() {
        return typeName;
    }

    private static Map<String, CaptchaType> INSTANCEMAP = EnumUtils
            .mapStrEnum(CaptchaType.class);

    public static CaptchaType getByTypeName(String typeName) {
        return INSTANCEMAP.get(typeName);
    }

    public boolean eq(String typeName) {
        if (StringUtils.isBlank(typeName)) {
            return false;
        }
        return this.getId().equals(typeName);
    }
}

