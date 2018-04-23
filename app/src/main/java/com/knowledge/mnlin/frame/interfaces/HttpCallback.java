package com.knowledge.mnlin.frame.interfaces;

/**
 * Created on 2018/4/10
 * function : callback回调
 *
 * @author ACChain
 */

public interface HttpCallback<T> {
    Object run(T param);
}
