package com.sitech.esb.sentinel.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwei on 2018/8/29.
 * saved all flowResources and degradeResources,in accord with FlowRuleManager or DegradeRuleManager
 * for the esb, the flowResource comprises host and server name
 * the degradeResource always comprises server name
 */
public class RuleResources {
    private List<String> flowResources ;
    private List<String> degradeResources ;

    /**
     * TODO 担心一个问题，当RuleManager中的规则过多时，会不会有影响呢
     */

    private static volatile RuleResources ruleResources;
    private RuleResources(){
        flowResources = new ArrayList<String>();
        degradeResources = new ArrayList<String>();
    }

    public static RuleResources getInstance(){
        if(ruleResources==null){
            synchronized (RuleResources.class){
                if(ruleResources==null){
                    ruleResources = new RuleResources();
                }
            }
        }
        return ruleResources;
    }


    public boolean containsFlowResource(String resource){
        return flowResources.contains(resource);
    }

    public boolean containsDegradeResources(String resource){
        return degradeResources.contains(resource);
    }

    public void addFlowResource(String flowResource){
        flowResources.add(flowResource);
    }
    public void addDegradeResource(String degradeResource){
        degradeResources.add(degradeResource);
    }

    public void resetFlowResource(){
        this.flowResources = new ArrayList<String>();
    }


    public List<String> getFlowResources() {
        return flowResources;
    }

    public void setFlowResources(List<String> flowResources) {
        this.flowResources = flowResources;
    }

    public List<String> getDegradeResources() {
        return degradeResources;
    }

    public void setDegradeResources(List<String> degradeResources) {
        this.degradeResources = degradeResources;
    }
}
