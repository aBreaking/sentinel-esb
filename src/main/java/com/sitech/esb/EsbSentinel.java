package com.sitech.esb;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.sitech.esb.sentinel.BaseSentinel;

import javax.servlet.http.HttpServletRequest;

public class EsbSentinel extends BaseSentinel {
    private String flowResource;
    private String degradeResource;
    private String origin;

    public Entry entryFlowRule() throws BlockException {
        return super.entryFlowRule(flowResource,origin);
    }
    public Entry entryDegradeRule() throws BlockException{
        return super.entryDegradeRule(degradeResource);
    }


    public void parseRequest(HttpServletRequest request){
        String host = request.getRemoteHost();
        String uri = request.getRequestURI();
        this.flowResource = uri+host;
        this.origin = host;
        this.degradeResource = uri;
    }


    public String getFlowResource() {
        return flowResource;
    }

    public void setFlowResource(String flowResource) {
        this.flowResource = flowResource;
    }

    public String getDegradeResource() {
        return degradeResource;
    }

    public void setDegradeResource(String degradeResource) {
        this.degradeResource = degradeResource;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
