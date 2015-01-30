package scheduler.filter;

/**
 * Created by andrew on 15-1-30.
 */
public interface Filter<E> {
    /**
     * 判断是否已经包含
     * @param element
     * @return
     */
    public boolean contains(E element);

    /**
     * 添加元素
     * @param element
     */
    public void add(E element);

    /**
     * 清除之前的记录
     */
    public void clear();

    /**
     * 获取当前存入元素的数量
     * @return
     */
    public int count();
}
