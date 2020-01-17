package com.swe.gateway.dao;

import com.swe.gateway.config.NBIOTConfig;
import com.swe.gateway.model.NBIOT;
import com.swe.gateway.model.StructObservation;
import com.swe.gateway.util.SOSWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lx
 */
@Repository
public class NBIOTRepository {
    static Logger logger = LogManager.getLogger(NBIOTRepository.class.getName());

    public void insertNBIOT(NBIOT nbiot) {
        final List<SOSWrapper> sosWrappers = new ArrayList<SOSWrapper>();//气象要素SOS封装类对象列表
        List<StructObservation> lstStructObs;//传感器观测信息结构体列表
        //观测时间
        //String samplingTime = new SimpleDateFormat("yyyy-MM-dd"+"HH:mm:ss").format(new Date(nbiot.getTimestamp() * 1000));
        String samplingTime = new DateTime(nbiot.getTimestamp() * 1000).toString();//观测时间
        //region 土壤温度
        double dSoilTemperature = nbiot.getTemperature();
        //region 土壤温度
        double dSoilHumidity = nbiot.getHumidity();
        //设备id
        String deviceID = nbiot.getDeviceID();

        logger.info(deviceID + "  dSoilTemperature:" + dSoilTemperature + "  dSoilHumidity:" + dSoilHumidity + "  samplingTime:" + samplingTime);
        StructObservation _structObsSoilTemperature = new StructObservation(NBIOTConfig.SoilTemperature_ObsProperty, NBIOTConfig.SoilTemperature_ObsResultName, NBIOTConfig.SoilTemperature_ObsResultUom, dSoilTemperature);
        StructObservation _structObsSoilHumidity = new StructObservation(NBIOTConfig.SoilHumidity_ObsProperty, NBIOTConfig.SoilHumidity_ObsResultName, NBIOTConfig.SoilHumidity_ObsResultUom, dSoilHumidity);
        lstStructObs = new ArrayList<StructObservation>();
        lstStructObs.add(_structObsSoilTemperature);
        lstStructObs.add(_structObsSoilHumidity);

        SOSWrapper soilTemeratureHumiditySOSWrapper = new SOSWrapper(NBIOTConfig.deviceID_SOSID.get(nbiot.getDeviceID()), samplingTime, NBIOTConfig.Longitude, NBIOTConfig.Latitude, lstStructObs, NBIOTConfig.SOS_Url);
        sosWrappers.add(soilTemeratureHumiditySOSWrapper);
        //#endregion
        if (sosWrappers != null) {
            for (SOSWrapper sosWrapper : sosWrappers) {
                if (sosWrapper != null) {
                    try {
                        sosWrapper.insertSOS();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }
}
