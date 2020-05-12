package com.itzx.gmall.mock.util;

/**
 * author: yyeleven
 * create: 2020/4/21 22:42
 */
public class RanOpt<T> {

    T value ;
    int weight;

    public RanOpt ( T value, int weight ){
        this.value=value ;
        this.weight=weight;
    }

    public T getValue() {
        return value;
    }
    public int getWeight() {
        return weight;
    }

}
