package com.sitech.esb.sentinel.cmd.servlet;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.sitech.esb.sentinel.SentinelRuleConfig;
import com.sitech.esb.sentinel.rule.RuleConst;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by liwei on 2018/8/31.
 * 开放的功能：
 * 开关、open/rule
 * 修改、update/rule?count=
 * 查询、show(get)/rule
 */
public class SentinelServlet extends HttpServlet {

    static SentinelRuleConfig sentinelRuleConfig = new SentinelRuleConfig();
    private String operate  = "";
    private String rule = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        parseRequest(request);
        Map<String, String[]> parameterMap = request.getParameterMap();
        if(parameterMap.isEmpty()){
            //目前仅用于开关的操作，仅限制于post请求
            //sentinel/open/flow
            sentinelRuleConfig.switchRule(rule,operate);
            //TODO 修改成功的提示操作有待  友好优化
            response.getWriter().println("update success");
        }else{
            //进行规则修改等操作
            //sentinel/update/flow?count=XX
            String countStr = request.getParameter("count");
            String twStr = request.getParameter("timewindow");
            double count = StringUtil.isNotBlank(countStr)?Double.parseDouble(countStr):"flow".equals(rule)?RuleConst.FLOW_QPS:RuleConst.DEGRADE_GRADE;
            int timewindow = StringUtil.isNotBlank(twStr)?Integer.parseInt(twStr):RuleConst.DEGRADE_TIMEWINDOW;
            List list = sentinelRuleConfig.updateRule(rule, count, timewindow);
            response.getWriter().println(list);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        parseRequest(request);
        //进行查询的操作
        if("show".equals(operate)||"get".equals(operate)){
            List rules = sentinelRuleConfig.getRules(rule);
            response.getWriter().println(rules);
        }else if("all".equals(operate)){
            Map map = "flow".equals(rule)?RuleConst.allFlowRules():RuleConst.allDegradeRules();
            response.getWriter().println(map.toString().replaceAll("=", ":"));
        }

    }

    private void parseRequest(HttpServletRequest request){
        String uri = request.getRequestURI();
        String sentinelUri = uri.substring(uri.indexOf("sentinel"));
        //TODO 对uri还得进行校验
        String[] ops = sentinelUri.split("/");
        operate = ops[1];
        rule = ops[2];
    }

    public static void main(String args[]){
        Map map = RuleConst.allFlowRules();
        String s = map.toString().replaceAll("=", ":");
        System.out.println(s);
    }
}
