package com.atguigu.gmall.item.annotation;


import java.lang.annotation.*;

/*
    缓存注解
 */
@Target({ElementType.METHOD}) //能标注的位置
@Retention(RetentionPolicy.RUNTIME) //运行时有效
@Inherited
public @interface GmallCache {

}
