package com.atguigu.starter.cache.aspect;


import com.atguigu.starter.cache.annotation.GmallCache;
import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Aspect  //这是切面
@Component //容器中的组件，放入容器中
public class CacheAspect {


    //在标注了 @GmallCache 注解的方法之前执行
//    @Before("@annotation(com.atguigu.gmall.item.annotation.GmallCache)")
//    public void spf(){
//        System.out.println("前置通知");
//    }


    /**
     * 目标方法： public SkuDetailTo getSkuDetailWithCache(Long skuId)
     * 连接点：所有目标方法的信息都在连接点
     * <p>
     * try{
     * //前置通知
     * 目标方法.invoke(args)
     * //返回通知
     * }catch(Exception e){
     * //异常通知
     * }finally{
     * //后置通知
     * }
     */

    ExpressionParser parser = new SpelExpressionParser(); //线程安全的
    TemplateParserContext context = new TemplateParserContext();//final的


    @Autowired
    CacheOpsService cacheOpsService; //封装的缓存操作


    @Around("@annotation(com.atguigu.starter.cache.annotation.GmallCache)") //环绕通知
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

//        Object arg = joinPoint.getArgs()[0]; //获取参数： 49
        Object result = null;

        //key不同
        String cacheKey = determinCacheKey(joinPoint);

        //1.先查询缓存
        Type returnType = getMethodGenericReturnType(joinPoint);

        Object cacheData = cacheOpsService.getCacheData(cacheKey, returnType);

//        SkuDetailTo cacheData = cacheOpsService.getCacheData(cacheKey, SkuDetailTo.class);

        //2.判断缓存中是否有数据
        if (cacheData == null) {
            //3.准备回源
            //4.先查询布隆
//            boolean contains = cacheOpsService.bloomContains(arg);

            String bloomName = determinBloomName(joinPoint);
            if (!StringUtils.isEmpty(bloomName)){
                Object bVal =  determinBloomValue(joinPoint);
                boolean contains = cacheOpsService.bloomContains(bloomName, bVal);

                if (!contains) {
                    //布隆说没有
                    return null;
                }
            }


            //5.布隆说有，准备回源，有击穿风险，先加锁

            boolean lock = false;  //尝试加锁
            String lockName ="";
            try {
                  lockName =  determinLockName(joinPoint);
                lock = cacheOpsService.tryLock(lockName);
                if (lock) {
                    //6.获取到锁，开始回源
                    result = joinPoint.proceed(joinPoint.getArgs());
                    //7.调用成功，重新保存到缓存
                    cacheOpsService.saveData(cacheKey, result);
                    return result;

                } else {
                    //未加到锁,等待一秒，直接返回
                    Thread.sleep(1000L);
                    return cacheData = cacheOpsService.getCacheData(cacheKey, returnType);
                }
            } finally {
                //解锁
                if (lock) cacheOpsService.unlock(lockName);

            }

        }
        //缓存中有
        return cacheData;
    }

    /**
     * 根据表达式计算出要用的锁的名字
     * @param joinPoint
     * @return
     */

    private String determinLockName(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);
        //3.拿到锁表达式
        String lockName = cacheAnnotation.lockName();
        if (StringUtils.isEmpty(lockName)){
            //没指定锁用方法级别的锁
            return SysRedisConst.LOCK_PREFIX +method.getName();
        }
        //4.计算锁值
        String lockNameVal = evaluationExpression(lockName, joinPoint, String.class);
        return lockNameVal;

    }

    /**
     * 根据布隆过滤器值表达式计算出布隆需要判定的值
     * @param joinPoint
     * @return
     */

    private Object determinBloomValue(ProceedingJoinPoint joinPoint) {
        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        //3.拿到布隆表达式
        String bloomValue = cacheAnnotation.bloomValue();
        Object expression = evaluationExpression(bloomValue, joinPoint, Object.class);
        return expression;

    }

    private String determinBloomName(ProceedingJoinPoint joinPoint) {


        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        //3.获取注解上的cacheKey
        String bloomName = cacheAnnotation.bloomName();

        return bloomName;
    }

    /**
     * 获取目标方法的精确返回值类型
     *
     * @param joinPoint
     * @return
     */

    private Type getMethodGenericReturnType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Type type = method.getGenericReturnType();
        return type;
    }

    /**
     * 根据当前整个连接点的执行信息，确定缓存用什么key
     *
     * @param joinPoint
     * @return
     */
    private String determinCacheKey(ProceedingJoinPoint joinPoint) {

        //1.拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        //2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        //3.获取注解上的cacheKey
        String expression = cacheAnnotation.cacheKey();

        //4.根据表达式计算缓存键
        String cacheKey = evaluationExpression(expression, joinPoint, String.class);
        return cacheKey;
    }

    private <T> T evaluationExpression(String expression, ProceedingJoinPoint joinPoint,
                                       Class<T> clz) {

        //1.创建表达式解析器
        Expression exp = parser.parseExpression(expression, context);

        //2、sku:info:#{#params[0]}
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

        //3、取出所有参数，绑定到上下文
        Object[] args = joinPoint.getArgs();
        evaluationContext.setVariable("params", args);

        //4、得到表达式的值
        T expValue = exp.getValue(evaluationContext, clz);
        return expValue;
    }




    /*
            模板
     */
   /* @Around("@annotation(com.atguigu.gmall.item.annotation.GmallCache)") //环绕通知
    public Object around(ProceedingJoinPoint joinPoint) {
        //1.获取签名，将要执行的目标方法的签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        //2.获取当时调用者调用目标方法时传递的所有参数
        Object[] args = joinPoint.getArgs();


        System.out.println(joinPoint.getTarget());
        System.out.println(joinPoint.getThis());


        //3.放行目标方法
        Method method = signature.getMethod();

        //以上是前置通知
        Object result = null;
        try {
            //目标方法执行，并返回返回值   修改参数
             result = method.invoke(joinPoint.getTarget(), args);


            //返回通知
        } catch (Exception e) {
//            e.printStackTrace();
            throw new RuntimeException();
            //异常通知

        } finally {
            //后置通知


        }
        //修改返回值
        return result;
    }*/
}
