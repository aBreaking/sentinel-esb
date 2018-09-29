package com.sitech.esb;

import com.alibaba.csp.sentinel.node.ClusterNode;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import com.sitech.esb.sentinel.SentinelRuleConfig;
import com.sitech.esb.sentinel.SentinelRuleService;

import javax.servlet.ServletRequest;
import java.util.List;

/**
 * @author liwei_paas
 * 对esb提供动态规则调整的位置
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
                //流控规则暂时没有grade把
                sentinelRuleConfig.updateRule("flow",resource,null,rule.getCount(),null,rule.getInterval());
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
              sentinelRuleConfig.updateRule("degrade",resource,rule.getGrade(),rule.getCount(),rule.getTimeWindow(),rule.getInterval());
            }else{
              //add
              sentinelRuleConfig.configDegradeRule(resource,rule.getGrade(),rule.getCount(),rule.getTimeWindow());
            }
        }
        sentinelRuleConfig.reloadDegradeRule();
    }

    public void recoverDegradeRule(String resource){
        sentinelRuleConfig.recoverDegradeRule(resource);
    }

}
