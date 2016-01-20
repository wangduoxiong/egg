package edu.xiyou.andrew.egg.scheduler.filter;

/**
 * Created by andrew on 15-12-16.
 */
public interface Filter<E> {

    //判断是否包含该元素
    boolean contains(E element);

    //将指定元素添加到过滤器中
    void add(E element);

    // 返回过滤器中元素个数
    int count();

    void clear();
}
