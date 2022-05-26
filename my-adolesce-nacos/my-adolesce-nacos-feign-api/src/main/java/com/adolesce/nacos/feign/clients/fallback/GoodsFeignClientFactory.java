package com.adolesce.nacos.feign.clients.fallback;

import com.adolesce.nacos.feign.clients.GoodsFeignClient;
import com.adolesce.nacos.feign.domain.Goods;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GoodsFeignClientFactory implements FallbackFactory<GoodsFeignClient> {
    @Override
    public GoodsFeignClient create(Throwable throwable) {
        return new GoodsFeignClient() {
            @Override
            public Goods findGoodsById(int id) {
                //使用Factory方式好处：可以处理异常
                log.error("查询商品信息异常", throwable);
                Goods goods = new Goods();
                goods.setTitle("通过客户端降级了~~~");
                return goods;
            }
        };
    }
}
