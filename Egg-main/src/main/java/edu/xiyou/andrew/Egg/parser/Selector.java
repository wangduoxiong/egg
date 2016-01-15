package edu.xiyou.andrew.Egg.parser;

import edu.xiyou.andrew.Egg.model.Page;

import java.util.List;

/**
 * 判断器，判断所给条件是否符合要求，主要使用在{@link edu.xiyou.andrew.Egg.parser.Parser}中<br>
 *     例如可以判断{@link Page#getAllLinksList()}中是否满足要求，可被添加至列表中<br>
 * Created by andrew on 16-1-11.
 */
public interface Selector<E> {
    boolean isTarget(E e);

    List<E> getAllTarget(List<E> elementList);
}
