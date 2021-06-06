package com.adolesce.server.jdk8Speciality;

import cn.hutool.core.lang.copier.Copier;
import com.adolesce.common.bo.MyUser;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.util.*;
import java.util.function.*;

/**
 * @author Lwd
 *
 * JDK1.8新特性
 *
 *
 * 一、函数式接口
 *      1、有且仅有一个抽象方法，但是可以有多个非抽象方法的接口。可以被隐式转换为 lambda 表达式。
 *      2、JDK 1.8 之前已有的函数式接口:
 *          1)、java.lang.Runnable
 *          2)、java.util.concurrent.Callable
 *          3)、java.security.PrivilegedAction
 *          4)、java.util.Comparator
 *          5)、java.io.FileFilter
 *          6)、java.nio.file.PathMatcher
 *          7)、java.lang.reflect.InvocationHandler
 *          8)、java.beans.PropertyChangeListener
 *          9)、java.awt.event.ActionListener
 *          10)、javax.swing.event.ChangeListener
 * 3、JDK 1.8 新增加的函数接口：
 *      java.util.function, 它包含了很多类，用来支持 Java的 函数式编程  详见(http://www.runoob.com/java/java8-functional-interfaces.html)
 *
 *
 * 二、Lambda 表达式
 *      1、Lambda 表达式，也可称为闭包，它是推动 Java 8 发布的最重要新特性。
 *      2、Lambda 允许把函数作为一个方法的参数（函数作为参数传递进方法中）。
 *      3、使用 Lambda 表达式可以使代码变的更加简洁紧凑。
 *      4、语法:
 *          lambda 表达式的语法格式如下：
 *              (parameters) -> expression 或 (parameters) ->{ statements; }
 *      5、重要特征
 *          1）、可选类型声明：不需要声明参数类型，编译器可以统一识别参数值。
 *          2）、可选的参数圆括号：一个参数无需定义圆括号，但多个参数需要定义圆括号。
 *          3）、可选的大括号：如果主体只包含了一个语句，就不需要使用大括号。
 *          4）、可选的返回关键字：如果主体只有一个表达式返回值则编译器会自动返回值，大括号需要指定明表达式返回了一个数值。
 *
 *
 * 三、方法引用
 *      1、方法引用通过方法的名字来指向一个方法。
 *      2、方法引用可以使语言的构造更紧凑简洁，减少冗余代码。
 *      3、方法引用使用一对冒号 :: 。
 *          1)、构造器引用：它的语法是Class::new
 *          2)、静态方法引用：它的语法是Class::static_method
 *          3)、特定类的任意对象的方法引用：它的语法是Class::method
 *          4)、特定对象实例的方法引用：它的语法是instance::method
 *
 *
 * 四、默认方法
 *      简单说，默认方法就是接口可以有实现方法，而且不需要实现类去实现其方法。
 *      我们只需在方法名前面加个default关键字即可实现默认方法。
 */

@NoArgsConstructor
public class LambdaAndFunctionInterfaceTest {
    //定义函数式接口，1.8新注解,主要用于编译级错误检查，加上该注解，当你写的接口不符合函数式接口定义的时候，编译器会报错。
    @FunctionalInterface
    interface MathOperation {
        //只允许有一个抽象方法
        int operation(int a, int b);

        //函数式接口里是可以包含默认方法，因为默认方法不是抽象方法，其有一个默认实现，所以是符合函数式接口的定义的；
        default int addition(int a, int b) {
            return a + b;
        }

        //函数式接口里是可以包含静态方法，因为静态方法不能是抽象方法，是一个已经实现了的方法，所以是符合函数式接口的定义的；
        static void printHello() {
            System.out.println("Hello");
        }

        //函数式接口里是可以包含Object里的public方法，这些方法对于函数式接口来说，不被当成是抽象方法（虽然它们是抽象方法）；
        //因为任何一个函数式接口的实现，默认都继承了 Object 类，包含了来自 java.lang.Object 里对这些抽象方法的实现；
        @Override
        boolean equals(Object obj);
    }

    @FunctionalInterface
    interface MyConsumer {
        void accept(String message);
    }

    /**
     * @param a 参数a
     * @param b 参数b
     * @param mathOperation 数学操作函数式接口
     * @return
     */
    private static int operator(int a, int b, MathOperation mathOperation) {
        return mathOperation.operation(a, b);
    }

    public int thisAdd(int a, int b) {
        return a + b;
    }

    public static void print(String s) {
        System.err.println(s);
    }

    public void printHello(String s) {
        System.out.println("Hello " + s);
    }

