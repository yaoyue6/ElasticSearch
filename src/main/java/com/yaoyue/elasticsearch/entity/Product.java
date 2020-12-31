package com.yaoyue.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 商品信息
 * @author: WangDongXu (15224931482)
 * @date: 2020/12/30 22:13
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * 图片
     */
    private String img;

    /**
     * 价格
     */
    private String price;

    /**
     * 标题
     */
    private String title;
}
