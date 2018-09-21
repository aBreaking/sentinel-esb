package com.sitech.esb;
import com.sitech.esb.sentinel.AbstractSphu;
import javax.servlet.http.HttpServletRequest;

public class EsbSphu extends AbstractSphu {

    private String srvName; //服务名
    private String clientIp;    //调用端ip

    public EsbSphu(){}

    public EsbSphu(String srvName,String clientIp){
        this.srvName = srvName;
        this.clientIp = clientIp;
    }

    public String getFlowResource() {
        String flowResource = clientIp + ":" + srvName;
        return flowResource;
    }

    public String getDegradeResource() {
        String degradeResouece = srvName;
        return degradeResouece;
    }

    public String getOrigin() {
        return clientIp;
    }

    /**
     * 这里做了一个模板的操作，解析esb  request成srvName及clientIp
     * @param request
     */
    public void parseEsbRequest(HttpServletRequest request){
        String host = request.getRemoteHost();
        String uri = request.getRequestURI();
        this.srvName = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
        this.clientIp = host;
    }

    public String getSrvName() {
        return srvName;
    }

    public void setSrvName(String srvName) {
        this.srvName = srvName;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
}
