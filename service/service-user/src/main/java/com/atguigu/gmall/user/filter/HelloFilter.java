package com.atguigu.gmall.user.filter;

import javax.servlet.*;

import java.io.IOException;


public class HelloFilter implements Filter {


    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {


        //目标方法执行之前

        //放行
        chain.doFilter(request,response);


        //目标方法执行之后


    }
}
