package com.adolesce.server.jdk8Speciality;

import com.adolesce.common.bo.MyUser;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Stream
 * 1、Java 8 API添加了一个新的抽象称为流Stream，可以让你以一种声明的方式处理数据。
 * 2、Stream 使用一种类似用 SQL 语句从数据库查询数据的直观方式来提供一种对 Java 集合运算和表达的高阶抽象。 比如filter, map, reduce, find, match, sorted等。
 * 3、Stream API可以极大提高Java程序员的生产力，让程序员写出高效率、干净、简洁的代码。
 * 4、这种风格将要处理的元素集合看作一种流， 流在管道中传输， 并且可以在管道的节点上进行处理， 比如筛选， 排序，聚合等。
 * 5、元素流在管道中经过中间操作（intermediate operation）的处理，最后由最终操作(terminaloperation)得到前面处理的结果。
 * 6、元素：是特定类型的对象，形成一个队列。Java中的Stream并不会存储元素，而是按需计算。
 * 7、Stream数据源 ：流的来源。可以是集合，数组，I/O channel，产生器generator等。
 *
 * 8、特点：
 *      1). 不是数据结构，不会保存数据。
 *      2). 不会修改原来的数据源，它会将操作后的数据保存到另外一个对象中。（保留意见：毕竟peek方法可以修改流中元素）
 *      3). 惰性求值，流在中间处理过程中，只是对操作进行了记录，并不会立即执行，需要等到执行终止操作的时候才会进行实际的计算。
 * 参考：https://www.runoob.com/java/java8-streams.html
 */
public class StreamAndOptionalTest {
    /**
     * stream常见创建方式
     */
    @Test
    public void createStyle() {
        //由Collection子类（List，Set）来创建流：
        List<String> list = Arrays.asList("aaa", "bbb", "ccc");
        Stream listStream = list.stream();
        Stream parallelSteam = list.parallelStream();

        //由数组来创建流：
        String[] strArr = new String[]{"abc", "bbc", "ca"};
        Stream attrStream = Arrays.stream(strArr);

        //还有许多重载形式的方法，可以返回带类型的Stream，例如：
        IntStream intStream = Arrays.stream(new int[]{1, 2, 3, 4, 5});
        double a = intStream.summaryStatistics().getAverage();
        System.out.println(a);

        //通过具体值来创建流：通过Stream的静态方法Stream.of(T...values)可以创建一个流，它可以接受任意个值
        Stream s = Stream.of("1", "2", 3, new String[]{"a", "b", "c"});

    }

    /**
     * stream 流常见操作
     */
    @Test
    public void testStreamOperation1() {
        List<String> strs = Arrays.asList("abc","","ab","bc","efg","ab","jkl","abcd","","jkl","eopdkl");
        System.out.println("列表: " + strs);
        System.out.println("--------------------------------");

        // filter(Predicate d) 接受一个断言型函数，对Stream流中的元素进行处理，过滤掉不满足条件的元素
        long count = strs.stream().filter(str -> str.isEmpty()).count();
        System.out.println("空字符串数量为: " + count);

        count = strs.stream().filter(str -> str.length() == 3).count();
        System.out.println("字符串长度为 3 的数量为: " + count);

        List<String> filteredStrs = strs.stream().filter(str -> !str.isEmpty()).collect(Collectors.toList());
        System.out.println("非空筛选后的列表: " + filteredStrs);

        String mergedStrs = strs.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining(", "));
        System.out.println("非空字符串逗号拼接: " + mergedStrs);

        //distinct 筛选元素，通过Stream元素中的hasCode和equals方法来去除重复元素
        List<String> distinctStrs = strs.stream().distinct().collect(Collectors.toList());
        System.out.println("去重后的列表: " + distinctStrs);

        // limit(long maxSize) 截断流，0
        List<String> limitStrs = strs.stream().limit(3).collect(Collectors.toList());
        System.out.println("截断后的列表: " + limitStrs);

        //skip(Long n) 跳过元素，返回一个扔掉了前n个元素的流，若流中的元素不足n个，则会返回一个空流
        List<String> skipStrs = strs.stream().skip(3).limit(3).collect(Collectors.toList());
        System.out.println("跳过后的列表: " + skipStrs);

