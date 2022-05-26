package com.adolesce.server.demo;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 */
public class Parent {
    private int age = 25;

    public Parent() {
        super(); //注：不写也可以，默认就会调用
        printAge();
    }

    public void printAge(){
        System.out.println("Parent Age:" + age);
    }
}

class Child extends Parent{
    private int age = 1;

    public Child() {
        super(); //注：不写也可以，默认就会调用
        printAge();
    }

    public void printAge(){
        System.out.println("Child Age:" + this.age);
    }
}
