package com.glmall.common.java8learn.practise.lambda01.ch03;

import com.alibaba.fastjson.JSON;
import com.glmall.common.java8learn.practise.lambda01.datasupport.Album;
import com.glmall.common.java8learn.practise.lambda01.datasupport.Artist;
import com.glmall.common.java8learn.practise.lambda01.datasupport.SampleData;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * note 1. 常用流操作。实现如下函数：
 *  a. 编 写 一 个 求 和 函 数， 计 算 流 中 所 有 数 之 和。
 *  例 如，int addUp(Stream<Integer> numbers)；
 *  b. 编写一个函数，接受艺术家列表作为参数，返回一个字符串列表，其中包含艺术家的
 *  姓名和国籍；
 *  c. 编写一个函数，接受专辑列表作为参数，返回一个由最多包含 3 首歌曲的专辑组成的
 *  列表。
 */
public class Test01 {

    public static void main(String[] args) {
        Integer sum = addUp(Stream.of(3, 6, 9));
        System.out.println(sum);
        List<String> result = fetchDataFromArtist(SampleData.membersOfTheBeatles);
        System.out.println(result);
        List<Album> albums = fetchDataFromAlbum(SampleData.albums.collect(Collectors.toList()));
        System.out.println(JSON.toJSONString(albums));
    }

    /**
     * c. 编写一个函数，接受专辑列表作为参数，返回一个由最多包含 3 首歌曲的专辑组成的
     * 列表。
     * @param albums
     * @return
     */
    private static List<Album> fetchDataFromAlbum(List<Album> albums) {
        return albums.stream()
                .filter(album -> album.getTrackList().size()<=3L)
                .collect(Collectors.toList());
    }

    /**
     * b. 编写一个函数，接受艺术家列表作为参数，【返回一个字符串列表】，其中包含艺术家的
     * 姓名和国籍；
     */
    private static List<String> fetchDataFromArtist(List<Artist> artists) {
        Assert.notNull(artists,"艺术家列表不能为null");
        Assert.notEmpty(artists,"艺术家列表不能为空");
        return artists.stream()
                .map(item -> item.getName() + "," + item.getNationality()).collect(Collectors.toList());
    }

    /**
     * a. 编 写 一 个 求 和 函 数， 计 算 流 中 所 有 数 之 和。
     * 例 如，int addUp(Stream<Integer> numbers)；
     */
    private static Integer addUp(Stream<Integer> numbers) {
        return numbers.reduce(0, (x, y) -> x + y);
    }


}
