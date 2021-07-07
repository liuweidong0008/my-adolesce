CREATE TABLE `account_info` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `account_name` VARCHAR(10) COLLATE utf8_bin DEFAULT NULL COMMENT '户主姓名',
  `account_no` VARCHAR(30) COLLATE utf8_bin DEFAULT NULL COMMENT '银行卡号',
  `account_password` VARCHAR(10) COLLATE utf8_bin DEFAULT NULL COMMENT '帐户密码',
  `account_balance` DECIMAL(10,0) DEFAULT NULL COMMENT '帐户余额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=INNODB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

insert into `account_info` (`id`, `account_name`, `account_no`, `account_password`, `account_balance`) values('1','张三','629098789670876','888888','100');
insert into `account_info` (`id`, `account_name`, `account_no`, `account_password`, `account_balance`) values('2','李四','629098789690909','888888','100');

