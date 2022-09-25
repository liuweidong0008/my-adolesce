package com.adolesce.server.meetquestion;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MeetQuestion {
    //问题编号
    private Integer no;
    //问题
    private String question;
    //回答
    private String answer;
    //是否父结点
    private Boolean isParent;
    private List<MeetQuestion> childQuestion = new ArrayList<>();

}
