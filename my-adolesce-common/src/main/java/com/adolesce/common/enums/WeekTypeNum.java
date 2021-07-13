package com.adolesce.common.enums;

import com.adolesce.common.utils.enums.EnumUtils;
import com.adolesce.common.utils.enums.IntEnum;
import org.springframework.util.ObjectUtils;

import java.util.Map;

public enum WeekTypeNum implements IntEnum {
	MONDAY(1,"星期一"),
	TUESDAY(2,"星期二"),
	WEDNESDAY(3,"星期三"),
	THURSDAY(4,"星期四"),
	FRIDAY(5,"星期五"),
	SATURDAY(6,"星期六"),
	SUNDAY(7,"星期天");
	
	private Integer codeId;
	private String codeName;
	
	private WeekTypeNum(Integer codeId, String codeName) {
		this.codeId = codeId;
		this.codeName = codeName;
	}

	public String getCodeName() {
		return codeName;
	}

	@Override
	public Integer getId() {
		return this.codeId;
	}
	
	private static Map<Integer, WeekTypeNum> INSTANCEMAP = EnumUtils
			.mapIntEnum(WeekTypeNum.class);
	
	public static WeekTypeNum getByCodeId(Integer codeId) {
		return INSTANCEMAP.get(codeId);
	}
	
	public boolean eq(Integer codeId){
		if(ObjectUtils.isEmpty(codeId)){
			return false;
		}
		return this.getId().equals(codeId);
	}
}
