package com.sitech.esb.sentinel.parse;

import com.alibaba.csp.sentinel.datasource.ConfigParser;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;

import java.util.List;

/**
 * Created by liwei on 2018/8/29.
 */
public class ConstParser<T> implements ConfigParser<String, List<T>> {

    public List<T> parse(String source) {
        return null;
    }
}
