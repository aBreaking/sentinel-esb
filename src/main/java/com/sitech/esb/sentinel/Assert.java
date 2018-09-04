package com.sitech.esb.sentinel;

public class Assert {
    public static void objInRange(Object value,Object...ranges){
        Object ret = "";
        for (Object r :ranges){
            if(r.equals(value)){
                return;
            }
            ret+=r+",";
        }
        throw new IllegalArgumentException(value.toString()+""+"必须在:"+ret.toString()+"之中");
    }

    public static void CannotBeNull(Object obj,String tips){
        if(obj == null){
            throw new IllegalArgumentException(obj+"不能为空，"+tips);
        }
    }
}
