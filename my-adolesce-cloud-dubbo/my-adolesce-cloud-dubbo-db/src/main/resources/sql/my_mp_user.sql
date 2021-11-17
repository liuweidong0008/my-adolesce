CREATE TABLE `my_mp_user`
(
    `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_name`   varchar(20) NOT NULL COMMENT '用户名',
    `password`    varchar(20) NOT NULL COMMENT '密码',
    `name`        varchar(30) DEFAULT NULL COMMENT '姓名',
    `age`         int(11)     DEFAULT NULL COMMENT '年龄',
    `email`       varchar(50) DEFAULT NULL COMMENT '邮箱',
    `birthday`    date        DEFAULT NULL COMMENT '生日',
    `create_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime    DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8;

insert into `my_mp_user` (`id`, `user_name`, `password`, `name`, `age`, `email`, `birthday`, `create_time`,
                          `update_time`)
values ('1', 'zhangsan', '123456', '张三', '18', 'test1@itcast.cn', '2013-02-08', '2021-05-04 02:00:49',
        '2021-05-06 02:01:09');
insert into `my_mp_user` (`id`, `user_name`, `password`, `name`, `age`, `email`, `birthday`, `create_time`,
                          `update_time`)
values ('2', 'lisi', '123456', '李四', '20', 'test2@itcast.cn', '2007-02-19', '2021-05-05 02:00:53',
        '2021-05-05 02:01:12');
insert into `my_mp_user` (`id`, `user_name`, `password`, `name`, `age`, `email`, `birthday`, `create_time`,
                          `update_time`)
values ('3', 'wangwu', '123456', '王五', '28', 'test3@itcast.cn', '2004-01-19', '2021-05-04 02:00:56',
        '2021-05-01 02:01:16');
insert into `my_mp_user` (`id`, `user_name`, `password`, `name`, `age`, `email`, `birthday`, `create_time`,
                          `update_time`)
values ('4', 'zhaoliu', '123456', '赵六', '20', 'test4@itcast.cn', '2016-01-27', '2021-05-07 02:00:59',
        '2021-05-16 02:01:19');
insert into `my_mp_user` (`id`, `user_name`, `password`, `name`, `age`, `email`, `birthday`, `create_time`,
                          `update_time`)
values ('5', 'sunqi', '123456', '孙七', '24', 'test5@itcast.cn', '2015-07-09', '2021-05-09 02:01:02',
        '2021-05-03 02:01:21');
insert into `my_mp_user` (`id`, `user_name`, `password`, `name`, `age`, `email`, `birthday`, `create_time`,
                          `update_time`)
values ('6', 'itcast', '1111111', '赵晓雅', '18', NULL, '2013-11-19', '2021-05-07 02:01:05', '2021-05-16 02:01:24');
