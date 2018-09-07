package com.sitech.esb.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;

public class BaseSentinel {

    private SentinelRuleConfig sentinelRuleConfig = new SentinelRuleConfig();

    public Entry entryFlowRule(String flowResource, String origin) throws BlockException {
        if(sentinelRuleConfig.isFlowRuleOpen()){
            //可能还得改变下策略，当第一次调用时，就不进入entry了，返回enrty = null，即可
            if(!sentinelRuleConfig.containsFlowResource(flowResource)){
                sentinelRuleConfig.configFlowRule(flowResource,origin);
            }
            ContextUtil.enter(flowResource, origin);
            return SphU.entry(flowResource);
        }
        return null;
    }

    public Entry entryDegradeRule(String degradeResource) throws BlockException {
        if(sentinelRuleConfig.isDegradeRuleOpen()){
            if(!sentinelRuleConfig.containsDegradeResources(degradeResource)){
                sentinelRuleConfig.configDegradeRule(degradeResource);
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
}
