package com.adolesce.server.controller;

import com.adolesce.common.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class FreemarkerController {

    @GetMapping("/basic")
    public ModelAndView hello(ModelAndView modelAndView){

        //name
//        model.addAttribute("name","freemarker");
        //stu
        User user = new User();
        user.setUserName("小明");
        user.setAge(18);
        modelAndView.addObject("user",user);
        modelAndView.setViewName("01-basic");

        return modelAndView;
    }

    /*@GetMapping("/list")
    public String list(Model model){
        //------------------------------------
        User user1 = new User();
        user1.setUserName("小强");
        user1.setAge(18);
        user1.setMoney(1000.86f);
        user1.setBirthday(new Date());

        //小红对象模型数据
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);

        //将两个对象模型数据存放到List集合中
        List<Student> stus = new ArrayList<>();
        stus.add(user1);
        stus.add(stu2);

        //向model中存放List集合数据
        model.addAttribute("stus",stus);

        //map数据
        Map<String,Student> stuMap = new HashMap<>();
        stuMap.put("user1",user1);
        stuMap.put("stu2",stu2);

        model.addAttribute("stuMap",stuMap);
        //日期
        model.addAttribute("today",new Date());

        //长数值
        model.addAttribute("point",38473897438743L);

        return "02-list";
    }*/

}
