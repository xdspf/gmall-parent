package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.List;

public interface CartService {

    SkuInfo addItemToCart(Long skuId, Integer num, String cartKey);


    String determinCartKey();

    SkuInfo addToCart(Long skuId, Integer num);

    CartInfo getItemFromCart(String cartKey, Long skuId);

    List<CartInfo> getCartList(String cartKey);

    void updateItemNum(Long skuId, Integer num, String cartKey);

    void updateChecked(Long skuId, Integer status, String cartKey);

    void deleteCartItem(Long skuId, String cartKey);

    void deleteChecked(String cartKey);

    /**
     * 获取指定购物车中所有选中的商品
     * @param cartKey
     * @return
     */
    List<CartInfo> getCheckedItems(String cartKey);

    void mergeUserAndTempCart();

    void updateCartAllItemsPrice(String cartKey);

}
