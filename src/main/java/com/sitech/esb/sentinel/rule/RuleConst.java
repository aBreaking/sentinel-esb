package com.sitech.esb.sentinel.rule;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;

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


}
