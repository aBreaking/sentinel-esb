package com.sitech.esb.sentinel;

import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

import java.util.List;

public interface SentinelRuleService {

    void saveOrUpdateFlowRules(List<FlowRule> rules);

}
