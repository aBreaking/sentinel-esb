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
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.property.SimplePropertyListener;
import com.alibaba.csp.sentinel.slots.clusterbuilder.ClusterBuilderSlot;

import java.util.HashMap;
import java.util.Map;

/***
 * QPS statistics interval.
 *
 * @author youji.zj
 * @author jialiang.linjl
 * @author CarpenterLee
 */
public class IntervalProperty {

    /**
     * <p>
     * Interval in seconds. This variable determines sensitivity of the QPS calculation.
     * </p>
     * DO NOT MODIFY this value directly, use {@link #updateInterval(int)}, otherwise the modification will not
     * take effect.
     */
    public static volatile int INTERVAL = 1;


    //控制全局的flow Interval
    public static volatile int FLOW_INTERVAL = 1;

    //控制全局的degrade Interval
    public static volatile int DEGRADE_INTERVAL = 10;

    public static boolean RESET = false;
    public static Map<String,Integer> RESOURCE_INTERVAL = new HashMap<String, Integer>();

    public static void init(SentinelProperty<Integer> property) {
        property.addListener(new SimplePropertyListener<Integer>() {

            public void configUpdate(Integer value) {
                if (value != null) {
                    updateInterval(value);
                }
            }
        });
    }

    /**
     * Update the {@link #INTERVAL}, All {@link ClusterNode}s will be reset if newInterval is
     * different from {@link #INTERVAL}
     *
     * 目前只是先针对流量控制的Interval 进行控制，熔断功能有待确认
     *
     * @param newInterval New interval to set.
     */
    public static void updateInterval(int newInterval) {
        if (newInterval != FLOW_INTERVAL) {
            FLOW_INTERVAL = newInterval;
            RESET = true;
            ClusterBuilderSlot.resetClusterNodes();
        }
        RecordLog.info("Flow INTERVAL updated to: " + FLOW_INTERVAL);
    }


}
