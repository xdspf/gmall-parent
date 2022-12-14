package com.atguigu.gmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.atguigu.gmall.order.mapper.OrderStatusLogMapper;
import org.springframework.stereotype.Service;

/**
* @author SPF
* @description 针对表【order_status_log】的数据库操作Service实现
* @createDate 2022-09-13 21:25:54
*/
@Service
public class OrderStatusLogServiceImpl extends ServiceImpl<OrderStatusLogMapper, OrderStatusLog>
    implements OrderStatusLogService{

}




