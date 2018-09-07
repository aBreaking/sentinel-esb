package com.sitech.esb.sentinel.rule;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liwei on 2018/8/29.
 * 在这里面先手动指定一些常量吧
 */
public class RuleConst {
    public static boolean FLOW_OPEN = true;
    public static int FLOW_GRADE = RuleConstant.FLOW_GRADE_QPS;
    public static double FLOW_QPS = 10;

    public static boolean DEGRADE_OPEN = true;
    public static int DEGRADE_GRADE = RuleConstant.DEGRADE_GRADE_EXCEPTION;
    public static double DEGRADE_RATIO = 0.2;
    public static int DEGRADE_TIMEWINDOW = 5;


    public static Map allFlowRules(){
        HashMap flowMap = new HashMap();
        flowMap.put("open",FLOW_OPEN);
        flowMap.put("grade",FLOW_GRADE);
        flowMap.put("qps",FLOW_QPS);
        return flowMap;
    }


    public static Map allDegradeRules(){
        HashMap degradeMap = new HashMap();
        degradeMap.put("open",DEGRADE_OPEN);
        degradeMap.put("grade",DEGRADE_GRADE);
        degradeMap.put("ratio",DEGRADE_RATIO);
        degradeMap.put("timewindow",DEGRADE_TIMEWINDOW);
        return degradeMap;
    }
}
