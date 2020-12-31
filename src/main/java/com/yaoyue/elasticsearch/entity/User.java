package com.yaoyue.elasticsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 用户实体类
 * @author: WangDongXu (15224931482)
 * @date: 2020/12/30 17:04
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别
     */
    private String sex;

    /**
     * 爱好
     */
    private List<String> hobby;
}
