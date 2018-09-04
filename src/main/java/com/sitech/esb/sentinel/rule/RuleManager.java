package com.sitech.esb.sentinel.rule;

import com.alibaba.csp.sentinel.datasource.DataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.sitech.esb.sentinel.ds.MemRefreshaleDateSource;
import com.sitech.esb.sentinel.parse.ConstParser;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwei on 2018/8/29.
 */
public class RuleManager {

    private List<FlowRule> flowRules;
    private List<DegradeRule> degradeRules;

    private static volatile RuleManager ruleManager;
    private RuleManager(){
        flowRules = new ArrayList<FlowRule>();
        degradeRules = new ArrayList<DegradeRule>();
    }

    public static RuleManager getInstance(){
        if(ruleManager==null){
            synchronized (RuleManager.class){
                if(ruleManager==null){
                    ruleManager = new RuleManager();
                }
            }
        }
        return ruleManager;
    }

    public void addFlowRule(String resource,String origin){
        flowRules = FlowRuleManager.getRules();
        if(flowRules==null){
            flowRules = new ArrayList<FlowRule>();
        }
        FlowRule rule = new FlowRule();
        rule.setResource(resource); //资源名
        rule.setGrade(RuleConst.FLOW_GRADE); //限流阈值类型，此处为qps类型
        rule.setCount(RuleConst.FLOW_QPS);   //限流阈值，表示每秒钟通过n次请求
        rule.setLimitApp(origin);   //对调用端进行控制
        flowRules.add(rule);
        FlowRuleManager.loadRules(flowRules);
    }
    public void addDegradeRule(String resource){
        degradeRules = DegradeRuleManager.getRules();
        if(degradeRules==null){
            degradeRules = new ArrayList<DegradeRule>();
        }
        DegradeRule rule = new DegradeRule();
        rule.setResource(resource);
        rule.setCount(RuleConst.DEGRADE_RATIO);
        rule.setGrade(RuleConst.DEGRADE_GRADE);
        rule.setTimeWindow(RuleConst.DEGRADE_TIMEWINDOW);
        degradeRules.add(rule);
        DegradeRuleManager.loadRules(degradeRules);
    }


    public List<FlowRule> updateFlowRule(double count){
        List<FlowRule> rules = FlowRuleManager.getRules();
        for (FlowRule rule : rules){
            rule.setCount(count);
        }
        DataSource<String,List<FlowRule>> mrds = new MemRefreshaleDateSource<List<FlowRule>>(
                new ConstParser(),rules);
        FlowRuleManager.register2Property(mrds.getProperty());
        return rules;
    }
    public List<DegradeRule> updateDegradeRule(double ratio,int timewindow){
        List<DegradeRule> rules = DegradeRuleManager.getRules();
        for (DegradeRule rule : rules){
            rule.setCount(ratio);
            rule.setTimeWindow(timewindow);
        }
        DataSource<String,List<DegradeRule>> mrds = new MemRefreshaleDateSource<List<DegradeRule>>(
                new ConstParser(),rules);
        DegradeRuleManager.register2Property(mrds.getProperty());
        return rules;
    }

    public static void main(String[] args) {
        RuleManager instance = RuleManager.getInstance();
    }

}
