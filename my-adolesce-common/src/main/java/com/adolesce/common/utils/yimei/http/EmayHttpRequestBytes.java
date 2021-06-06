package com.adolesce.common.utils.yimei.http;

import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * 传输数据为byte[]的请求实体
 * 
 * @author Frank
 *
 */
public class EmayHttpRequestBytes extends EmayHttpRequest<byte[]> {

	public EmayHttpRequestBytes(String url, String charSet, String method, Map<String, String> headers, String cookies, byte[] params) {
		super(url, charSet, method, headers, cookies, params);
	}

	@Override
	public byte[] paramsToBytesForPost() {
		return this.getParams();
	}

	@Override
	public String paramsToStringForGet() {
		try {
			return new String(this.getParams(),this.getCharSet());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}


}
