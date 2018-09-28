package com.sitech.esb.sentinel.rule;

import com.alibaba.csp.sentinel.datasource.DataSource;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.sitech.esb.sentinel.ds.MemRefreshaleDateSource;
import com.sitech.esb.sentinel.parse.ConstParser;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liwei on 2018/8/29.
 * the CURD operation of rule
 */
public class RuleManager {

    private List<FlowRule> flowRules;
    private List<DegradeRule> degradeRules;

    private static volatile RuleManager ruleManager;
    private RuleManager(){
        flowRules = new ArrayList<FlowRule>();
        degradeRules = new ArrayList<DegradeRule>();
    }

    public static RuleManager getInstance(){
        if(ruleManager==null){
            synchronized (RuleManager.class){
                if(ruleManager==null){
                    ruleManager = new RuleManager();
                }
            }
        }
        return ruleManager;
    }

    public FlowRule addFlowRule(String resource,String origin,double count,int grade){
        if(flowRules.isEmpty()){
            flowRules = FlowRuleManager.getRules().isEmpty()?new ArrayList<FlowRule>():FlowRuleManager.getRules();
        }
        FlowRule rule = new FlowRule();
        rule.setResource(resource); //资源名
        rule.setGrade(grade); //限流阈值类型，此处为qps类型
        rule.setCount(count);   //限流阈值，表示每秒钟通过n次请求
        rule.setLimitApp(origin);   //对调用端进行控制
        flowRules.add(rule);
        ruleManager.loadFlowRules(flowRules);
        return rule;
    }

    public DegradeRule addDegradeRule(String resource,double count,int grade,int timewindow){
        if(degradeRules.isEmpty()){
            degradeRules = DegradeRuleManager.getRules().isEmpty()?new ArrayList<DegradeRule>():DegradeRuleManager.getRules();
        }
        DegradeRule rule = new DegradeRule();
        rule.setResource(resource);
        rule.setCount(count);
        rule.setGrade(grade);
        rule.setTimeWindow(timewindow);
        degradeRules.add(rule);
        ruleManager.loadDegradeRules(degradeRules);
        return rule;
    }


    public FlowRule updateFlowRule(String resource,Double count,Integer interval){
        for (FlowRule rule : flowRules){
            if(resource.equals(rule.getResource())){
                if(null!=count){
                    rule.setCount(count);
                }
                //FIXME 目前是将interval的修改放在SentinelRuleConfig里面的，日后需要考虑将INTERVAL 弄到这里面去
                //不要每次修改
                return rule;
            }
        }
        return null;
    }


    public DegradeRule updateDegradeRule(String resource,Double count,Integer timewindow){
        for (DegradeRule rule : degradeRules){
            if(resource.equals(rule.getResource())){
                if(null!=count){
                    rule.setCount(count);
                }
                if(null!=timewindow){
                    rule.setTimeWindow(timewindow);
                }

                //FIXME 考虑将INTERVAL 弄到这里面去
                return rule;
            }
        }
        return null;
    }


    public List<FlowRule> updateAllFlowRule(Double count){
        List<FlowRule> rules = FlowRuleManager.getRules();
        for (FlowRule rule : rules){
            if(null!=count){
                rule.setCount(count);
            }
        }
        registerFlowRules();
        return rules;
    }

    public List<DegradeRule> updateAllDegradeRule(Double count,Integer timewindow){
        List<DegradeRule> rules = DegradeRuleManager.getRules();
        for (DegradeRule rule : rules){
            if(null!=count){
                rule.setCount(count);
            }
            if(null!=timewindow){
                rule.setTimeWindow(timewindow);
            }
        }
        registerDegradeRules();
        return rules;
    }

    /**
     * 针对register方法和load方法，，register是可以替代load方法的，
     * 但是register方法有锁，效率可能更低，
     * 为此，在初始化加载时，如第一次性将rules加载进来，请使用load方法，
     * 后续的rule的修改时，就尽量使用register方法了。
     * 使用register方法时，不要每次update都register，这样效率会很低下，先全部update完，再一次register
     */
    public void registerFlowRules(){
        DataSource<String,List<FlowRule>> mrds = new MemRefreshaleDateSource<List<FlowRule>>(
                new ConstParser(),this.flowRules);
        FlowRuleManager.register2Property(mrds.getProperty());
    }
    public void registerDegradeRules(){
        DataSource<String,List<DegradeRule>> mrds = new MemRefreshaleDateSource<List<DegradeRule>>(
                new ConstParser(),this.degradeRules);
        DegradeRuleManager.register2Property(mrds.getProperty());
    }

    public void loadFlowRules(List<FlowRule> rules){
        FlowRuleManager.loadRules(rules);
    }
    public void loadDegradeRules(List<DegradeRule> rules){
        DegradeRuleManager.loadRules(rules);
    }



    public List<FlowRule> getFlowRules() {
        return flowRules;
    }

    public void setFlowRules(List<FlowRule> flowRules) {
        this.flowRules = flowRules;
    }

    public List<DegradeRule> getDegradeRules() {
        return degradeRules;
    }

    public void setDegradeRules(List<DegradeRule> degradeRules) {
        this.degradeRules = degradeRules;
    }

    public static void main(String[] args) {
        ArrayList<FlowRule> rules = new ArrayList<FlowRule>();
        FlowRule r1 = new FlowRule();
        r1.setResource("zhangsan");
        r1.setCount(12);

        FlowRule r2 = new FlowRule() ;
        r2.setResource("lisi");
        r2.setCount(20);

        rules.add(r1);
        rules.add(r2);
        FlowRuleManager.loadRules(rules);
        System.out.println(FlowRuleManager.getRules());
        for (FlowRule rule : FlowRuleManager.getRules()){
            if(rule.getResource().equals("zhangsan")){
                FlowRule r3 = new FlowRule() ;
                r3.setResource("zhaoliu");
                r3.setCount(26660);
                rule  = r3;
            }

        }
        System.out.println(FlowRuleManager.getRules());
    }

}