    /**
     * 测试自定义函数式接口、Lambda表达式
     */
    @Test
    public void testFunctionInterface() {
        //带有类型声明的表达式
        //这个表达式的目标类型必须是一个函数接口,只有一个方法的接口叫函数接口,这样的接口可以隐式地转换成lambda表达式
        MathOperation add = (int a, int b) -> a + b;

        //没有类型声明的表达式
        MathOperation sub = (a, b) -> a - b;

        //带有大括号，带有返回语句的表达式
        MathOperation mul = (int a, int b) -> {
            return a * b;
        };

        //没有大括号和return语句的表达式
        MathOperation div = (int a, int b) -> a / b;

        //没有类型声明的表达式
        //也可以写成： (info) -> System.out.println(info); 或(String info) ->System.out.println(info);
        MyConsumer myConsumer = info -> System.out.println(info);
        //函数式接口/Lamdba表达式作为方法参数传递
        myConsumer.accept("10 + 5 =" + operator(10, 5, add));
        myConsumer.accept("10 - 5 =" + operator(10, 5, sub));
        myConsumer.accept("10 * 5 =" + operator(10, 5, mul));
        myConsumer.accept("10 / 5 =" + operator(10, 5, div));
    }

    /**
     * 测试方法引用
     */
    @Test
    public void testMethodReference(){
        //构造器引用
        Copier<LambdaAndFunctionInterfaceTest> aNew = LambdaAndFunctionInterfaceTest::new;
        LambdaAndFunctionInterfaceTest test = aNew.copy();

        //静态方法引用
        Consumer<String> consumer = LambdaAndFunctionInterfaceTest::print;
        consumer.accept("Hello Word");

        //特定类的任意对象的方法引用
        BiConsumer<LambdaAndFunctionInterfaceTest, String> printHello = LambdaAndFunctionInterfaceTest::printHello;
        printHello.accept(test,"reboot");

        //特定实例对象的方法引用
        MathOperation thisAdd = test::thisAdd;
        System.out.println(thisAdd.operation(2, 3));
    }

    /**
     * 测试打印
     *
     */
    @Test
    public void testPrint() {
        List<String> names = new ArrayList<String>();
        names.add("aaa");
        names.add("bbb");
        names.add("ccc");

        Consumer<String> comsumer1 = s -> System.out.println(s);
        Consumer<String> comsumer2 = System.out::println;
        Consumer<String> comsumer3 = LambdaAndFunctionInterfaceTest::print;

        names.forEach(comsumer1);
        names.forEach(this::printHello);
        names.stream().forEach(this::printHello);
    }

    /**
     * 测试排序
     */
    @Test
    public void testSort(){
        List<Integer> numbers = new ArrayList<Integer>();
        numbers.add(56);
        numbers.add(24);
        numbers.add(89);

        //JDK7 排序写法
	    /*Collections.sort(names, new Comparator<String>() {
	         @Override
	         public int compare(String s1, String s2) {
	            return s1.compareTo(s2);
	         }
	    });*/

        //JDK8 排序写法
        Collections.sort(numbers, (s1, s2) -> s1.compareTo(s2));

        numbers.forEach(System.out::println);

        Integer[] numberAttr = {1, 5, 2,100,1,4,66};
        Arrays.sort(numberAttr, Integer::compareTo);
        System.out.println(Arrays.asList(numberAttr));
    }

    /**
     * lambda 表达式只能引用标记了 final 的外层局部变量，也就是说不能在 lambda 内部修改定义在域外的局部变量，否则会编译错误。
     */
    @Test
    public void testLocalVariable() {
        int num = 1;    //可以不用声明为 final，但是必须不可被后面的代码修改（即隐性的具有 final 的语义）
        MyConsumer myConsumer = s -> System.out.println(num + s);
        myConsumer.accept("23");
    }

    /**
     * JDK 1.8 新增加的函数接口：java.util.function 测试
     * http://www.runoob.com/java/java8-functional-interfaces.html
     */
    @Test
    public void testFunctionInterface2() {
        //BiConsumer<T,U>代表了一个接受两个输入参数的操作，并且不返回任何结果
        BiConsumer<Integer, String> bigConsumer = (a, b) -> System.out.println(a + b);
        bigConsumer.accept(12, "34");

        //BiFunction<T,U,R>代表了一个接受两个输入参数的方法，并且返回一个结果
        BiFunction<Integer, String, String> biFunction = (a, b) -> a + b;
        System.out.println(biFunction.apply(12, "34"));

        //BinaryOperator<T>代表了一个作用于于两个同类型操作符的操作，并且返回了操作符同类型的结果
        BinaryOperator<String> binaryOperator = (a, b) -> a + b;
        System.out.println(binaryOperator.apply("12", "34"));

        //BiPredicate<T,U>代表了一个两个参数的boolean值方法
        BiPredicate<Integer, Integer> biPredicate = (a, b) -> a > b;
        System.out.println(biPredicate.test(1, 2));

        //Consumer<T>代表了接受一个输入参数并且无返回的操作
        Consumer<String> consumer = System.out::println;

        //Function<T,R>接受一个输入参数，返回一个结果。
        Function<String, String> function = a -> a.toString();

        //Predicate<T> 接受一个输入参数，返回一个布尔值结果。
        Predicate<String> predicate = a -> a.equals("aaa");

        //Supplier<T> 无参数，返回一个结果。
        Supplier<String> supplier = () -> "aa";

        //UnaryOperator<T> 接受一个参数为类型T,返回值类型也为T。
        UnaryOperator<MyUser> unaryOperator = a -> a;
    }
}
