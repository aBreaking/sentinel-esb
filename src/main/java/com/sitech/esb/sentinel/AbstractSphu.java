package com.sitech.esb.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;


/**
 * author liwei_paas
 * 使用方法：
 * 1、首先，你需要先指定资源resource(包括流控规则的资源flowResource,服务降级规则的资源degradeResource)、调用源origin。可参考{@link com.sitech.esb.EsbSphu}
 * 2、使用如下代码框包围你的代码：
 *      Entry flowEntry = null;
 *      Entry degradeEntry = null;
 *      try {
 *       // 这里的AbstractSphu是你的实现类,下同
 *         flowEntry = AbstractSphu.enterFlowEntry();
 *         degradeEntry = AbstractSphu.enterDegradeEntry();
 *        //你的代码块
 *      } catch (BlockException blockException) {
 *          // 流量超过阈值该怎么办，以及出现降级该怎么版
 *      } catch (Throwable bizException) {
 *          // 统计异常比例，
 *          AbstractSphu.statisticsExceptionRatio();
 *      } finally {
 *          // ensure finally be executed
 *          AbstractSphu.exitEntry();
 *      }
 * 3、启动后，规则的配置需要自己动态指定。参考：
 */
public abstract class AbstractSphu {


    public Entry enterFlowEntry() throws BlockException {
        ContextUtil.enter(getFlowResource(),getOrigin());
        return SphU.entry(getFlowResource());
    }

    public Entry enterDegradeEntry() throws BlockException {
        return SphU.entry(getDegradeResource());
    }

    public void statisticsExceptionRatio(Throwable t){
        if(!BlockException.isBlockException(t)){
            Tracer.trace(t);
        }
    }

    public void exitEntry(Entry ... entries){
        for (Entry entry:entries){
            if(entry!=null){
                entry.exit();
            }
        }
        ContextUtil.exit();
    }


    public abstract String getFlowResource();
    public abstract String getDegradeResource();
    public abstract String getOrigin();
}
