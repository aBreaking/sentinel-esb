package com.sitech.esb;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.sitech.esb.sentinel.SentinelRuleConfig;
import com.sitech.esb.sentinel.SentinelRuleService;
import java.util.List;

/**
 * author liwei_paas
 * 对当前的sentinel里的规则进行各种操作，
 */
public class EsbSentinelRuleService implements SentinelRuleService {

    SentinelRuleConfig sentinelRuleConfig = new SentinelRuleConfig();

    /**
     * 添加或者修改规则List
     * @param rules
     */
    public void saveOrUpdateFlowRules(List<FlowRule> rules){
        for(FlowRule rule : rules){
            String resource = rule.getResource();
            if(sentinelRuleConfig.containsFlowResource(resource)){
                //update
                sentinelRuleConfig.updateRule("flow",resource,rule.getCount(),null,rule.getInterval());
            }else{
                //add
                String[] split = resource.split(":");
                String origin = split[0];
                //add
                sentinelRuleConfig.configFlowRule(resource, origin,rule.getCount(),rule.getGrade());
            }
        }
        sentinelRuleConfig.reloadFlowRules();
    }

}
