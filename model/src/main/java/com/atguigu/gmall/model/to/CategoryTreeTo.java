package com.atguigu.gmall.model.to;

import lombok.Data;

import java.util.List;


/*
    DDD（Domain-Driven Design）：领域驱动设计
 */
@Data
public class CategoryTreeTo {

    private Long categoryId;
    private String categoryName;
    private List<CategoryTreeTo> categoryChild; //子类

}
