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
 * 调式展示功能
 */
public class SentinelServlet extends HttpServlet {

    static SentinelRuleConfig sentinelRuleConfig = new SentinelRuleConfig();
    private String operate  = "";
    private String rule = "";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        parseRequest(request);
        //进行查询的操作
        if("show".equals(operate)||"get".equals(operate)){
            List rules = sentinelRuleConfig.getRules(rule);
            response.getWriter().println(rules);
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

}

