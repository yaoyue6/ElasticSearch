package com.yaoyue.elasticsearch.controller;


import com.yaoyue.elasticsearch.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: WangDongXu (15224931482)
 * @date: 2020/12/31 14:16
 **/
@RestController
public class ProductController {

    @Autowired
    ProductService productService;


    /**
     * 解析商品
     *
     * @param keyword
     * @return
     * @throws Exception
     */
    @GetMapping("/product/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws Exception {
        return productService.parseProduct(keyword);
    }

    /**
     * 分页查询
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> searchRequest(@PathVariable("keyword") String keyword,
                                                   @PathVariable("pageNo") int pageNo,
                                                   @PathVariable("pageSize") int pageSize) throws IOException {
        if (pageNo == 0) {
            pageNo = 1;
        }
        if(pageSize == 0) {
            pageSize = 10;
        }

        return productService.searchRequest(keyword, pageNo, pageSize);
    }

}
