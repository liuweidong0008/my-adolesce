package com.adolesce.server.javabasic.hmtest.meetquestion.yuanxiao.zerenlian;

public abstract class BaseHandler{

    protected BaseHandler next;

    public void setNext(BaseHandler handler) {
        this.next = handler;
    }
    public void handle(Integer number,StringBuilder result){
        handlerSub(number,result);
        if (next!=null){
            next.handle(number,result);
        }
    }

    /**
     *  留给子类扩展实现
     * @param number
     * @param result
     */
    protected abstract void handlerSub(Integer number, StringBuilder result);
}

class AHandler extends BaseHandler {
    @Override
    protected void handlerSub(Integer number, StringBuilder result) {
        result.append(number % 3 == 0 ? "A" : "");
    }
}

class BHandler extends BaseHandler{
    @Override
    protected void handlerSub(Integer number, StringBuilder result) {
        result.append(number % 5 == 0 ? "B" : "");
    }
}

class CHandler extends BaseHandler{
    @Override
    protected void handlerSub(Integer number, StringBuilder result) {
        result.append(number % 7 == 0 ? "C" : "");
    }
}


