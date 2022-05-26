package com.adolesce.server.javabasic.hmtest.meetquestion.yuanxiao.zerenlian;

public class Main {
    public static void main(String[] args) {
        BaseHandler handlerA = new AHandler();
        BaseHandler handlerB = new BHandler();
        BaseHandler handlerC = new CHandler();
        handlerA.setNext(handlerB);
        handlerB.setNext(handlerC);
        for (int i = 1; i <= 100; i++) {
            StringBuilder str = new StringBuilder();
            handlerA.handle(i,str);
            System.out.println(i+"=>"+str);
        }
    }
}
