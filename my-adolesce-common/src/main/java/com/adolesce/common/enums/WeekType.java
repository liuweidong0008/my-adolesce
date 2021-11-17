package com.adolesce.common.enums;

import com.adolesce.common.utils.enums.EnumUtils;
import com.adolesce.common.utils.enums.StrEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author liuweidong
 */
public enum WeekType implements StrEnum {
    MONDAY("001", "星期一"),
    TUESDAY("002", "星期二"),
    WEDNESDAY("003", "星期三"),
    THURSDAY("004", "星期四"),
    FRIDAY("005", "星期五"),
    SATURDAY("006", "星期六"),
    SUNDAY("007", "星期天");

    private String codeId;
    private String codeName;

    private WeekType(String codeId, String codeName) {
        this.codeId = codeId;
        this.codeName = codeName;
    }

    public String getCodeId() {
        return this.codeId;
    }

    public String getCodeName() {
        return codeName;
    }

    @Override
    public String getId() {
        return codeId;
    }

    private static Map<String, WeekType> INSTANCEMAP = EnumUtils
            .mapStrEnum(WeekType.class);

    public static WeekType getByCodeId(String codeId) {
        return INSTANCEMAP.get(codeId);
    }

    public boolean eq(String codeId) {
        if (StringUtils.isBlank(codeId)) {
            return false;
        }
        return this.getId().equals(codeId);
    }
}

