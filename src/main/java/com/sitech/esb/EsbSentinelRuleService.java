package com.sitech.esb;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.sitech.esb.sentinel.SentinelRuleConfig;
import com.sitech.esb.sentinel.SentinelRuleService;

import javax.servlet.ServletRequest;
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
                /**
                 * FIXME 这里的代码还是需要重构。 第一次config时，就应该把interval加上了，下同
                 */
                sentinelRuleConfig.configFlowRule(resource, origin,rule.getCount(),rule.getGrade());
            }
        }
        sentinelRuleConfig.reloadFlowRules();
    }


    public void saveOrUpdateDegradeRules(List<DegradeRule> rules) {
        for(DegradeRule rule : rules){
            String resource = rule.getResource();
            if(sentinelRuleConfig.containsDegradeResources(resource)){
              //update
              sentinelRuleConfig.updateRule("degrade",resource,rule.getCount(),rule.getTimeWindow(),rule.getInterval());
            }else{
              //add
              sentinelRuleConfig.configDegradeRule(resource,rule.getCount(),rule.getGrade(),rule.getTimeWindow());
            }
        }
        sentinelRuleConfig.reloadDegradeRule();
    }

}
