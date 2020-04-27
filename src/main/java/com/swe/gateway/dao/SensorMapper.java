package com.swe.gateway.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.swe.gateway.model.Sensor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    Sensor getSensorByName(@Param("name") String name);

    List<Sensor> getSensorsByType(@Param("typeId") String typeId, @Param("protocol") String protocol);


}
