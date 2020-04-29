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
    Sensor getSensorByName(@Param("sensorName") String sensorName);

    List<Sensor> getSensorsByType(@Param("typeId") String typeId, @Param("protocol") String protocol);

    int addSensor(Sensor sensor);

    int deleteSensor(@Param("sensorName")String sensorName,@Param("protocol") String protocol);

    int updateSensor(Sensor sensor);

    List<Sensor> getAllSensor();

    Sensor getSensorById(@Param("sensorId")Integer sensorId);

    List<Sensor> getSensorsByProtocol( @Param("protocol") String protocol);


}
