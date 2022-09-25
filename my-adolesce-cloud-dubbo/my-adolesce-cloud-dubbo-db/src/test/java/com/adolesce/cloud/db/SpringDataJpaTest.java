package com.adolesce.cloud.db;

import com.adolesce.cloud.db.repository.JpaAddressRepository;
import com.adolesce.cloud.db.repository.JpaIdentityRepository;
import com.adolesce.cloud.db.repository.JpaUserRepository;
import com.adolesce.cloud.dubbo.domain.db.JpaAddress;
import com.adolesce.cloud.dubbo.domain.db.JpaIdentity;
import com.adolesce.cloud.dubbo.domain.db.JpaUser;
import com.adolesce.cloud.dubbo.domain.db.JpaUserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * springBoot jpa集成步骤
 *  1、pom依赖mysql-connector-java、com.alibaba druid、spring-boot-starter-data-jpa
 *  2、yml加上spring.datasource.druid配置
 *  3、定义实体JpaUser，表名、字段映射
 *  4、定义JpaUserRepository，继承JpaRepository<JpaUser, Long>
 *  5、启动类增加实体扫描 @EntityScan("com.adolesce.cloud.dubbo.domain")
 *
 * spring data jpa 操作测试
 *
 * spring data jpa 使用方式有如下三种
 *  1、框架基础操作API
 *  2、自定义简单查询（根据方法名自动生成SQL）
 *  3、自定义SQL查询
 *      1）、JPQL方式
 *      2）、Native方式
 *
 * 多表查询方式有如下两种
 *  1、使用jpa框架自带的的@OneToOne、@OneToMany、@ManyToMany进行查询
 *  2、使用自定义JPQL/Native SQL进行查询(返回结果用接口进行封装，提供属性的get方法)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataJpaTest {
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaAddressRepository jpaAddressRepository;
    @Autowired
    private JpaIdentityRepository jpaIdentityRepository;

    /**
     * 一、基础操作
     *
     * 	框架已定义的基本方法
     * 	save(S entity);  --
     * 	saveAll(Iterable<S> entities);   --
     * 	saveAndFlush(S entity);
     *	BOOLEAN existsById(ID id);  --
     *  BOOLEAN EXISTS(EXAMPLE<S> EXAMPLE)  --
     *  Optional<T> findById(ID id);  --
     *  Optional<S> findOne(EXAMPLE<S> EXAMPLE) --
     *  LIST<T> findAllById(Iterable<ID> ids); --
     *	LIST<T> findAll();  --
     * 	LIST<T> findAll(Sort sort); --
     * 	LIST<S> findAll(EXAMPLE<S> EXAMPLE);  --
     * 	LIST<S> findAll(EXAMPLE<S> EXAMPLE, Sort sort);  --
     * 	Page<T> findAll(Pageable pageable);
     * 	Page<S> findAll(EXAMPLE<S> EXAMPLE, Pageable pageable)  --
     * 	LONG COUNT();  --
     * 	void deleteById(ID id); --
     * 	void DELETE(T entity);	--
     * 	void deleteAll(Iterable<? extends T> entities); --
     * 	void deleteAll();  --
     * 	void deleteInBatch(Iterable<T> entities); --
     * 	void deleteAllInBatch();
     * 	T getOne(ID id); --
     * 	LONG COUNT(EXAMPLE<S> EXAMPLE) --
     */

    /**
     * 单个插入
     */
    @Test
    public void singleSave() {
        JpaUser user = new JpaUser();
        user.setUserName("zhaoyun");
        user.setName("赵云11");
        user.setPassword("123");
        user.setSex(1);
        user.setAge(Integer.valueOf(12));
        user.setPhone("18800000000");
        user.setBirthday(new Date());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        this.jpaUserRepository.save(user);
        //this.jpaUserRepository.saveAndFlush(user);
    }

    /**
     * 批量插入
     */
    @Test
    public void saveAll() {
        List<JpaUser> usersList = new ArrayList<>();
        JpaUser user;
        for (int i = 1; i <= 3; i++) {
            user = new JpaUser();
            user.setUserName("u" + i);
            user.setPassword("p" + i);
            user.setAge(i);
            user.setBirthday(new Date());
            usersList.add(user);
        }
        this.jpaUserRepository.saveAll(usersList);
    }

    /**
     * 根据实体删除
     */
    @Test
    public void delete() {
        Optional<JpaUser> users = this.jpaUserRepository.findById(23L);
        this.jpaUserRepository.delete(users.get());
    }

    /**
     * 根据ID删除
     */
    @Test
    public void deleteById() {
        this.jpaUserRepository.deleteById(1L);
    }

    /**
     * 删除所有、条件批量删除
     */
    @Test
    public void deleteAll() {
        //this.jpaUserRepository.deleteAll();
        List<JpaUser> users = new ArrayList<>();
        JpaUser user = new JpaUser();
        user.setId(24L);
        users.add(user);
        user = new JpaUser();
        user.setId(25L);
        users.add(user);

        this.jpaUserRepository.deleteInBatch(users);
        //this.jpaUserRepository.deleteAll(users);
    }

    /**
     * 根据ID进行修改
     */
    @Test
    public void edit() {
        JpaUser user = this.jpaUserRepository.findById(108L).get();
        user.setAge(Integer.valueOf(18));
        user.setPassword("123456");
        this.jpaUserRepository.save(user);
    }

    /**
     * 查询某个ID是否存在
     */
    @Test
    public void existsById() {
        Boolean result = this.jpaUserRepository.existsById(40L);
        System.err.println(result);
    }

    /**
     * 按Example条件查询是否存在
     * 对于非字符串属性的只能精确匹配，比如想查询在某个时间段内注册的用户信息，就不能通过Example来查询
     */
    @Test
    public void exists() {
        JpaUser user = new JpaUser();
        user.setUserName("zhang");
        user.setPassword("56");
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("userName", match -> match.startsWith()) //%userName
                .withMatcher("password", match -> match.contains()); //%passWord%
        //Example<Users> example = Example.of(users);
        Example<JpaUser> example = Example.of(user, matcher);
        Boolean result = this.jpaUserRepository.exists(example);

        long count = this.jpaUserRepository.count(example);
        System.err.println(result+":"+count);
    }

    /**
     * 查询所有记录条数
     */
    @Test
    public void count() {
        long result = this.jpaUserRepository.count();
        System.err.println(result);
    }

    /**
     * 按Example条件查询结果条数
     */
    @Test
    public void countByExample() {
        JpaUser users = new JpaUser();
        users.setUserName("zhangsan");
        Example<JpaUser> example = Example.of(users);
        long result = this.jpaUserRepository.count(example);
        System.err.println(result);
    }

    /**
     * 查询所有结果集
     */
    @Test
    public void findAll() {
        List<JpaUser> usersList = this.jpaUserRepository.findAll();
        System.err.println(usersList);
    }

    /**
     * 排序查询所有
     */
    @Test
    public void findAllAndSort() {
        Sort sort = Sort.by(Sort.Direction.DESC, new String[]{"age"});
        Sort.Order order = new Sort.Order(Sort.Direction.DESC,"123");
        List<JpaUser> usersList = this.jpaUserRepository.findAll(sort);
        System.err.println(usersList);
    }

    /**
     * 按Example条件查询结果集
     */
    @Test
    public void findAllByExample() {
        JpaUser users = new JpaUser();
        users.setUserName("zhangsan");
        Example<JpaUser> example = Example.of(users);
        List<JpaUser> usersList = this.jpaUserRepository.findAll(example);
        System.err.println(usersList);
    }

    /**
     * 按Example条件查询结果集并排序
     */
    @Test
    public void findAllByExampleAndSort() {
		//Order order1 = new Order(Direction.DESC,"age");
		//Order order2 = new Order(Direction.ASC,"username");
		//Sort sort = new Sort(order1,order2);
        //Sort sort = Sort.by(Sort.Direction.DESC, new String[]{"age"});

        Sort sort = Sort.by("age");  //正序

        JpaUser users = new JpaUser();
        users.setPassword("123456");
        Example<JpaUser> example = Example.of(users);
        List<JpaUser> usersList = this.jpaUserRepository.findAll(example, sort);
        System.err.println(usersList);
    }

    /**
     * 按Example条件分页查询结果集
     */
    @Test
    public void findAllByExampleAndPage() {
        Sort sort = Sort.by(Sort.Order.desc("age"));
        Pageable pageable = PageRequest.of(0,3,sort);

        JpaUser users = new JpaUser();
        users.setUserName("zhaoyun");
        Example<JpaUser> example = Example.of(users);
        Page<JpaUser> page = this.jpaUserRepository.findAll(example,pageable);
        System.err.println(page);
    }

    /**
     * 根据ID集合查询结果集
     */
    @Test
    public void findAllByIds() {
        Long[] ids = {1L, 2L, 3L};
        List<JpaUser> usersList = this.jpaUserRepository.findAllById(Arrays.asList(ids));
        System.err.println(usersList);
    }

    /**
     * 根据ID查询实体
     */
    @Test
    public void findById() {
        JpaUser users = this.jpaUserRepository.findById(108L).get();
        System.err.println(users);
    }

    @Test
    public void getById() {
        JpaUser users = this.jpaUserRepository.getOne(108L);
        System.err.println(users);
    }

    /**
     * 根据Example条件查询单条结果集
     */
    @Test
    public void findOneByExample() {
        JpaUser users = new JpaUser();
        users.setUserName("zhangfei");
        Example<JpaUser> example = Example.of(users);
        JpaUser user = this.jpaUserRepository.findOne(example).get();
        System.err.println(user);
    }

    /**
     *	二、自定义简单查询
     *	自定义的简单查询就是根据方法名来自动生成SQL，主要的语法是find|read|query|get xxx  by  paramName		countByParamName
     *  User findByUserName(String userName);
     *  也可以使用一些关键字And、 Or
     *
     *	User findByUserNameOrEmail(String username, String email);
     *	修改、删除、统计也是类似语法
     *
     *	Long deleteById(Long id);
     *
     *	Long countByUserName(String userName)
     *	基本上SQL体系中的关键词都可以使用，例如：LIKE、 IgnoreCase、 OrderBy。
     *
     *	List<User> findByEmailLike(String email);
     *
     *	User findByUserNameIgnoreCase(String userName);
     *
     *	List<User> findByUserNameOrderByEmailDesc(String email);
     *	具体的关键字，使用方法和生产成SQL如下表所示
     *
     *	Keyword					Sample									JPQL snippet
     *	And						findByLastnameAndFirstname				… where x.lastname = ?1 and x.firstname = ?2
     *	Or						findByLastnameOrFirstname				… where x.lastname = ?1 or x.firstname = ?2
     *	Is,Equals				findByFirstnameIs,findByFirstnameEquals	… where x.firstname = ?1
     *	Between					findByStartDateBetween					… where x.startDate between ?1 and ?2
     *	LessThan				findByAgeLessThan						… where x.age < ?1
     *	LessThanEqual			findByAgeLessThanEqual					… where x.age <= ?1
     *	GreaterThan				findByAgeGreaterThan					… where x.age > ?1
     *	GreaterThanEqual		findByAgeGreaterThanEqual				… where x.age >= ?1
     *	After					findByStartDateAfter					… where x.startDate > ?1
     *	Before					findByStartDateBefore					… where x.startDate < ?1
     *	IsNull					findByAgeIsNull							… where x.age is null
     *	IsNotNull,NotNull		findByAge(Is)NotNull					… where x.age not null
     *	Like					findByFirstnameLike						… where x.firstname like ?1
     *	NotLike					findByFirstnameNotLike					… where x.firstname not like ?1
     *	StartingWith			findByFirstnameStartingWith				… where x.firstname like '?%'
     *	EndingWith				findByFirstnameEndingWith				… where x.firstname like '%?'
     *	Containing				findByFirstnameContaining				… where x.firstname like '%?%'
     *	OrderBy					findByAgeOrderByLastnameDesc			… where x.age = ?1 order by x.lastname desc
     *	Not						findByLastnameNot						… where x.lastname <> ?1
     *	In						findByAgeIn(Collection ages)			… where x.age in ?1
     *	NotIn					findByAgeNotIn(Collection age)			… where x.age not in ?1
     *	TRUE					findByActiveTrue()						… where x.active = true
     *	FALSE					findByActiveFalse()						… where x.active = false
     *	IgnoreCase				findByFirstnameIgnoreCase				… where UPPER(x.firstame) = UPPER(?1)
     */

    /**
     * 根据姓名查询
     */
    @Test
    public void findByUserName() {
        JpaUser users = this.jpaUserRepository.findByUserName("zhangsan");
        System.err.println(users);
    }

    /**
     * 根据姓名和年龄查询
     */
    @Test
    public void findByUserNameAndAge() {
        JpaUser users = this.jpaUserRepository.findByUserNameAndAge("zhaoyun", 18);
        System.err.println(users);
    }

    /**
     * 根据姓名、年龄和密码查询
     */
    @Test
    public void findByUserNameAndAgeAndPassword() {
        JpaUser users = this.jpaUserRepository.findByUserNameAndAgeAndPassword("zhangsan", 18, "123456");
        System.err.println(users);
    }

    /**
     * 根据 姓名或（姓名和年龄）查询【and 和 or 结合使用时注意sql解析顺序，and的解析优先级比or更高】
     * 以下例子sql: select * from user where user_name = '' or (user_name = '' and age = '')
     */
    @Test
    public void findByUserNameOrUserNameAndAge() {
        List<JpaUser> users = this.jpaUserRepository.findByUserNameOrUserNameAndAge("zhangsan", "wangwu", 20);
        System.err.println(users);
    }

    /**
     * 根据姓名查询结果条数
     */
    @Test
    public void countByUserName() {
        long count = this.jpaUserRepository.countByUserName("zhangsan");
        System.err.println(count);
    }

    /**
     * 根据姓名右模糊查询
     */
    @Test
    public void findByUserNameLike() {
        List<JpaUser> users = this.jpaUserRepository.findByUserNameLike("zhao%");
        System.err.println(users);
    }

    /**
     * 根据姓名忽略大写小查询
     */
    @Test
    public void findByUserNameIgnoreCase() {
        List<JpaUser> users = this.jpaUserRepository.findByUserNameIgnoreCase("ZHANGSAN");
        System.err.println(users);
    }

    /**
     * 根据姓名忽略大小写 或者 密码全模糊查询，根据年龄倒序排
     */
    @Test
    public void findByUserNameIgnoreCaseOrPasswordLikeOrderByAgeDesc() {
        List<JpaUser> users = this.jpaUserRepository.findByUserNameIgnoreCaseOrPasswordLikeOrderByAgeDesc("ZHANGSAN", "%23%");
        System.err.println(users);
    }

    /**
     * 根据生日精确查询
     */
    @Test
    public void findByBirthdayIs() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date birthday = dateFormat.parse("2004-01-19");
        List<JpaUser> users = this.jpaUserRepository.findByBirthdayIs(birthday);
        System.err.println(users);
    }

    /**
     * 根据生日区间查询
     */
    @Test
    public void findByBirthdayBetween() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startTime = dateFormat.parse("2007-02-19");
        Date endTime = dateFormat.parse("2015-07-09");
        //startTime<=xx<=endTime
        List<JpaUser> users = this.jpaUserRepository.findByBirthdayBetween(startTime, endTime);
        System.err.println(users);
    }

    /**
     * 根据创建时间大于、小于区间 查询
     */
    @Test
    public void findByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date startTime = dateFormat.parse("2021-05-07 02:00:59");
        Date endTime =  dateFormat.parse("2021-10-06 12:30:58");

        //startTime<=xx<=endTime
        List<JpaUser> users = this.jpaUserRepository.findByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(startTime, endTime);
        System.err.println(users);
    }



    /**
     * 三、复杂查询
     *  1、Spring data jpa已经帮我们实现了分页的功能，在查询的方法中，需要传入参数Pageable，当查询中有多个参数的时候Pageable建议做为最后一个参数传入（习惯）
     *  2、限制查询(有时候我们只需要查询前N个元素，或者只取前一个实体等)
     *      User findFirstByOrderByNickNameAsc();  //根据nickname正序查询，返回第一个
     * 	    User findTopByOrderByAgeDesc();     //根据年龄倒序排，返回第一个
     * 	    Page<User> queryFirst10ByLastname(String lastname, Pageable pageable);  //根据lastname进行分页查询，返回前面10个
     * 	    List<User> findFirst10ByLastname(String lastname, Sort sort);   //根据lastname进行排序查询，返回前面10个
     * 	    List<User> findTop10ByLastname(String lastname, Pageable pageable); //根据lastname进行分页查询，返回前面10个
     *  3、自定义SQL查询
     *      Spring data 絕大部分的SQL都可以根据方法名定义的方式来实现，但是由于某些原因我们想使用自定义的SQL来查询，spring data jpa也是完美支持的；
     * 	    在SQL的查询方法上面使用@Query注解，如涉及到删除和修改在需要加上@Modifying.也可以根据需要添加 @Transactional 对事物的支持，查询超时的设置等
     */

    /**
     * 分页查询所有
     */
    @Test
    public void findAllWithPage() {
        Pageable pageable = PageRequest.of(1, 3,  Sort.by(Sort.Direction.DESC, new String[]{"age"}));    //以size为一页，查询第pageNum(0为第一页)页

        Page<JpaUser> page = this.jpaUserRepository.findAll(pageable);
        List<JpaUser> users = page.getContent();

        System.err.println(users);
        System.err.println("Number:" + page.getNumber());    //当前查询第几页
        System.err.println("NumberOfElements:" + page.getNumberOfElements());    //当前页码元素总数
        System.err.println("Size:" + page.getSize());    //条件设置中一页元素数量
        System.err.println("TotalElements" + page.getTotalElements());    //符合条件的元素总数量(不分页)
        System.err.println("TotalPages:" + page.getTotalPages());    //符合条件的总页数
        System.err.println("Order:" + page.getSort().getOrderFor("age"));
    }

    /**
     * 根据姓名进行分页条件查询
     */
    @Test
    public void findByUserNamePageable() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, new String[]{"age"}));
        Page<JpaUser> page = this.jpaUserRepository.findByUserName("zhaoyun", pageable);
        List<JpaUser> users = page.getContent();
        System.err.println(users);
    }

    /**
     * 限制查询:根据姓名正向排序取第一个
     */
    @Test
    public void findFirstByOrderByAgeAsc() {
        JpaUser users = this.jpaUserRepository.findFirstByOrderByAgeAsc();
        System.err.println(users);
    }

    /**
     * 限制查询:根据姓名分页查询，取前3个
     */
    @Test
    public void findTop3ByUserName() {
        Pageable pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, new String[]{"age"}));

        List<JpaUser> users = this.jpaUserRepository.findTop3ByUserName("zhaoyun",pageable);
        Page<JpaUser> usersFirstPage = this.jpaUserRepository.findFirst3ByUserName(pageable,"zhaoyun");
        System.out.println(users);
    }

    /**
     * 根据密码进行自定义查询
     */
    @Test
    public void findByPassWord() {
        List<JpaUser> users = this.jpaUserRepository.myFindByPassword("123456");
        System.err.println(users);
    }

    /**
     * 根据密码进行自定义查询
     */
    @Test
    public void findByUserNameWithOrder() {
        Sort sort = Sort.by(Sort.Order.desc("age"));
        List<JpaUser> users = this.jpaUserRepository.myFindByUserNameWithOrder("zhaoyun",sort);
        System.err.println(users);
    }

    /**
     * 根据用户名进行自定义分页查询
     */
    @Test
    public void pageByUserName() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, new String[]{"age"}));
        Page<JpaUser> userPage = this.jpaUserRepository.myPageByUserName(pageable,"zhaoyun");
        System.err.println(userPage);
    }

    /**
     * 根据ID自定义更新年龄
     */
    @Test
    @Rollback(false)// 默认在执行之后,回滚事务,这里设置不回滚
    public void updateAgeById() {
        this.jpaUserRepository.myUpdateAgeById(11, 99L);
    }


    /**
     * Native自定义查询
     */
    @Test
    public void myFindByUserName() {
        List<JpaUser> userList = this.jpaUserRepository.myFindByUserName("zhaoyun");
        System.err.println(userList);
    }

    @Test
    public void myFindByLikeUserName() {
        List<JpaUser> userList = this.jpaUserRepository.myFindByLikeUserName("%ao%");
        System.err.println(userList);
    }

    @Test
    public void myFindByUserNameAndgAge() {
        List<JpaUser> userList = this.jpaUserRepository.myFindByUserNameAndgAge("zhaoyun",20);
        System.err.println(userList);
    }

    @Test
    public void myFindByUserNameWithPage() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, new String[]{"age"}));
        Page<JpaUser> page = this.jpaUserRepository.myFindByUserNameWithPage("zhangsan",pageable);
        System.err.println(page);
    }

    /**
     * Native自定义多表查询（使用自定义接口接收结果集）
     */
    @Test
    public void myFindUserInfoByUserId() {
        JpaUserInfo info = this.jpaUserRepository.myFindUserInfoByUserId(108L);
        System.out.println(info);
    }


    /**
     * 单个插入用户(级联插入身份证信息【一对一】，级联插入地址信息【一对多】)
     */
    @Test
    public void saveUser() {
        JpaUser user = new JpaUser();
        user.setUserName("zhangfei");
        user.setPassword("234");
        user.setName("张飞");
        user.setAge(22);
        user.setSex(1);
        user.setPhone("18899990000");
        user.setBirthday(new Date());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        //一对一级联保存新增
        JpaIdentity identity = new JpaIdentity();
        identity.setUser(user);
        identity.setCardNo("430703199008299095");
        identity.setStartTime(new Date());
        identity.setEndTime(new Date());

        //一对多级联保存新增
        List<JpaAddress> addressList = new ArrayList<>();
        JpaAddress address = new JpaAddress();
        address.setUser(user);
        address.setProvince("湖北省");
        address.setCity("武汉市");
        address.setArea("汉口区");
        address.setCreateTime(new Date());
        address.setUpdateTime(new Date());
        addressList.add(address);

        address = new JpaAddress();
        address.setUser(user);
        address.setProvince("湖北省");
        address.setCity("武汉市");
        address.setArea("武昌区");
        address.setCreateTime(new Date());
        address.setUpdateTime(new Date());
        addressList.add(address);

        user.setIdentity(identity);
        user.setAddresses(addressList);
        this.jpaUserRepository.save(user);
    }

    /**
     * 修改用户(级联修改身份证信息【一对一】，级联修改地址信息【一对多】)
     *
     * 注意：先查后改，如果直接用new对象的方式进行修改，会把没有设置的属性置为NULL
     */
    @Test
    public void editUser() {
        JpaUser user = this.jpaUserRepository.findById(108L).get();
        user.setPhone("19900000000");

        //一对一级联修改
        JpaIdentity identity = user.getIdentity();
        identity.setCardNo("43070319900829999");

        //一对多级联保存新增
        user.getAddresses().get(0).setProvince("湖南省");
        user.getAddresses().get(1).setProvince("湖南省");
        this.jpaUserRepository.save(user);
    }

    /**
     * 通过identity级联查询
     */
    @Test
    public void queryIdentity() {
        JpaIdentity identity = this.jpaIdentityRepository.findById(7L).get();
        System.out.println(identity);
    }

    /**
     * 通过address级联查询
     */
    @Test
    public void queryAddress() {
        JpaAddress address = this.jpaAddressRepository.findById(49L).get();
        System.out.println(address);
    }

    /**
     * 保存或修改identity
     *
     * 当JpaIdentity设置为CascadeType.MERGE：且其中的JpaUser也进行了设置且存在，可对JpaIdentity进行新增(此时其中JpaUser即使改了也不会修改)，
     *          另外，也可进行JpaIdentity的修改(JpaUser级联修改)
     * 当JpaIdentity设置为CascadeType.PERSIST或ALL: 可进行 JpaUser 和JpaIdentity的级联新增
     */
    @Test
    public void saveOrUpdateIdentity() {
        //新增
        /*JpaUser user = new JpaUser();
        user.setId(99L);

        JpaIdentity identity = new JpaIdentity();
        identity.setUser(user);
        identity.setCardNo("430703198809981111");
        identity.setStartTime(new Date());
        identity.setEndTime(new Date());
        this.jpaIdentityRepository.save(identity);*/

        //修改
        JpaIdentity identity = this.jpaIdentityRepository.findById(13L).get();
        identity.setCardNo("999999");

        JpaUser user = identity.getUser();
        user.setUserName("guanyu1");
        user.setName("关羽1");

        JpaAddress address = user.getAddresses().get(0);
        address.setProvince("北京市");
        address.setCity("北京市");
        address.setArea("昌平区");
        this.jpaIdentityRepository.save(identity);
    }

    /**
     * 保存或修改address
     */
    @Test
    public void saveAllAddress() {
        //新增
        /*JpaUser user = new JpaUser();
        user.setId(99L);

        List<JpaAddress> addressList = new ArrayList<>()
        JpaAddress address;
        for (int i = 3; i <= 4; i++) {
            address = new JpaAddress("湖南省" + i, "常德市" + i, "鼎城区" + i);
            address.setUser(user);
            addressList.add(address);
        }
        this.jpaAddressRepository.saveAll(addressList);*/

        //修改
        JpaAddress address = this.jpaAddressRepository.findById(62L).get();
        address.setProvince("天津市");
        address.setCity("天津市");
        address.setArea("滨海新区");

        JpaUser user = address.getUser();
        user.setUserName("liubei");
        user.setName("刘备");

        JpaIdentity identity = user.getIdentity();
        identity.setCardNo("00000000");
        this.jpaAddressRepository.save(address);
    }
}
