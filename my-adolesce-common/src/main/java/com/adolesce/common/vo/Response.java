package com.adolesce.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.UnsupportedEncodingException;

/**
 * 响应体的通用封装类
 *
 * @author Wh
 * @lastModified 2015-7-1 12:01:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    /**
     * 是否成功
     */
    private Boolean success; // 是否成功
    private Object data; // 返回的数据
    private String msg; // 消息

    /**
     * 只返回成功的布尔值
     *
     * @return
     */
    public static Response success() {
        return new Response(true, null, null);
    }

    /**
     * 返回成功的布尔值和数据
     *
     * @param data
     * @return
     */
    public static Response success(Object data) {
        return new Response(true, data, null);
    }

    /**
     * 返回成功的布尔值、消息
     *
     * @param msg
     * @return
     */
    public static Response success(String msg) {
        return new Response(true, null, msg);
    }

    /**
     * 返回成功的布尔值、数据和消息
     *
     * @param data
     * @param msg
     * @return
     */
    public static Response success(Object data, String msg) {
        return new Response(true, data, msg);
    }

    /**
     * 只返回失败的布尔值
     *
     * @return
     */
    public static Response failure() {
        return new Response(false, null, null);
    }

    /**
     * 返回失败的布尔值和数据
     *
     * @param data
     * @return
     */
    public static Response failure(Object data) {
        return new Response(false, data, null);
    }

    /**
     * 返回失败的布尔值和消息
     *
     * @param msg
     * @return
     */
    public static Response failure(String msg) {
        return new Response(false, null, msg);
    }

    /**
     * 返回失败的布尔值、数据和消息
     *
     * @param data
     * @param msg
     * @return
     */
    public static Response failure(Object data, String msg) {
        return new Response(false, data, msg);
    }

    public static String attachment(String fileName, String format) {
        return attachment(fileName + "." + format);
    }

    public static String attachment(String fullFileName) {
        String encodedFileName = "";
        try {
            encodedFileName = new String(fullFileName.getBytes(), "ISO8859-1"); // 解决中文乱码问题
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "attachment; filename=\"" + encodedFileName + "\"";
    }

}
