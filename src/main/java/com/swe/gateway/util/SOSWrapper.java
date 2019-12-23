package com.swe.gateway.util;

import com.swe.gateway.model.StructObservation;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.joda.time.DateTime;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author cbw
 */
public class SOSWrapper {
    private String sensorID;
    private String samplingTime;
    private double longitude;
    private double latitude;
    private List<StructObservation> structObservations;
    private String sosAddress;

    public SOSWrapper(String sensorID, String samplingTime, double longitude, double latitude, List<StructObservation> structObservations, String sosAddress) {
        this.sensorID = sensorID;
        this.samplingTime = samplingTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.structObservations = structObservations;
        this.sosAddress = sosAddress;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getSamplingTime() {
        return samplingTime;
    }

    public void setSamplingTime(String samplingTime) {
        this.samplingTime = samplingTime;
    }

    public String getSosAddress() {
        return sosAddress;
    }

    public void setSosAddress(String sosAddress) {
        this.sosAddress = sosAddress;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public List<StructObservation> getStructObservations() {
        return structObservations;
    }

    public void setStructObservations(List<StructObservation> structObservations) {
        this.structObservations = structObservations;
    }

    public String insertSOS()
    {
        //构造SOS InsertObservation操作所需的O&M文档
        String requestContent="";
        try {
            requestContent = createSOSInsertObservationRequestXml ();
        }catch (Exception e){
            e.printStackTrace ();
        }
        //响应数据
        String responseXml = sendRequest(sosAddress, requestContent);

        return responseXml;
    }

    public String sendRequest(String URLString, String requestContent){
        String resp = null;
        String resultJudge="";
        try {
            resp = Jsoup.connect (URLString).method (
                    Connection.Method.POST).data ("request", requestContent)
                    .timeout (30000).ignoreContentType (true).header (
                            "Content-Type", "application/xml; charset=UTF-8")
                    .execute ( ).body ( );

            resultJudge=FileUtil.formatXml(resp);
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        return resultJudge;
    }

    public String createSOSInsertObservationRequestXml (){
        String instantFilePath=FileUtil.getInstantFile ();
        String tempFilePath=FileUtil.getTemporaryFile ();

        FileInputStream fin = null;
        FileWriter fw=null;
        XMLWriter writer=null;
        try {
            fin = new FileInputStream (instantFilePath);
            SAXReader reader = new SAXReader();
            reader.setEncoding ("UTF-8");
            Document doc = reader.read(fin, "UTF-8");
            // 获取根标签对象
            Element rootElement = doc.getRootElement();
            // 获取根标签下的子标签 默认获取的是第一个子标签
            Element IdElement = rootElement.element("AssignedSensorId");
            IdElement.setText (sensorID);
            Element ObsElement = rootElement.element("Observation");
            for (Iterator iter = ObsElement.elementIterator(); iter.hasNext();) {
                Element element = (Element) iter.next ( ); // 获取标签对象
                if(element.getQualifiedName ().equals ("om:samplingTime")) {
                    Element timePositionElement = element.element ("TimeInstant").element ("timePosition");
                    timePositionElement.setText (samplingTime);
                }
                else if(element.getQualifiedName ().equals ("om:procedure")) {
                    element.attribute ("href").setValue (sensorID);
                }

                else if(element.getQualifiedName ().equals ("om:observedProperty")) {
                    Element compositePhenomenonElement = element.element ("CompositePhenomenon");

                    for(StructObservation structObservation:structObservations) {
                        Element e_obsPropertyElement = compositePhenomenonElement.addElement ("swe:component");
                        String attrValue="urn:ogc:def:property:OGC:1.0:" + structObservation.getObservedProperty ();
                        e_obsPropertyElement.addAttribute ("xlink:href", attrValue);
                    }
                }
                else if(element.getQualifiedName ().equals ("om:featureOfInterest")) {
                    Element samplingPointElement = element.element ("SamplingPoint");
                    samplingPointElement.attribute ("id").setValue (latitude + " " + longitude);
                    samplingPointElement.element ("name").setText (latitude + " " + longitude);
                    samplingPointElement.element ("position").element ("Point").element ("pos").setText (latitude + " " + longitude);
                }

                else if(element.getQualifiedName ().equals ("om:result")) {
                    Element dataArrayElement = element.element ("DataArray");
                    String resultValue="";
                    for(StructObservation structObservation:structObservations) {
                        Element fieldElement=dataArrayElement.element ("elementType").element ("DataRecord").addElement ("swe:field");
                        fieldElement.addAttribute ("name",structObservation.getObservedResultName ());

                        Element quantityElement=fieldElement.addElement ("swe:Quantity");
                        String quantityAttr="urn:ogc:def:property:OGC:1.0:" + structObservation.getObservedProperty ();
                        quantityElement.addAttribute ("definition",quantityAttr);

                        Element uomElement=quantityElement.addElement ("swe:uom");
                        uomElement.addAttribute ("code",structObservation.getUom ());

                        if (structObservations.indexOf(structObservation) == structObservations.size () - 1)
                        {
                            resultValue += structObservation.getResultValue ();
                        }
                        else
                        {
                            resultValue += structObservation.getResultValue ()+",";
                        }
                    }
                    dataArrayElement.element ("values").setText (samplingTime+","+resultValue+";");
                }
            }
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding ("UTF-8");

            fw=new FileWriter (tempFilePath);
            writer= new XMLWriter(fw, format);//重新写回到原来的xml文件中
            writer.write(doc);
        } catch (FileNotFoundException e) {
            e.printStackTrace ( );
        } catch (DocumentException e) {
            e.printStackTrace ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }finally {
            try {
                writer.close ();
                fw.close ();
                fin.close ();
            } catch (IOException e) {
                e.printStackTrace ( );
            }
        }
        String requestContent=FileUtil.readFileContent(tempFilePath,"UTF-8");

        return requestContent;
    }

}
