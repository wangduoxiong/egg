/*
 *Copyright (c) 2015 Andrew-Wang.
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package edu.xiyou.andrew.Egg.parse.filter;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by andrew on 15-2-1.
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
