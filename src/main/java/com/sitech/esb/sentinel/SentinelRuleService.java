package com.sitech.esb.sentinel;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

import java.util.List;

/**
 * @author liwei_paas
 * 对外i提供sentinel动态规则的接口
 */
public interface SentinelRuleService {

    /**
     * 对传过来的规则列表进行添加或者修改，如果某个rule已经存在，那么重加载一下里面所有的规则；如果没有这个rule,就加上。
     * 这里判断是否包含rules 里面的某个rule，是跟内存中已经存在的所有的rule进行对比（通过FlowRuleManager.getRules获取到）
     * @param rules
     */
    void saveOrUpdateFlowRules(List<FlowRule> rules);
    void saveOrUpdateDegradeRules(List<DegradeRule> rules);

    void recoverDegradeRule(String resource);
}
