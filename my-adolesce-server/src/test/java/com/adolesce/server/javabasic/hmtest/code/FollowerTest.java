package com.adolesce.server.javabasic.hmtest.code;

import java.util.ArrayList;
import java.util.List;

class Follower{
    private String name;
    public Follower(String name){
        this.name = name;
    }
    public void see(String msg){
        System.out.println(name + " receive: "+msg);
    }
    public void listen(Leader leader){
        leader.add(this);
    }
    public void bye(Leader leader){
        leader.remove(this);
    }
}

class Leader{
    //1、定义变量
    private List<Follower> followers = new ArrayList<>();


    public void publish(String msg){
        //2、完成方法
        followers.stream().forEach(follower -> follower.see(msg));
    }

    public void add(Follower follower){
        //3、完成方法
        followers.add(follower);
    }

    public void remove(Follower follower){
        //4、完成方法
        followers.remove(follower);
    }
}

public class FollowerTest{
    public static void main(String[] args){
        Follower follower1 = new Follower("follower1");
        Follower follower2 = new Follower("follower2");
        Follower follower3 = new Follower("follower3");
        Follower follower4 = new Follower("follower4");
        Follower follower5 = new Follower("follower5");

        Leader leader1 = new Leader();
        Leader leader2 = new Leader();

        follower1.listen(leader1);
        follower2.listen(leader1);
        follower3.listen(leader1);

        follower4.listen(leader2);
        follower5.listen(leader2);

        leader1.publish("from leader1 , msg1 ");
        System.out.println("---1---");
        leader2.publish("from leader2 , msg1 ");
        System.out.println("===2===");

        follower1.bye(leader1);
        follower1.listen(leader2);

        leader1.publish("from leader1 , msg2");
        System.out.println("---3---");
        leader2.publish("from leader2 , msg2");
    }
}