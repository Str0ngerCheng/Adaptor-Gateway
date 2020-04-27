package com.swe.gateway.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author cbw
 * @since 2020-04-25
 */
public class ObservationProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "obs_prop_id", type = IdType.AUTO)
    private Integer obsPropId;
    @TableField("obs_prop_name")
    private String obsPropName;
    @TableField("value_type")
    private Integer valueType;
    @TableField("uom")
    private String uom;
    @TableField("decription")
    private String decription;


    public Integer getObsPropId() {
        return obsPropId;
    }

    public void setObsPropId(Integer obsPropId) {
        this.obsPropId = obsPropId;
    }

    public String getObsPropName() {
        return obsPropName;
    }

    public void setObsPropName(String obsPropName) {
        this.obsPropName = obsPropName;
    }

    public Integer getValueType() {
        return valueType;
    }

    public void setValueType(Integer valueType) {
        this.valueType = valueType;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    @Override
    public String toString() {
        return "ObservationProperty{" +
                "obsPropId=" + obsPropId +
                ", obsPropName=" + obsPropName +
                ", valueType=" + valueType +
                ", uom=" + uom +
                ", decription=" + decription +
                "}";
    }
}
