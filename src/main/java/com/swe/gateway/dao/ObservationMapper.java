package com.swe.gateway.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.swe.gateway.model.Observation;
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
public interface ObservationMapper extends BaseMapper<Observation> {
    List<Observation> getHistoryData(@Param("sensorId")int sensorId,@Param("typeId")int typeId,
                                     @Param("start")int start,@Param("end")int end);

    int deleteObservation(@Param("sensorId")Integer sensorId, @Param("obsPropId")Integer obsPropId);

    Observation getObservationByIds(@Param("sensorId")Integer sensorId, @Param("obsPropId")Integer obsPropId);

}
