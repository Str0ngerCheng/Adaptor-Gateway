package com.swe.gateway.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.swe.gateway.model.ObservationProperty;
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
public interface ObservationPropertyMapper extends BaseMapper<ObservationProperty> {
    List<String> getTypesBySensorId(@Param("sensorId")Integer sensorId);

    List<String> getTypeIdsByTypes(@Param("types")List<String> types);

    ObservationProperty getObsPropByType(@Param("type")String type);



}
