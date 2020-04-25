package com.swe.gateway.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.swe.gateway.model.Sensor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cbw
 * @since 2020-04-24
 */
@Repository
public interface SensorMapper extends BaseMapper<Sensor> {
    Sensor getSensorsByName(@Param("name") String name, @Param("protocol") String protocol);

}
