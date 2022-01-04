package com.adolesce.server.javabasic.hmtest.fifo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/12/7 21:00
 */
public class FIFO {
    private Integer[] numarr;
    private Integer length;

    public FIFO(Integer length) {
        this.numarr = new Integer[length];
        this.length = length;
    }

    public FIFO() {
    }

    public void in(int i) {
        System.arraycopy(numarr,0,numarr,1,length-1);
        numarr[0] = i;
        print();
    }

    public void print(){
        List<Integer> collect = Arrays.asList(numarr).stream().filter(n -> n != null).collect(Collectors.toList());
        System.out.println(collect);
    }

    public static void main(String[] args) {
        FIFO fifo = new FIFO(3);
        fifo.in(1);
        fifo.in(2);
        fifo.in(3);
        fifo.in(4);
        fifo.in(5);
        fifo.in(6);
        fifo.in(7);
        fifo.in(8);
        fifo.in(9);
        fifo.in(10);
        fifo.in(11);
    }
}
