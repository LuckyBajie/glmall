###1、streamAPI
![img.png](img.png)

####创建流

![img_2.png](img_2.png)

####中间操作

![img_4.png](img_4.png)


![img_5.png](img_5.png)

![img_6.png](img_6.png)

1）中间操作，map：
map接受一个lambda表达式函数作为该方法的参数，将元素转换成
其他形式或者提取信息。  
集合中的每个元素都作为map的lambda表达式函数的参数，被这个函数
作用，映射成新的元素。

![img_7.png](img_7.png)

2）中间操作，flatmap：
接受一个返回值为stream对象的lambda表达式函数作为参数，
将流中的每一个值都换成新的流， 然后将所有流合并连接成一个流


![img_8.png](img_8.png)

排序操作
![img_9.png](img_9.png)

####终止操作
查找与匹配

![img_10.png](img_10.png)

![img_11.png](img_11.png)

---
规约和收集
![img_12.png](img_12.png)

![img_13.png](img_13.png)

![img_15.png](img_15.png)

---
分组
![img_16.png](img_16.png)

![img_17.png](img_17.png)

---
分区
![img_18.png](img_18.png)

---
汇总
![img_19.png](img_19.png)

---
字符串连接
![img_20.png](img_20.png)

###2、练习
![img_21.png](img_21.png)

用map和reduce统计总数
![img_22.png](img_22.png)













