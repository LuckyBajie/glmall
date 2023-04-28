package com.glmall.common.java8learn.practise.lambda01.ch03;

import com.glmall.common.java8learn.practise.lambda01.datasupport.Artist;
import com.glmall.common.java8learn.practise.lambda01.datasupport.SampleData;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Test02 {
    public static void main(String[] args) {
        example01();
    }

    /**
     * note 2. 迭代。修改如下代码，将外部迭代转换成内部迭代：
     *  int totalMembers = 0;
     *  for (Artist artist : artists) {
     *  Stream<Artist> members = artist.getMembers();
     *  totalMembers += members.count();
     *  }
     */
    private static void example01() {
        int totalMembers = 0;
        List<Artist> artists = SampleData.getThreeArtists();
        for (Artist artist : artists) {
            Stream<Artist> members = artist.getMembers();
            totalMembers += members.count();
        }
        System.out.println(totalMembers);

        // -----------------------------------------------
        long sum = artists.stream().map(artist -> artist.getMembers().count()).reduce(0L, (x, y) -> x + y);
        long sum2 = artists.stream().map(artist -> artist.getMembers().count()).collect(Collectors.summingLong(x->x));
        long sum3 = artists.stream().map(artist -> artist.getMembers()).collect(Collectors.summingLong(x->x.count()));
        System.out.println(sum);
        System.out.println(sum2);
        System.out.println(sum3);
    }


}
