/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.node;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.property.PropertyListener;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;
import com.alibaba.csp.sentinel.slots.statistic.StatisticSlot;

/***
 * QPS statistics interval.
 *
 * @author youji.zj
 * @author jialiang.linjl
 */
public class IntervalProperty {

    public static volatile int INTERVAL = 1;

    //定义了FLOW_INTERVAL，专用于进行流量控制的  时间周期的规则。
    public static volatile int FLOW_INTERVAL = 1;

    public static void init(SentinelProperty<Integer> dataSource) {
        dataSource.addListener(new FlowIntervalPropertyListener());
    }
    private static class FlowIntervalPropertyListener implements PropertyListener<Integer> {

        public void configUpdate(Integer value) {
            if (value == null) {
                value = 1;
            }
            //目前只是想先动态修改FLOW_INTERVAL，下同
            FLOW_INTERVAL = value;
            RecordLog.info("Init flow interval: " + FLOW_INTERVAL);
        }

        public void configLoad(Integer value) {
            if (value == null) {
                value = 1;
            }
            FLOW_INTERVAL = value;
            for (ClusterNode node : ClusterBuilderSlot.getClusterNodeMap().values()) {
                node.reset();
            }
            RecordLog.info("Flow interval change received: " + FLOW_INTERVAL);
        }
    }
}
