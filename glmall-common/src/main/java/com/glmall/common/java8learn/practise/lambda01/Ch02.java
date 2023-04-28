package com.glmall.common.java8learn.practise.lambda01;

public class Ch02 {
    /*
    *
    * note 1. 请看例 2-15 中的 Function 函数接口并回答下列问题。
        例 2-15 Function 函数接口
        public interface Function<T, R> {
        R apply(T t);
        }
        a. 请画出该函数接口的图示。
            答：T ->Function -> R
        b. 若要编写一个计算器程序，你会使用该接口表示什么样的 Lambda 表达式？
            答：用来做一元运算，例如：求次幂，开方等
        c. 下列哪些 Lambda 表达式有效实现了 Function<Long,Long> ？
            x -> x + 1; 这个代码有效实现了Function<Long,Long>接口
            (x, y) -> x + 1;
            x -> x == 1;
    * */

    /*
    *
    * note 2. ThreadLocal Lambda 表达式。Java 有一个 ThreadLocal 类，作为容器保存了当前线程里
        局部变量的值。Java 8 为该类新加了一个工厂方法，接受一个 Lambda 表达式，并产生
        一个新的 ThreadLocal 对象，而不用使用继承，语法上更加简洁。
        a. 在 Javadoc 或集成开发环境（IDE）里找出该方法。
        *
        * public static <S> ThreadLocal<S> withInitial(Supplier<? extends S> supplier) {
                return new SuppliedThreadLocal<>(supplier);
            }
        *
        *
        b. DateFormatter 类是非线程安全的。使用构造函数创建一个线程安全的 DateFormatter
        对象，并输出日期，如“01-Jan-1970”
    *
    *
    *
    *
    * */

    /**
     *
     * note 3. 类型推断规则。下面是将 Lambda 表达式作为参数传递给函数的一些例子。
     *  javac 能正确推断出 Lambda 表达式中参数的类型吗？换句话说，程序能编译吗？
     *  a. Runnable helloWorld = () -> System.out.println("hello world");
     *  能
     *  b. 使用 Lambda 表达式实现 ActionListener 接口：
     *  JButton button = new JButton();
     *  button.addActionListener(event -> System.out.println(event.getActionCommand()));
     *  能
     *  c. 以如下方式重载 check 方法后，还能正确推断出 check(x -> x > 5) 的类型吗？
     *  interface IntPred {
     *      boolean test(Integer value);
     *  }
     *  boolean check(Predicate<Integer> predicate);
     *  boolean check(IntPred predicate);
     *  不能
     *  No - the lambda expression could be inferred as IntPred or Predicate<Integer>
     *      so the overload is ambiguous.
     *
     * */

    public static void main(String[] args) {
    }



}
