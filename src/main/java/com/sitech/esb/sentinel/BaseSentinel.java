package com.sitech.esb.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.sitech.esb.sentinel.rule.RuleConst;

public class BaseSentinel {
    private String flowResource;    //流量控制，调用源origin+服务名srvName
    private String degradeResource; //服务熔断降级策略，一般就是服务名

    protected SentinelRuleConfig sentinelRuleConfig = new SentinelRuleConfig();

    public Entry entryFlowRule(String flowResource, String origin) throws BlockException {
        if(sentinelRuleConfig.isFlowRuleOpen()){
            //可能还得改变下策略，当第一次调用时，就不进入entry了，返回enrty = null，即可
            if(!sentinelRuleConfig.containsFlowResource(flowResource)){
                sentinelRuleConfig.configFlowRule(flowResource,origin,RuleConst.FLOW_QPS,RuleConst.FLOW_GRADE);
               /* sentinelRuleConfig.loadFlowRule();*/
            }
            ContextUtil.enter(flowResource, origin);
            return SphU.entry(flowResource);
        }
        return null;
    }

    public Entry entryDegradeRule(String degradeResource) throws BlockException {
        if(sentinelRuleConfig.isDegradeRuleOpen()){
            if(!sentinelRuleConfig.containsDegradeResources(degradeResource)){
                sentinelRuleConfig.configDegradeRule(degradeResource,RuleConst.DEGRADE_RATIO,RuleConst.DEGRADE_GRADE,RuleConst.DEGRADE_TIMEWINDOW);
                sentinelRuleConfig.loadDegradeRule();
            }
            return SphU.entry(degradeResource);
        }
        return null;
    }

    public void statisticsExceptionRatio(Throwable t){
        if(!BlockException.isBlockException(t)){
            Tracer.trace(t);
        }
    }
    public void exitEntry(Entry ... entries){
        for (Entry entry:entries){
            if(entry!=null){
                entry.exit();
            }
        }
        ContextUtil.exit();
    }

    public String getFlowResource() {
        return flowResource;
    }

    public void setFlowResource(String flowResource) {
        this.flowResource = flowResource;
    }

    public String getDegradeResource() {
        return degradeResource;
    }

    public void setDegradeResource(String degradeResource) {
        this.degradeResource = degradeResource;
    }
}
