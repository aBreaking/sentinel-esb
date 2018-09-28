package com.sitech.esb.sentinel;
import com.alibaba.csp.sentinel.node.IntervalProperty;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
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

    public synchronized void configFlowRule(String flowResource,String origin,double count,int grade) {
        if (!containsFlowResource(flowResource)) {

            ruleResources.addFlowResource(flowResource);
            ruleManager.addFlowRule(flowResource, origin,count,grade);
        }
    }

    public synchronized void configDegradeRule(String degradeResource,double count,int grade,int timewindow){
        if(!containsDegradeResources(degradeResource)){

            ruleResources.addDegradeResource(degradeResource);
            ruleManager.addDegradeRule(degradeResource,count,grade,timewindow);
        }
    }

    public void loadFlowRule(){
        FlowRuleManager.loadRules(ruleManager.getFlowRules());
    }

    public void loadDegradeRule(){
        DegradeRuleManager.loadRules(ruleManager.getDegradeRules());
    }


    public boolean containsFlowResource(String resource){
        return ruleResources.containsFlowResource(resource);
    }

    public boolean containsDegradeResources(String resource){
        return ruleResources.containsDegradeResources(resource);
    }

    public Rule updateRule(String rule,String resource,Double count,Integer timewindow,Integer interval){
        if("flow".equals(rule)){
            //FIXME 目前暂做到 将Interval放在外面，从而动态实现，先考虑如何放在FlowRule里面去
            updateFlowInterval(resource,interval);
            ruleManager.updateFlowRule(resource,count,interval);
        }
        if("degrade".equals(rule)){
            updateFlowInterval(resource,interval);
            return ruleManager.updateDegradeRule(resource,count,timewindow);
        }
        return  null;
    }

    public void reloadFlowRules(){
       ruleManager.registerFlowRules();
    }

    public void reloadDegradeRule(){
        ruleManager.registerDegradeRules();
    }


    public void addFlowRulesFirst(List<FlowRule> rules) throws IllegalAccessException {
        if(ruleResources.getFlowResources().isEmpty()){
            ArrayList<String> resources = new ArrayList<String>();
            for(FlowRule flowRule : rules){
                resources.add(flowRule.getResource());
            }
            ruleResources.setFlowResources(resources);
            ruleManager.setFlowRules(rules);
            ruleManager.loadFlowRules(rules);
        }else{
            throw new IllegalAccessException("ruleResources 包含有规则了,请第一次初始化规则时使用");
        }

    }



    public List updateAllRule(String rule,Double count,Integer timewindow,Integer interval){
        if(interval!=null){
            RuleConst.FLOW_INTERVAL = interval;
            updateFlowInterval(rule,RuleConst.FLOW_INTERVAL);
        }
        return updateAllRule(rule,count,timewindow);
    }

    public List updateAllRule(String rule,Double count,Integer timewindow){
        if("flow".equals(rule)){
            RuleConst.FLOW_QPS = count;
            List<FlowRule> rules = ruleManager.updateAllFlowRule(count);
            return rules;
        }
        if("degrade".equals(rule)){
            RuleConst.DEGRADE_RATIO = count;
            RuleConst.DEGRADE_TIMEWINDOW = timewindow;

            List<DegradeRule> rules = ruleManager.updateAllDegradeRule(count, timewindow);
            return rules;
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

    public void updateFlowInterval(String resource,Integer interval){
        if(null!=interval&&null!=resource){
            IntervalProperty.RESOURCE_INTERVAL.put(resource,interval);
            DynamicSentinelProperty<Integer> sentinelProperty = new DynamicSentinelProperty<Integer>(interval);
            IntervalProperty.init(sentinelProperty);
        }
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

    public List<String> getAllResources(String rule){
        return "flow".equals(rule)?ruleResources.getFlowResources():ruleResources.getDegradeResources();
    }


}
