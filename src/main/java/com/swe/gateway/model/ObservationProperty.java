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
    @TableField("value_tpye")
    private Integer valueTpye;
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

    public Integer getValueTpye() {
        return valueTpye;
    }

    public void setValueTpye(Integer valueTpye) {
        this.valueTpye = valueTpye;
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
        ", valueTpye=" + valueTpye +
        ", decription=" + decription +
        "}";
    }
}
