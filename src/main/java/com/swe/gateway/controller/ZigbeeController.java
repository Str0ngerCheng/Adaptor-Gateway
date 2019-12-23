package com.swe.gateway.controller;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * @author cbw
 */
@RestController
public class ZigbeeController {
    static Logger logger = LogManager.getLogger(ZigbeeController.class.getName());
    @RequestMapping(value = "/zigbee", method = RequestMethod.POST)
    public Map getByRequest(HttpServletRequest request) {
        return null;
    }
}
