package com.sitech.esb.sentinel.ds;

import com.alibaba.csp.sentinel.datasource.AutoRefreshDataSource;
import com.alibaba.csp.sentinel.datasource.ConfigParser;

/**
 * Created by liwei on 2018/8/29.
 */
public class MemRefreshaleDateSource<T> extends AutoRefreshDataSource<String, T> {
    public MemRefreshaleDateSource(ConfigParser<String, T> configParser, T rules) {
        super(configParser,Integer.MAX_VALUE);
        refreshRules(rules);
    }

    public MemRefreshaleDateSource(ConfigParser<String, T> configParser) {
        super(configParser);
    }

    public MemRefreshaleDateSource(ConfigParser<String, T> configParser, long recommendRefreshMs) {
        super(configParser, recommendRefreshMs);
    }

    public String readSource() throws Exception {
        return null;
    }

    public void refreshRules(T rules){
        T newValue = rules;
        getProperty().updateValue(newValue);
    }
}
