package com.adolesce.nacos.provider.controller;


import com.adolesce.nacos.provider.domain.Goods;
import com.adolesce.nacos.provider.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Value("${server.port}")
    private int port;

    @GetMapping("/findOne/{id}")
    public Goods findOne(@PathVariable("id") int id) {

        Goods goods = goodsService.findOne(id);
        goods.setTitle(goods.getTitle() + ":" + port);
        return goods;
    }
}
