package com.adolesce.common.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 响应结果码
 */
public enum ResultCode {
		SUCCESS("1", "操作成功！"),
		FAILURE("0", "操作失败！"),
		
		PARAM_LACK("102", "请求缺少必要参数！"), 
		AUTH_LIMIT("106", "接口访问权限受限！"), 
		OP_FREQUENTLY("107", "操作频繁！"), 
		SYS_BUSY("108", "系统繁忙！"), 
		USER_NOT_EXIST("203", "用户不存在！"), 
		PASSWORD_ERROR("204", "用户密码错误！"), 
		VERIFICATION_CODE_ERROR("205", "验证码错误！"), 
		OBJECT_NOT_EXIST("206", "查询对象不存在！"), 
		BUSINESSFAILED("212","业务异常"),
		SYSTERM_MAINTAIN("301","系统维护");

		public String key;
		public String value;

		private ResultCode(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public static ResultCode getByKey(String key) {
			ResultCode[] values = ResultCode.values();
			for (ResultCode object : values) {
				if (object.key.equals(key)) {
					return object;
				}
			}
			return null;
		}

		public boolean eq(String key){
			if(StringUtils.isBlank(key)){
				return false;
			}
			return this.toString().equals(key);
		}

		public String getValue() {
			return this.value;
		}

		@Override
		public String toString() {
			return key;
		}
	}