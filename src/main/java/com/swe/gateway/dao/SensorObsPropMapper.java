package com.swe.gateway.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.swe.gateway.model.SensorObsProp;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author cbw
 * @since 2020-04-25
 */
@Repository
public interface SensorObsPropMapper extends BaseMapper<SensorObsProp> {
    //int addSenObsPro(Map<String, Integer> map);
 int addSenObsPro(@Param("sensorId")Integer sensorId,@Param("obsPropId")Integer obsPropId );

 int deleteSenObsPro(@Param("sensorId")Integer sensorId,@Param("obsPropId")Integer obsPropId );

 List<SensorObsProp> getAllSensorObsProp();

 List<SensorObsProp> getByObsPropId(@Param("obsPropId")Integer obsPropId);

 List<SensorObsProp> getBySensorId(@Param("sensorId")Integer sensorId);

 List<SensorObsProp> getSenObsProByProtocol(@Param("protocol")String protocol);

}
