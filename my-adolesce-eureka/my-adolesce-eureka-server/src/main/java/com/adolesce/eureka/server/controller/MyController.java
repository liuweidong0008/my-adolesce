package com.adolesce.eureka.server.controller;

import com.adolesce.eureka.server.service.IMyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 * @version 1.0
 * @date 2021/7/19 18:57
 * @Autowire : 属于Spring
 * 1、默认按类型进行匹配，如果匹配不到，则按属性名称去匹配 (匹配容器中name与属性名相同的bean)
 * 2、required默认为true，即项目启动时找不到匹配的bean则报错，设置为false则不会
 * 3、如果容器中有两个相同类型的bean，且按属性名也匹配不到则会报错(need single,but found two)
 * 此时有如下两种方案：
 * 1）、通过结合@Qualifier注解去指定bean的名称
 * 2）、通过在其中一个bean上标注@Primary注解来指定注入该bean
 *
 * @Resource : 属于JAVA JSR-250 规范
 * 1、默认按照属性名称去匹配(匹配容器中name与属性名相同的bean)
 * 如果找不到则按类型去进行匹配
 * 如果找不到则报错
 * 如果找到了只有一个则进行注入
 * 如果找到了且不止一个，则报错
 * 2、可以单独指定按照name或者type或者同时指定name、type属性去匹配
 * 按照上述指定规则去匹配，匹配到了则成功注入，匹配不到会直接报错
 */
@RequestMapping("my")
@RestController
public class MyController {
    @Autowired
    private IMyService myServiceOne;

    @GetMapping("test1")
    public String test1() {
        return myServiceOne.getName();
    }
}
