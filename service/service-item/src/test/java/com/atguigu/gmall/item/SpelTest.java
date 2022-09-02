package com.atguigu.gmall.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.List;

public class SpelTest {


    @Test
    void test06() throws JsonProcessingException {
        String json = "[\"1\",\"a\"]"; //List<String>
        ObjectMapper mapper = new ObjectMapper();
        List<String> list = mapper.readValue(json, new TypeReference<List<String>>() {
        });

        System.out.println(list);

    }


    @Test
    public void test5(){

        Hello hello = new Hello();
        Method[] methods = hello.getClass().getDeclaredMethods();
//        Method[] methods = hello.getClass().getMethods();
        for (Method method : methods) {
            System.out.println(method.getName() +" 返回值类型" + method.getGenericReturnType());
        }


    }


    @Test
    public void test4(){
        SpelExpressionParser parser = new SpelExpressionParser();

        //加前置
        Expression expression = parser.parseExpression("haha-#{T(java.util.UUID).randomUUID().toString()}", new TemplateParserContext());
        System.out.println(expression.getValue());


    }


    @Test
    public void test3() {

        SpelExpressionParser parser = new SpelExpressionParser();

        Expression expression = parser.parseExpression("new int[]{1,2,3,4}");

        int[] value = (int[]) expression.getValue();
        for (int i : value) {
            System.out.println(i);
        }
    }


    @Test
    public void test2() {
        Object[] params1 = new Object[]{49L, 50, 60};
        Object[] params2 = new Object[]{59L, 80, 90};
        Object[] params3 = new Object[]{69L, 100, 110};

        //1.创建一个表达式解析器
        ExpressionParser parser = new SpelExpressionParser();
        //    表达式                       模板上下文
        Expression expression = parser.parseExpression("sku:info:#{#params[0]}", new TemplateParserContext());

        //2.准备一个计算上下文
        StandardEvaluationContext context = new StandardEvaluationContext();

        //3.变量和上下文环境绑定
        context.setVariable("params", params2);
        String value = expression.getValue(context, String.class);
        System.out.println(value);

        /*Object value = expression.getValue(context);
        System.out.println(value); //sku:info:59*/


    }


    @Test
    public void test1() {

        //1.创建一个表达式解析器
        ExpressionParser parser = new SpelExpressionParser();


        //2、准备一个表达式 'Hello #{1+1}'
        //告诉Spring遇见定界符（#{、}）内部的所有东西都需要动态计算
        String myExpression = "Hello #{1<1}"; //最终 Hello 2

        //3、得到一个表达式
        Expression expression = parser.parseExpression(myExpression, new TemplateParserContext());
        Object value = expression.getValue();
        System.out.println(value);


    }

}
