package com.sitech.esb.sentinel;
import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.node.IntervalProperty;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
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

    public synchronized void configDegradeRule(String degradeResource,int grade,double count,int timewindow){
        if(!containsDegradeResources(degradeResource)){

            ruleResources.addDegradeResource(degradeResource);
            ruleManager.addDegradeRule(degradeResource,count,grade,timewindow);
        }
    }

    public boolean containsFlowResource(String resource){
        return ruleResources.containsFlowResource(resource);
    }

    public boolean containsDegradeResources(String resource){
        return ruleResources.containsDegradeResources(resource);
    }

    public Rule updateRule(String rule,String resource,Integer grade,Double count,Integer timewindow,Integer interval){
        if("flow".equals(rule)){
            //FIXME 目前暂做到 将Interval放在外面，从而动态实现，先考虑如何放在FlowRule里面去
            updateFlowInterval(resource,interval);
            ruleManager.updateFlowRule(resource,count,interval);
        }
        if("degrade".equals(rule)){
            updateFlowInterval(resource,interval);
            return ruleManager.updateDegradeRule(resource,count,timewindow,grade,null);
        }
        return  null;
    }

    public void reloadFlowRules(){
       ruleManager.registerFlowRules();
    }

    public void reloadDegradeRule(){
        ruleManager.registerDegradeRules();
    }

    /**
     * 恢复 某个规则
     * 这里还有点复杂，不但需要将degradeRule的cut置为false，
     * 还需要将统计重置，还得需要将degradeRule的resetTask给停掉
     * @param resource
     */
    public void recoverDegradeRule(String resource){
        ClusterNode clusterNode = ClusterBuilderSlot.getClusterNode(resource);
        clusterNode.resetDegradeStatistic();
        DegradeRule.stopResetTask();
        ruleManager.updateDegradeRule(resource,null,null,null,false);
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


    public List<String> getAllResources(String rule){
        return "flow".equals(rule)?ruleResources.getFlowResources():ruleResources.getDegradeResources();
    }


}
