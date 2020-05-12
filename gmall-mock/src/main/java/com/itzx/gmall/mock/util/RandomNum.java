package com.itzx.gmall.mock.util;

import java.util.Random;

/**
 * author: yyeleven
 * create: 2020/4/21 22:44
 */
public class RandomNum {

    public static final int getRandInt(int fromNum, int toNum){
        return fromNum + new Random().nextInt(toNum - fromNum + 1);
    }

}
