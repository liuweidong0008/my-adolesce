package com.adolesce.server.javabasic.hmtest.meetquestion.yuanxiao.zerenlian;

public interface Handler {
   void setNext(Handler handler);
   void handle(Integer number,StringBuilder result);
}
