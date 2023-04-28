package com.glmall.common.java8learn.practise.lambda01.ch03;

import com.glmall.common.java8learn.practise.lambda01.datasupport.Album;
import com.glmall.common.java8learn.practise.lambda01.datasupport.SampleData;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 5. 纯函数。下面的 Lambda 表达式有无副作用，或者说它们是否更改了程序状态？
 * x -> x + 1
 * 示例代码如下所示：
 * AtomicInteger count = new AtomicInteger(0);
 * List<String> origins = album.musicians().forEach(musician -> count.incAndGet();)
 * a. 上述示例代码中传入 forEach 方法的 Lambda 表达式。
 *
 * a. Side Effect Free
 * b. Mutates count
 */
public class Test05 {
    public static void main(String[] args) {
        Album album = SampleData.albums.findAny().get();
        AtomicInteger count = new AtomicInteger(0);
        album.getMusicians().forEach(musician -> count.incrementAndGet());
    }
}