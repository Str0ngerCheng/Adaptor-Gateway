
package com.swe.gateway.service;

import com.alibaba.fastjson.JSONObject;
import com.swe.gateway.dao.ObservationMapper;
import com.swe.gateway.dao.ObservationPropertyMapper;
import com.swe.gateway.dao.SensorMapper;
import com.swe.gateway.dao.SensorObsPropMapper;
import com.swe.gateway.dto.ObservationDTO;
import com.swe.gateway.dto.SensorDTO;
import com.swe.gateway.model.Observation;
import com.swe.gateway.model.ObservationProperty;
import com.swe.gateway.model.Sensor;
import com.swe.gateway.model.SensorObsProp;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class SensorHandler {
    @Autowired
    SensorMapper sensorMapper;
    @Autowired
    ObservationPropertyMapper observationPropertyMapper;
    @Autowired
    ObservationMapper observationMapper;
    @Autowired
    SensorObsPropMapper sensorObsPropMapper;

    public Mono<ServerResponse> getSensorsByName(ServerRequest request) {
        String name =  request.queryParams().getFirst("sensorName");
        Sensor sensor=sensorMapper.getSensorByName(name);
        List<String> types=new ArrayList<>();
        if(sensor!=null)
            types =observationPropertyMapper.getTypesBySensorId(sensor.getSensorId());
        List<SensorDTO> sensorDTOS=new ArrayList<>();
        for(int i=0;i<types.size();i++){
            SensorDTO sensorDTO=new SensorDTO(sensor,types.get(i));
            sensorDTOS.add(sensorDTO);
            System.out.println(sensorDTO.getName()+" "+sensorDTO.getLocation());
        }
        Collections.sort(sensorDTOS);

        Flux<SensorDTO> sensorDTOFlux = Flux.fromIterable(sensorDTOS);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(sensorDTOFlux,SensorDTO.class);
    }

    public Mono<ServerResponse> getSensorsByType(ServerRequest request) {
        List<String> types= request.queryParams().get("type[]");
        String protocol= request.queryParams().getFirst("protocol");
        List<String>typeIds=observationPropertyMapper.getTypeIdsByTypes(types);
        List<SensorDTO> sensorDTOS=new ArrayList<>();
        for(int i=0;i<typeIds.size();i++){
            List<Sensor> sensors=sensorMapper.getSensorsByType(typeIds.get(i),protocol);
            for(int j=0;j<sensors.size();j++) {
                SensorDTO sensorDTO = new SensorDTO(sensors.get(j), types.get(i));
                sensorDTOS.add(sensorDTO);
            }
        }
        Collections.sort(sensorDTOS);
        Flux<SensorDTO> sensorDTOFlux = Flux.fromIterable(sensorDTOS);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(sensorDTOFlux,SensorDTO.class);
    }

    public Mono<ServerResponse> getHistoryData(ServerRequest request) {
        String name =  request.queryParams().getFirst("sensorName");
        String type= request.queryParams().getFirst("type");
        Integer sensorId=sensorMapper.getSensorByName(name).getSensorId();

        ObservationProperty obsProperty=observationPropertyMapper.getObsPropByType(type);
        Integer typeId=obsProperty.getObsPropId();
        String uom=obsProperty.getUom();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        DateTime dateTime=new DateTime();
        int start=Integer.valueOf(df.format(dateTime.minusDays(30).toDate()));
        int end=Integer.valueOf(df.format(dateTime.minusDays(1).toDate()));

        List<Observation> observations=observationMapper.getHistoryData(sensorId,typeId,start,end);
        ObservationDTO obsDTO=new ObservationDTO(name,type,uom,observations);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type","application/json; charset=utf-8")
                .body(Mono.just(obsDTO),ObservationDTO.class);
    }

//增加传感器和传感器-类型，前提：类型是数据库中已有的类型
    public Mono<ServerResponse> add(ServerRequest request) {
        Mono<String> str = request.bodyToMono(String.class);
        return str.flatMap(s -> {
            JSONObject json = JSONObject.parseObject(s);
            Sensor sensor=new Sensor();
            sensor.setSensorName(json.getJSONObject("params").getString("name"));
           // sensor.setIsInsitu(json.getInteger("isInsitu"));
            sensor.setLocation(json.getJSONObject("params").getString("location"));
            sensor.setStatus(json.getJSONObject("params").getInteger("status"));
            sensor.setProtocol(json.getJSONObject("params").getString("protocol"));
            sensor.setDecription(json.getJSONObject("params").getString("decription"));
            String type=json.getJSONObject("params").getString("type");

           Sensor test=sensorMapper.getSensorByName(sensor.getSensorName());

           if(test==null) {
              sensorMapper.addSensor(sensor);
            }
            ObservationProperty obsProperty=observationPropertyMapper.getObsPropByType(type);
            Sensor sensor1=sensorMapper.getSensorByName(sensor.getSensorName());

            Integer sensorId=sensor1.getSensorId();
            Integer obsPropId=obsProperty.getObsPropId();
            int result= sensorObsPropMapper.addSenObsPro(sensorId,obsPropId);

            Flux<Integer> sensorFlux = Flux.just(result);

            return ServerResponse.ok().contentType(APPLICATION_JSON)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .body(sensorFlux, Integer.class);
        });
    }

    //删除观测记录及传感器-类型记录，不会删除传感器和对应的类型
    public Mono<ServerResponse> delete(ServerRequest request) {
        String name =  request.queryParams().getFirst("sensorName");
        String type= request.queryParams().getFirst("type");
        Integer sensorId=sensorMapper.getSensorByName(name).getSensorId();
        ObservationProperty obsProperty=observationPropertyMapper.getObsPropByType(type);
        Integer typeId=obsProperty.getObsPropId();

        int result1=observationMapper.deleteObservation(sensorId,typeId);

        int result2=sensorObsPropMapper.deleteSenObsPro(sensorId,typeId);

        int result=result1&result2;

        Flux<Integer> sensorFlux = Flux.just(result);

        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type", "application/json; charset=utf-8")
                .body(sensorFlux, Integer.class);
    }


    //仅更新传感器的值
    public Mono<ServerResponse> update(ServerRequest request) {
        Mono<String> str = request.bodyToMono(String.class);
        return str.flatMap(s -> {
            JSONObject json = JSONObject.parseObject(s);
            Sensor sensor=new Sensor();
            sensor.setSensorName(json.getJSONObject("params").getString("name"));
            // sensor.setIsInsitu(json.getInteger("isInsitu"));
            sensor.setLocation(json.getJSONObject("params").getString("location"));
            sensor.setStatus(json.getJSONObject("params").getInteger("status"));
          //  sensor.setProtocol(json.getJSONObject("params").getString("protocol"));
            sensor.setDecription(json.getJSONObject("params").getString("description"));
           // String type=json.getJSONObject("params").getString("type");

            int result1= sensorMapper.updateSensor(sensor);
            //ObservationProperty obsPropertyOld=observationPropertyMapper.getObsPropByType(oldtype);
          //  ObservationProperty observationNew=observationPropertyMapper.getObsPropByType(newtype);
           /* Sensor sensor1=sensorMapper.getSensorByName(sensor.getSensorName());

            Integer sensorId=sensor1.getSensorId();
            Integer obsPropIdOld=obsPropertyOld.getObsPropId();
            Integer obsPropIdNew=observationNew.getObsPropId();
            int result2=sensorObsPropMapper.deleteSenObsPro(sensorId,obsPropIdOld);
            int result3= sensorObsPropMapper.addSenObsPro(sensorId,obsPropIdNew);*/

          //  int result=result1&result2&result3;

            Flux<Integer> sensorFlux = Flux.just(result1);

            return ServerResponse.ok().contentType(APPLICATION_JSON)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .body(sensorFlux, Integer.class);
        });
    }


   //根据协议展示传感器，前提：传感器表，传感器-类型，类型表中有相应的值
   public Mono<ServerResponse> list(ServerRequest request) {
       String protocol=request.queryParams().getFirst("protocol");
       // List<Sensor> sensors=sensorMapper.getSensorsByProtocol(protocol);
       //ObservationProperty observationProperty=observationPropertyMapper.getObsPropByType(type);
       List<SensorObsProp> sensorObsProps=sensorObsPropMapper.getSenObsProByProtocol(protocol);
       List<SensorDTO> sensorDTOS=new ArrayList<>();
       for(int i=0;i<sensorObsProps.size();i++){
           SensorObsProp sensorObsProp=sensorObsProps.get(i);
           Integer sensorId=sensorObsProp.getSensorId();
           Integer obsPropId=sensorObsProp.getObsPropId();
           Sensor sensor=sensorMapper.getSensorById(sensorId);
           /* Observation observation=observationMapper.getObservationByIds(sensorId,obsPropId); */
           ObservationProperty observationProperty=observationPropertyMapper.getObsPropById(obsPropId);

           SensorDTO sensorObservation=new SensorDTO(sensor,observationProperty.getObsPropName());
           sensorDTOS.add(sensorObservation);
       }
       Collections.sort(sensorDTOS);
       Flux<SensorDTO> sensorFlux = Flux.fromIterable(sensorDTOS);

       return ServerResponse.ok().contentType(APPLICATION_JSON)
               .header("Content-Type", "application/json; charset=utf-8")
               .body(sensorFlux, SensorDTO.class);
   }

    public Mono<ServerResponse> allList(ServerRequest request) {
        List<Sensor> sensors = sensorMapper.getAllSensor ();
        Flux<Sensor> sensorFlux = Flux.fromIterable(sensors);
        return ServerResponse.ok().contentType(APPLICATION_JSON)
                .header("Content-Type", "application/json; charset=utf-8")
                .body(sensorFlux, Sensor.class);
    }
}

