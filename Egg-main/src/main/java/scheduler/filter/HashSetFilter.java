package scheduler.filter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andrew on 15-1-30.
 */
public class HashSetFilter<E> implements Filter<E>{
    private Set<E> set = Collections.newSetFromMap(new ConcurrentHashMap<E, Boolean>());

    @Override
    public boolean contains(E element) {
        return !set.add(element);
    }

    @Override
    public void add(E element) {
        set.add(element);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public int count() {
        return set.size();
    }


}
