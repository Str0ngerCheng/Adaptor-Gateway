package com.swe.gateway.model;

import lombok.Data;

/**
 * @author cbw
 */

/**
 * create by jack 2018/4/21
 *城市实体类
 */
@Data
public class City {
    /**
     * 城市编号
     */
    private Long id;

    /**
     * 省份编号
     */
    private Long provinceId;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 描述
     */
    private String description;
}

