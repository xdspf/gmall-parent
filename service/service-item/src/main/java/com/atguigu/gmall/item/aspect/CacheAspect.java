package com.atguigu.gmall.item.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect  //这是切面
@Component //容器中的组件，放入容器中
public class CacheAspect {


    //在标注了 @GmallCache 注解的方法之前执行
//    @Before("@annotation(com.atguigu.gmall.item.annotation.GmallCache)")
//    public void spf(){
//        System.out.println("前置通知");
//    }


    /**
     *   目标方法： public SkuDetailTo getSkuDetailWithCache(Long skuId)
     *   连接点：所有目标方法的信息都在连接点
     *
     *   try{
     *       //前置通知
     *       目标方法.invoke(args)
     *       //返回通知
     *   }catch(Exception e){
     *       //异常通知
     *   }finally{
     *       //后置通知
     *   }
     */





}
