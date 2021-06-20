package com.adolesce.server.tanhuaquestion;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TanhuaQuestion {
    //问题编号
    private Integer no;
    //问题
    private String question;
    //回答
    private String answer;
    //是否父结点
    private Boolean isParent;
    private List<TanhuaQuestion> childQuestion = new ArrayList<>();

}
