package com.adolesce.server.javabasic.hmtest.fifo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/12/7 23:15
 */
public class FIFO2 {

    class DataNode{
        private Integer item;
        private DataNode prev;
        private DataNode next;
    }

    private DataNode first;
    private DataNode last;
    private Integer size = 0;

    public void in(int i){
        DataNode node = new DataNode();
        if(last == null){
            node.item = i;
            first = node;
            last = node;
            size++;
        }else{
            if(size >=3 ){
                last.prev.next = null;
                last = last.prev;
                size--;
            }
            node.item = i;
            node.next = first;
            first.prev = node;
            first = node;
            size++;
        }
        print();
    }

    private void print(){
        List<Integer> list = new ArrayList<>();
        DataNode node = first;
        while (node != null){
            list.add(node.item);
            node = node.next;
        }
        System.out.println(list);
    }

    public static void main(String[] args) {
        FIFO2 fifo = new FIFO2();
        fifo.in(1);
        fifo.in(2);
        fifo.in(3);
        fifo.in(4);
        fifo.in(5);
    }
}
