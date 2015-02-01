package edu.xiyou.andrew.Egg.parse.filter;

/**
 * Created by andrew on 15-2-1.
 */

public interface Filter<E>{
    /**
     *
     * @param element
     * @return
     */
    public boolean contains(E element);

    /**
     *
     * @param element
     */
    public void add(E element);

    /**
     *
     */
    public void clear();

    /**
     *
     * @return
     */
    public int count();
}