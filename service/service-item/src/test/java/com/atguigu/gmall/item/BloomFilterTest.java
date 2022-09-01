package com.atguigu.gmall.item;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;

public class BloomFilterTest {


    @Test
    public void test(){

        /**
         * Funnel<? super T> funnel,
         *          (from,into)->{
         *             into.putLong( Long.parseLong(from.toString()));
         *         }
         * int expectedInsertions,  期望插入的数量： 1w
         * double fpp：false positive probability  误判率，越高，bloom存东西hash次数越多，占位越多
         */
        //1、创建出布隆过滤器
        BloomFilter<Long> filter = BloomFilter.create(Funnels.longFunnel(), 20000, 0.0001);

        //2.添数据
        for (long i = 0; i < 20; i++) {
            filter.put(i);
        }

        //3.判定有没有
        System.out.println(filter.mightContain(1l));
        System.out.println(filter.mightContain(20l));
        System.out.println(filter.mightContain(99l));


    }
}
