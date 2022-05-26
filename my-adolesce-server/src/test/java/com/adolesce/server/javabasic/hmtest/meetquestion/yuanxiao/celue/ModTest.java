package com.adolesce.server.javabasic.hmtest.meetquestion.yuanxiao.celue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/5/16 13:27
 */
public class ModTest {
    private static Map<Integer, Mod> modMap = new HashMap<>();
    static{
        modMap.put(3,(source,target) -> source%target == 0? "A":"");
        modMap.put(5,(source,target) -> source%target == 0? "B":"");
        modMap.put(7,(source,target) -> source%target == 0? "C":"");
        modMap.put(9,(source,target) -> source%target == 0? "D":"");
    }

    public static void main(String[] args) {
        int source = 0;
        while(source <= 100){
            System.out.println(source+"转换后的结果：" + calculate(source));
            source++;
        }
    }

    private static String calculate(int source){
        StringBuffer stringBuffer = new StringBuffer();
        for(Integer type:modMap.keySet()){
            stringBuffer.append(startCalculate(modMap.get(type),source,type));
        }
        return stringBuffer.toString();
    }

    private static String startCalculate(Mod mod, int source, int target){
       return mod.calculate(source,target);
    }
}