        //map(Function f) 接受一个函数型接口作为参数，该函数会对流中的每个元素进行处理，返回处理后的流
        List<String> mapStrs = strs.stream().map(str ->str = str+" hello").collect(Collectors.toList());
        System.out.println("map聚合后的列表: " + mapStrs);

        //sorted 返回一个新流，流中的元素按照自然排序进行排序 sorted(Comparator comp) 返回一个新流，并且Comparator指定的排序方式进行排序
        List<String> sortedStrs = strs.stream().sorted().collect(Collectors.toList());
        System.out.println("自然排序后的列表：" + sortedStrs);
        sortedStrs = strs.stream().sorted((s1,s2) -> s1.compareTo(s2)).collect(Collectors.toList());
        System.out.println("比较器排序后的列表：" + sortedStrs);

        //peek 接受Consumer，改变值
        List<Integer> integerList = Arrays.asList(1,9,2,4,8,0);
        integerList.stream().peek(num -> num = num + 2);
        System.out.println("peek后的int列表：" + integerList);

        strs.stream().peek(str-> str = str.toUpperCase());
        System.out.println("peek后的string列表：" + strs);

        // 并行处理
        count = strs.parallelStream().filter(string -> string.isEmpty()).count();
        System.out.println("空字符串的数量为: " + count);
    }

    /**
     * 流常见操作
     */
    @Test
    public void testStreamOperation2() {
        List<MyUser> myUsers = this.getUsers();
        myUsers.forEach(System.out::println);
        System.out.println("--------------------------------");
        //filter(Predicate d) 接受一个断言型函数，对Stream流中的元素进行处理，过滤掉不满足条件的元素
        myUsers.stream().filter(user -> user.getAge() >= 6 && user.getAge() <= 15)
                .collect(Collectors.toList()).forEach(System.out::println);

        //distinct 筛选元素，通过Stream元素中的hasCode和equals方法来去除重复元素
        myUsers.stream().distinct().collect(Collectors.toList()).forEach(System.out::println);

        //limit(long maxSize) 截断流，0
        myUsers.stream().limit(3).collect(Collectors.toList()).forEach(System.out::println);

        //skip(Long n) 跳过元素，返回一个扔掉了前n个元素的流，若流中的元素不足n个，则会返回一个空流
        myUsers.stream().skip(3).collect(Collectors.toList()).forEach(System.out::println);

        //map(Function f) 接受一个函数型接口作为参数，该函数会对流中的每个元素进行处理，返回处理后的流
        myUsers.stream().map(u -> u.getUserName()).collect(Collectors.toList()).forEach(System.out::println);

        //sorted 返回一个新流，流中的元素按照自然排序进行排序 sorted(Comparator comp) 返回一个新流，并且Comparator指定的排序方式进行排序
        myUsers.stream().sorted().forEach(System.out::println);//排序（正序）

        myUsers.sort((u1,u2) -> u1.getAge().compareTo(u2.getAge()));
        myUsers.forEach(System.out::println);

        myUsers.sort(Comparator.comparing(MyUser::getAge));
        myUsers.forEach(System.out::println);

        myUsers.stream().sorted(Comparator.comparing(MyUser::getAge)).forEach(System.out::println);

        //peek 接受Consumer，改变值
        myUsers.stream().peek(user -> user.setAge(50)).forEach(System.err::println);
    }

    /**
     * allMatch(Predicate p) 传入一个断言型函数，对流中所有的元素进行判断，如果都满足返回true，否则返回false
     * anyMatch(Predicate p) 传入一个断言型函数，对流中所有的元素进行判断，只要有一个满足条件就返回true，都不满足返回false
     * noneMatch(Predicate p) 所有条件都不满足，返回true，否则返回false。
     * findFirst() 返回流中的第一个元素
     */
    @Test
    public void testMatch() {
        List<MyUser> users = this.getUsers();
        users.forEach(System.out::println);
        System.out.println("--------------------------------");
        boolean allMatdhFlag = users.stream().allMatch(user -> user.getAge() > 10);
        boolean anyMatch = users.stream().anyMatch(user -> user.getAge() > 10);
        boolean noneMatch = users.stream().noneMatch(user -> user.getAge() > 10);

        System.out.println("allMatdhFlag: " + allMatdhFlag);
        System.out.println("anyMatch: " + anyMatch);
        System.out.println("noneMatch: " + noneMatch);
    }

    @Test
    public void testCollect() {
        List<MyUser> users = this.getUsers();
        users.forEach(System.out::println);
        System.out.println("--------------------------------");
        //转成List
        List list = users.stream().collect(Collectors.toList());

        //转成Set
        Set set = users.stream().collect(Collectors.toSet());

        //转成ArrayList
        List arrayList = users.stream().collect(Collectors.toCollection(ArrayList::new));

        //转换成Map（第三个参数表示当key出现重复时保留哪一个）
        Map<String, MyUser> userMap = users.stream().limit(5).collect(Collectors.toMap(MyUser::getUserName, Function.identity(),
                (name1, name2)->name1));
        System.out.println(userMap);

        //结果个数
        long count = users.stream().collect(Collectors.counting());

        //求和
        int sum = users.stream().collect(Collectors.summingInt(MyUser::getAge));
        //users.stream().collect(Collectors.summingDouble(Users::getAge));
        //users.stream().collect(Collectors.summarizingLong(Users::getAge));

        //求平均数
        double averag = users.stream().collect(Collectors.averagingInt(MyUser::getAge));
        //users.stream().collect(Collectors.averagingDouble(Users::getAge));
        //users.stream().collect(Collectors.averagingLong(Users::getAge));

        //以运算对象求运算结果集
        IntSummaryStatistics intStats = users.stream().collect(Collectors.summarizingInt(MyUser::getAge));
        //DoubleSummaryStatistics doubleStats = users.stream().collect(Collectors.summarizingDouble(Users::getAge));
        //LongSummaryStatistics longStats = users.stream().collect(Collectors.summarizingLong(Users::getAge));
        intStats.getAverage();
        intStats.getCount();
        intStats.getMax();
        intStats.getMin();
        intStats.getSum();

        //按给定值进行分组（按姓名分组）
        Map<String, List<MyUser>> kv = users.stream().collect(Collectors.groupingBy(MyUser::getUserName));

        //按是否符合条件（true|false）进行分组
        Map<Boolean, List<MyUser>> kvv = users.stream().collect(Collectors.partitioningBy(user -> user.getAge() > 10));

        //users.stream().limit(5).forEach(System.out::println);
    }

    public void summaryStatisticsTest(){
        List<Integer> integers = Arrays.asList(1, 2, 13, 4, 15, 6, 17, 8, 19);
        System.out.println("列表: " + integers);
        IntSummaryStatistics stats = integers.stream().mapToInt(i->i).summaryStatistics();

        //sorted 方法用于对流进行排序（默认正序排序）
        List<Integer> paixus = integers.stream().sorted((s1, s2) -> s2.compareTo(s1)).limit(7).collect(Collectors.toList());

        System.out.println("排序:" + paixus);
        System.out.println("列表中最大的数 : " + stats.getMax());
        System.out.println("列表中最小的数 : " + stats.getMin());
        System.out.println("所有数之和 : " + stats.getSum());
        System.out.println("平均数 : " + stats.getAverage());
        System.out.println("count :" + stats.getCount());
        System.out.println("随机数: ");

        Random random = new Random();
        //Stream 提供了新的方法 'forEach' 来迭代流中的每个数据
        random.ints().limit(10).sorted().forEach(System.out::println);
    }


    /**
     * 常见使用场景
     */
    @Test
    public void testUse(){
        List<MyUser> myUsers = this.getUsers();
        myUsers.forEach(System.out::println);
        System.out.println("--------------------------------");
        //拼接sql字符串
        String userNameStr = myUsers.stream().filter(user -> user.getAge()>=6 && user.getAge()<=15)
                .map(MyUser::getUserName)
                .collect(Collectors.joining("','","('","')"));
        System.out.println(userNameStr);

        //map进行设置返回新的集合
        myUsers.stream().map(u -> {
            u.setAge(123);
            return u;
        }).forEach(System.err::println);

        //获得对象某属性平均值
        double b = myUsers.stream().mapToInt(user -> user.getAge()).summaryStatistics().getAverage();
        double a = myUsers.stream().collect(Collectors.averagingDouble(user -> user.getAge()));
    }







    @Test
    public void testOptional1() {
        List<MyUser> users = this.getUsers();
        users.forEach(System.out::println);
        System.out.println("--------------------------------");

        // count() 返回流中元素的个数
        long count = users.stream().count();

        Optional<MyUser> first = users.stream().sorted(Comparator.comparing(MyUser::getAge)).findFirst();
        Optional<MyUser> min = users.stream().min((u2, u1) -> u1.getAge().compareTo(u2.getAge()));
        Optional<MyUser> max = users.stream().max((u2, u1) -> u1.getAge().compareTo(u2.getAge()));

        Optional min2 = users.stream().collect(Collectors.minBy(Comparator.comparing(MyUser::getAge)));
        Optional max2 = users.stream().collect(Collectors.maxBy(Comparator.comparing(MyUser::getAge)));


        System.out.println("count: " + count);
        System.out.println("first: " + first.get());
        System.out.println("min: " + min.get());
        System.out.println("min2: " + min2.get());
        System.out.println("max: " + max.get());
        System.out.println("max2: " + max2.get());

        MyUser user = null;
        Optional<MyUser> optional1 = Optional.of(user);
        Optional<MyUser> optional2 = Optional.ofNullable(user);
    }


    @Test
    public void testOptional2() {
        //允许为空值，当值为空时也不会报错
        Optional<String> name = Optional.ofNullable(null);
        System.out.println("name是否有值 " + name.isPresent());
        System.out.println("如果名称为空: " + name.orElseGet(() -> "代替名称"));
        System.out.println(name.map(s -> "名称为： " + s).orElse("名称为空"));
        System.out.println("---------------------------------");
        //不允许为空值，当值为空时会报错
        Optional<String> firstName = Optional.of("lwd");
        System.out.println("firstName是否有值 " + firstName.isPresent());
        System.out.println("如果名称为空: " + firstName.orElseGet(() -> "代替名称"));
        System.out.println(firstName.map(s -> "名称为： " + s).orElse("名称为空"));
    }

    private List<MyUser> getUsers() {
        List<MyUser> userList = new ArrayList<>();
        for (int i = 6; i <= 10; i++) {
            getUsers(userList, i);
        }
        for (int i = 6; i <= 10; i++) {
            getUsers(userList, i);
        }
        for (int i = 1; i <= 5; i++) {
            getUsers(userList, i);
        }
        for (int i = 11; i <= 15; i++) {
            getUsers(userList, i);
        }
        return userList;
    }

    private void getUsers(List<MyUser> userList, int i) {
        MyUser users = new MyUser();
        users.setUserId(ObjectId.get());
        users.setUserName("lwd" + i);
        users.setAge(i);
        users.setSex(i%2+1);
        userList.add(users);
    }



    /*List<Employee> emps = Arrays.asList(
            new Employee("张三", 18, 6666.66),
            new Employee("张三", 15, 6666.66),
            new Employee("张三", 40, 6666.66),
            new Employee("李四", 20, 7777.77),
            new Employee("王五", 36, 8888.88),
            new Employee("田七", 55, 11111.11),
            new Employee("赵六", 55, 9999.99),
            new Employee("赵六", 45, 12222.22),
            new Employee("赵六", 30, 9999.99)
    );

    *//**
     * 筛选与切片 filter, distinct limit skip
     *//*
    @Test
    public void test3() {
        //1.过滤掉年龄小于25的员工
        List<Employee> list = emps.stream().filter((e) -> e.getAge() > 25).collect(Collectors.toList());
        System.out.println(list);
        //2.过滤掉姓名重复的员工
        //emps.stream().distinct().forEach(System.out::println);
        //3.获取前三名员工
        //emps.stream().limit(3).forEach(System.out::println);
        //4.获取第三名以后的员工
        //emps.stream().skip(3).forEach(System.out::println);
        //5.先获取前3名员工，再获取其中年龄大于25的员工。（中间操作可以任意次）
        //emps.stream().limit(3).filter(e -> e.getAge() > 25);

        *//*List<String> list = new ArrayList<>();
        emps.stream().peek(e -> {
            if (e.getAge() > 38) {
                list.add(e.getName());
                e.setAge(88);
            }
        }).forEach(System.out::println);
        System.out.println(list); //空*//*
    }

    *//**
     * 映射操作 map, mapToDouble, mapToInt, mapToLong, flatMap
     *//*
    @Test
    public void test4() {
        //1. 获取所有员工的姓名
        emps.stream().map(e -> e.getName()).forEach(System.out::println);
        //2. 获取所有员工的工资，这里工资是Double类型，我们可以用mapToDouble方法
        emps.stream().mapToDouble(e -> e.getSalary()).forEach(System.out::println);
        //3. 获取所有员工的年龄，用mapToInt方法
        emps.stream().mapToInt(e -> e.getAge()).forEach(System.out::println);
        //4. 将员工年龄转换成Long型并输出,PS：这里就算不用longValue系统也会自动将getAge转换成Long类型
        emps.stream().mapToLong(e -> e.getAge().longValue()).forEach(System.out::println);
        *//**
         * 5.将所有员工的 姓名，年龄，工资转换成一个流并返回
         * 首先我们用map方法来处理，这种方式返回的是六个Stream对象，数据结构可以类似于：
         * [{"张三",18, 6666.66},{"李四",20, 7777.77}, {"王五", 36, 8888.88}, {"赵六", 24, 9999.99}, {"田七", 55, 11111.11}, {"赵六", 45, 12222.22}]
         * 然后我们用flatMap方法来处理，返回的是一个Stream对象，将所有元素连接到了一起，数据结构类似于：
         * ["张三",18, 6666.66,"李四",20, 7777.77, "王五", 36, 8888.88, "赵六", 24, 9999.99, "田七", 55, 11111.11, "赵六", 45, 12222.22]
         *//*
        emps.stream().map(e -> {
            return Stream.of(e.getName(), e.getAge(), e.getSalary());
        }).forEach(System.out::println);
        emps.stream().flatMap(e -> {
            return Stream.of(e.getName(), e.getAge(), e.getSalary());
        }).forEach(System.out::println);


        //1. 获取所有员工的姓名
        emps.stream().map(e -> e.getName()).forEach(System.out::println);
        //2. 获取所有员工的工资，这里工资是Double类型，我们可以用mapToDouble方法
        emps.stream().mapToDouble(e -> e.getSalary()).summaryStatistics();
        //3. 获取所有员工的年龄，用mapToInt方法
        //emps.stream().
        //4. 将员工年龄转换成Long型并输出,PS：这里就算不用longValue系统也会自动将getAge转换成Long类型
    }

    *//**
     * 排序操作 sorted
     *//*
    @Test
    public void test5() {
        //1.按照自然排序，注意，需要进行自然排序则对象必须实现Comparable接口
        emps.stream().sorted().forEach(System.out::println);
        //2.按照给定规则进行排序,(按照工资高低进行排序)
        emps.stream().sorted((x, y) -> Double.compare(x.getSalary(), y.getSalary())).forEach(System.out::println);
    }


    *//**
     * Stream的终止操作练习
     *//*


    *//**
     * 终止操作：查找与匹配 allMatch, anyMatch, noneMatch, findFirst, findAny, count, max, min ,forEach
     *//*
    @Test
    public void test6() {
        //1.查看是否有员工年龄是否都大于18
        boolean flag1 = emps.stream().allMatch(e -> e.getAge() > 18);
        System.out.println(flag1);   // false
        //2.先去掉张三，然后再判断所有员工年龄是否都大于18
        boolean flag2 = emps.stream().filter(e -> !"张三".equals(e.getName())).allMatch(e -> e.getAge() > 18);
        System.out.println(flag2);  //true
        //3.是否有员工年龄大于50
        boolean flag3 = emps.stream().filter(e -> !"张三".equals(e.getName())).anyMatch(e -> e.getAge() > 50);
        System.out.println(flag3);  //true
        //4.没有员工的年龄大于50？
        boolean flag4 = emps.stream().filter(e -> !"张三".equals(e.getName())).noneMatch(e -> e.getAge() > 50);
        System.out.println(flag4);  //false
        //5.先按照年龄进行排序，然后返回第一个员工。optional是java8用来包装可能出现空指针的对象的对象
        Optional<Employee> op1 = emps.stream().sorted((x, y) -> Integer.compare(x.getAge(), y.getAge())).findFirst();
        System.out.println(op1.get());  //Employee{name='张三', age=17, salary=6666.66}
        //6. 查找任意一名员工的姓名，当使用顺序流时，返回的是第一个对象，当使用并行流时，会随机返回一名员工的姓名
        Optional<String> op2 = emps.parallelStream().map(e -> e.getName()).findAny();
        System.out.println(op2.get()); //会随机获取一名员工
        //7. 查询员工人数
        Long count = emps.stream().count();
        System.out.println(count);
        //8.查询员工工资最大的员工信息。PS: 这个也可以通过先按照工资排序，然后取第一个元素来实现
        Optional<Employee> maxSalary = emps.stream().max((x, y) -> Double.compare(x.getSalary(), y.getSalary()));
        System.out.println(maxSalary.get());
        //9.查询员工最小年龄
        Optional<Employee> minAge = emps.stream().max((x, y) -> -Integer.compare(x.getAge(), y.getAge()));
        System.out.println(minAge.get());
        //10.循环输出所有员工的信息
        emps.stream().forEach(System.out::println);

        //1.查看是否有员工年龄是否都大于18
        //2.先去掉张三，然后再判断所有员工年龄是否都大于18
        //3.是否有员工年龄大于50
        //4.没有员工的年龄大于50？
        //5.先按照年龄进行排序，然后返回第一个员工。optional是java8用来包装可能出现空指针的对象的对象
        //6. 查找任意一名员工的姓名，当使用顺序流时，返回的是第一个对象，当使用并行流时，会随机返回一名员工的姓名
        //7. 查询员工人数
        //8.查询员工工资最大的员工信息。PS: 这个也可以通过先按照工资排序，然后取第一个元素来实现
        //9.查询员工最小年龄
        //10.循环输出所有员工的信息
    }

    *//**
     * 终止操作：规约 reduce
     * 规约操作两个重载方法的区别是：
     * 一个未指定起始值，有可能返回null,所以返回的是一个Optional对象
     * 一个指定了起始值，不可能返回null,所以返回值可以确定是一个Employee
     *//*
    @Test
    public void test7() {
        //1.将所有员工的名字加上 -> 下一个员工的名字： 如 张三 -> 李四
        Optional<Employee> op1 = emps.stream().reduce((x, y) -> {
            x.setName(x.getName() + "->" + y.getName());
            return x;
        });
        System.out.println(op1.get().getName());  //张三->李四->王五->田七->赵六->赵六
        //2.将所有员工的名字加上 -> 下一个员工的名字，并且开始以王八开始;
        // PS:测试时，请将例子1注释掉，不然会影响emps对象
        Employee emp = emps.stream()
                .reduce(new Employee("王八", 65, 8888.88)
                        , (x, y) -> {
                            x.setName(x.getName() + "->" + y.getName());
                            return x;
                        });
        System.out.println(emp.getName());  //王八->张三->李四->王五->田七->赵六->赵六
    }

    *//**
     * 终止操作：收集
     *//*
    @Test
    public void test8() {
        //1.按年龄排序后收集成一个list并返回
        List<Employee> list = emps.stream().sorted((x, y) -> Integer.compare(x.getAge(), y.getAge()))
                .collect(Collectors.toList());
        list.forEach(System.out::println);
        //2.按工资高低排序后收集成一个Set返回
        Set<Employee> set = emps.stream().sorted((x, y) -> Integer.compare(x.getAge(), y.getAge()))
                .collect(Collectors.toSet());
        set.forEach(System.out::println);
        //3.按工资排序后收集到指定的集合中，这里指定LinkedList
        LinkedList<Employee> linkedList = emps.stream().sorted((x, y) -> Integer.compare(x.getAge(), y.getAge()))
                .collect(Collectors.toCollection(LinkedList::new));
        linkedList.forEach(System.out::println);
        //4.计算流中元素的个数：
        long count = emps.stream().collect(Collectors.counting());
        System.out.println(count);
        //5.对所有员工的年龄求和：
        int inttotal = emps.stream().collect(Collectors.summingInt(Employee::getAge));
        System.out.println(inttotal);
        //6.计算所有员工工资的平均值：
        Double doubleavg = emps.stream().collect(Collectors.averagingDouble(Employee::getSalary));
        System.out.println(doubleavg);
        //7.返回一个IntSummaryStatistics，可以通过这个对象获取统计值，如平均值：
        IntSummaryStatistics iss = emps.stream().collect(Collectors.summarizingInt(Employee::getAge));
        System.out.println(iss.getAverage());
        System.out.println(iss.getMax());
        System.out.println(iss.getMin());
        //8.连接所有员工的名字：
        String str = emps.stream().map(Employee::getName).collect(Collectors.joining());
        System.out.println(str);
        //9.相当于先按照工资进行排序，再取出排在第一位的员工
        Optional<Employee> min = emps.stream().collect(Collectors.minBy((x, y) -> Double.compare(x.getSalary(), y.getSalary())));
        System.out.println(min);
        //10.相当于先按照工资进行排序，再取出排在最后一位的员工
        Optional<Employee> max = emps.stream().collect(Collectors.maxBy((x, y) -> Double.compare(x.getSalary(), y.getSalary())));
        System.out.println(max);
        //11.从一个作为累加器的初始值开始，利用BinaryOperator与流中元素逐个结合，从而归约成单个值。
        Integer totalAge = emps.stream().collect(Collectors.reducing(0, Employee::getAge, Integer::sum));
        System.out.println(totalAge);
        //12.包裹一个收集器，对其结果进行转换：
        int inthow = emps.stream().collect(Collectors.collectingAndThen(Collectors.toList(), List::size));
        System.out.println(inthow);
        //13.根据某属性对结果进行分组，属性为K，结果为V：
        Map<String, List<Employee>> kv = emps.stream().collect(Collectors.groupingBy(Employee::getName));
        System.out.println(emps);
        //14.根据true或false进行分区，年龄大于30的分在true区，小于30的分在false区
        Map<Boolean, List<Employee>> vd = emps.stream().collect(Collectors.partitioningBy(e -> e.getAge() > 30));
        System.out.println(vd);
    }


    *//**
     * 按某个字段分组后再按某个字段排序，每个组内取最大的放入最终集合
     *//*
    @Test
    public void test9() {
        emps.forEach(System.out::println);
        System.out.println("--------------------------------");
        List<Employee> result = new ArrayList<>();
        Map<String, List<Employee>> groupMap = emps.stream().collect(Collectors.groupingBy(Employee::getName));
        List<Employee> etemp;
        for (Map.Entry<String, List<Employee>> entry : groupMap.entrySet()) {
            etemp = entry.getValue();
            //Optional<Employee> temp = etemp.stream().sorted(Comparator.comparing(Employee::getAge)).findFirst();
            Optional<Employee> temp = etemp.stream().sorted((e1, e2) -> e2.getAge().compareTo(e1.getAge())).findFirst();
            result.add(temp.get());
        }
        result.stream().forEach(System.err::println);
    }

    //将list转换为Map
    @Test
    public void test10() {
        List<Employee> employees = new ArrayList<>();
        LinkedList list;
        //style 1 : 用google工具包
        *//*Map<Integer,Employee> employeeMap = Maps.uniqueIndex(employees, new Function<Employee, Integer>() {
            @Override
            public Integer apply(@Nullable Employee from) {
                return from.getAge();
            }
        });*//*

        //style 2 使用JDK8新特性
        Map<Integer, Employee> employeeMap = employees.stream().collect(Collectors.toMap(e -> e.getAge(), Function.identity()));
    }*/
}