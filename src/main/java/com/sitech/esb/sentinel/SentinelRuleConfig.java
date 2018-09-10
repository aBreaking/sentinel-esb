package com.sitech.esb.sentinel;

import com.alibaba.csp.sentinel.node.IntervalProperty;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.sitech.esb.sentinel.rule.RuleConst;
import com.sitech.esb.sentinel.rule.RuleManager;
import com.sitech.esb.sentinel.rule.RuleResources;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liwei on 2018/8/27.
 */
public class SentinelRuleConfig {
    final RuleResources ruleResources = RuleResources.getInstance();
    final RuleManager ruleManager = RuleManager.getInstance();

    public synchronized void configFlowRule(String flowResource,String origin) {
        if (!containsFlowResource(flowResource)) {
            ruleResources.addFlowResource(flowResource);
            ruleManager.addFlowRule(flowResource, origin);
        }
    }

    public synchronized void configDegradeRule(String degradeResource){
        if(!containsDegradeResources(degradeResource)){
            ruleResources.addDegradeResource(degradeResource);
            ruleManager.addDegradeRule(degradeResource);
        }
    }

    public boolean containsFlowResource(String resource){
            return ruleResources.containsFlowResource(resource);
    }

    public boolean containsDegradeResources(String resource){
        return ruleResources.containsDegradeResources(resource);
    }

    public List updateRule(String rule,double count,int timewindow,int interval){
        RuleConst.FLOW_INTERVAL = interval;
        updateFlowInterval(RuleConst.FLOW_INTERVAL);
        return updateRule(rule,count,timewindow);
    }

    public List updateRule(String rule,double count,int timewindow){
        if("flow".equals(rule)){
            ruleManager.updateFlowRule(count);
            RuleConst.FLOW_QPS = count;
            return FlowRuleManager.getRules();
        }
        if("degrade".equals(rule)){
            ruleManager.updateDegradeRule(count,timewindow);
            RuleConst.DEGRADE_RATIO = count;
            RuleConst.DEGRADE_TIMEWINDOW = timewindow;
            return DegradeRuleManager.getRules();
        }
        return null;
    }

    public void switchRule(String rule,String openOrClose){
        Assert.objInRange(openOrClose, "open", "close");
        if("flow".equals(rule)){
            RuleConst.FLOW_OPEN  = "open".equals(openOrClose);
        }
        if("degrade".equals(rule)){
            RuleConst.DEGRADE_OPEN = "open".equals(openOrClose);
        }
    }

    public void updateFlowInterval(int interval){
        DynamicSentinelProperty<Integer> sentinelProperty = new DynamicSentinelProperty<Integer>(interval);
        IntervalProperty.init(sentinelProperty);
    }

    public List getRules(String rule){
        if("flow".equals(rule)){
            return FlowRuleManager.getRules();
        }
        if("degrade".equals(rule)){
            return DegradeRuleManager.getRules();
        }
        return null;
    }

    public boolean isFlowRuleOpen(){
        return RuleConst.FLOW_OPEN;
    }

    public boolean isDegradeRuleOpen(){
        return  RuleConst.DEGRADE_OPEN;
    }


}
