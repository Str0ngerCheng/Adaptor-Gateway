package com.swe.gateway.dao;

import com.swe.gateway.model.MavLinkRawData;
import com.swe.gateway.model.UavGps;
import com.swe.gateway.model.UavVfrHud;
import com.swe.gateway.model.UavBatteryStatus;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljy
 * @since 2020-09-24
 */
@Repository
public interface UavGpsMapper {

    List<MavLinkRawData> getMavlinkRawDateByUavId(@Param("UavId") String UavId);

    MavLinkRawData getLatestMavLinkRawData(@Param("UavId") String UavId);

    MavLinkRawData getMavLinkRawDataByUavIdAndDate(@Param("UavId") String UavId, @Param("receivingTime") Date receivingTime);

    int addMavLinkRawData(MavLinkRawData mavLinkRawData);

    List<UavGps> getUavGpsByUavId(@Param("UavId") String UavId);

    UavGps getLatestUavGps(@Param("UavId") String UavId);

    UavGps getUavGpsByUavIdAndDate(@Param("UavId") String UavId, @Param("receivingTime") Date receivingTime);

    int addUavGps(UavGps uavGps);

    List<UavVfrHud> getUavVfrHudByUavId(@Param("UavId") String UavId);

    UavVfrHud getLatestUavVfrHud(@Param("UavId") String UavId);

    UavVfrHud getUavVfrHudByUavIdAndDate(@Param("UavId") String UavId, @Param("receivingTime") Date receivingTime);

    int addUavVfrHud(UavVfrHud vfrHud);

    List<UavBatteryStatus> getUavBatteryStatusByUavId(@Param("UavId") String UavId);

    UavBatteryStatus getLatestUavBatteryStatus(@Param("UavId") String UavId);

    UavBatteryStatus getUavBatteryStatusByUavIdAndDate(@Param("UavId") String UavId, @Param("receivingTime") Date receivingTime);

    int addUavBatteryStatus(UavBatteryStatus uavGps);

}
