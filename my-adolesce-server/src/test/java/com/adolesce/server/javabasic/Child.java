package com.adolesce.server.javabasic;

class Parent {
    public static String PARENT_STATIC_FIELD = "父类-静态属性";
    public int a = 99;
    // 父类-静态块
    static {
        System.out.println(PARENT_STATIC_FIELD);
        System.out.println("父类-静态代码块");
    }

    public String parentField = "父类-非静态属性";

    // 父类-非静态块（构造代码块）
    {
        System.out.println(parentField);
        System.out.println("父类-非静态代码块");
    }

    public Parent() {
        System.out.println("父类—无参构造函数");
        System.out.println(a);
    }

    void print(){
        System.out.println("parent print ..."+a);
    }
}

public class Child extends Parent {
    public static String CHILD_STATIC_FIELD = "子类-静态属性";
    private int a = 88;

    //子类-静态块
    static {
        System.out.println(CHILD_STATIC_FIELD);
        System.out.println("子类-静态代码块");
    }

    public String field = "子类-非静态属性";

    // 非静态块
    {
        System.out.println(field);
        System.out.println("子类-非静态代码块");
    }

    public Child() {
        System.out.println("子类-无参构造函数");
        System.out.println(a);
    }

    protected void print(){
        System.out.println("child print ..."+a);
    }

    public static void main(String[] args) {
        Child test1 = new Child();
        System.out.println("------------------");
        Child test2 = new Child();
        test1.print();
    }
}