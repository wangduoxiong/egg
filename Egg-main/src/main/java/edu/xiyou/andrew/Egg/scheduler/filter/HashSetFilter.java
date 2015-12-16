package edu.xiyou.andrew.Egg.scheduler.filter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by andrew on 15-12-16.
 */
public class HashSetFilter<E> implements Serializable, Filter<E>{

    private Set<E> hashSet;

    {
        hashSet = new HashSet<E>();
    }

    @Override
    public boolean contains(E element) {
        return hashSet.contains(element);
    }

    @Override
    public void add(E element) {
        hashSet.add(element);
    }

    @Override
    public int count() {
        return hashSet.size();
    }

    @Override
    public void clear() {
        hashSet.clear();
    }
}
