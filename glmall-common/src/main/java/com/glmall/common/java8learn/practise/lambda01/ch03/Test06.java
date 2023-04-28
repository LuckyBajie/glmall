package com.glmall.common.java8learn.practise.lambda01.ch03;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 6. 计算一个字符串中小写字母的个数（提示：参阅 String 对象的 chars 方法）
 */
public class Test06 {
    public static void main(String[] args) {
        char a = 'a';
        char z = 'z';
        System.out.println(a==97);
        System.out.println(z==122);
        String str = "sadasBJVJhbjhbJBJJbjbjbGFYU";

        char[] chars1 = str.toCharArray();
        Long sum = Arrays.asList(chars1).stream()
                .map(chars -> {
                    System.out.println("");
                    return  (97 <= chars[0] && chars[0] <= 122) ? 1 : 0;
                })
                .collect(Collectors.summingLong(x -> x));
        System.out.println(sum);
    }
}

