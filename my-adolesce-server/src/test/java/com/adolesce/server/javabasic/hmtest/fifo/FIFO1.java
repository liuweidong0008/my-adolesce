package com.adolesce.server.javabasic.hmtest.fifo;

import java.util.ArrayList;
import java.util.List;

public class FIFO1 {

    // 定义变量
    class DataNode {
        int value;
        DataNode prex;
        DataNode next;
    }

    private int length = 0;
    private DataNode head;
    private DataNode last;

    public void in(int i) {

        if (head == null) {
            DataNode dataNode = new DataNode();
            dataNode.value = i;
            head = dataNode;
            last = dataNode;
            length += 1;
        } else {
            if (length >= 3) {
                head.next.prex = null;
                head = head.next;
                length -= 1;
            }
            DataNode dataNode = new DataNode();
            dataNode.value = i;
            dataNode.prex = last;
            last.next = dataNode;
            last = dataNode;
            length += 1;

        }

        print();
    }

    public void print() {
        List<Integer> members = new ArrayList<Integer>();
        DataNode tmpNode = last;
        while (tmpNode != null) {
            members.add(tmpNode.value);
            tmpNode = tmpNode.prex;
        }

        System.out.println(members.toString());
    }

    public static void main(String[] args) {
        FIFO1 fifo = new FIFO1();
        fifo.in(1);
        fifo.in(2);
        fifo.in(3);
        fifo.in(4);
        fifo.in(5);
    }
}
