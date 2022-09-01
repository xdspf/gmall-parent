package com.atguigu.gmall.product.schedule;

/*
    重建布隆任务
 */

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RebuildBloomTask {

    @Autowired
    BloomOpsService bloomOpsService;

    @Autowired
    BloomDataQueryService bloomDataQueryService;

    //每隔7天重建一次； bitmap（更符合sku的场景）  88
    //  *  *  *  *  *  ?   *
    // 秒 分  时 日 月  周  年

    /**
     * https://cron.qqe2.com/
     * 生产环境： 0 0 3 ? * 3
     */


    @Scheduled(cron = "0 0 3 ? * 3")//每秒
    public void  rebuild(){
        bloomOpsService.rebuildBloom(SysRedisConst.BLOOM_SKUID,bloomDataQueryService);
    }

}
