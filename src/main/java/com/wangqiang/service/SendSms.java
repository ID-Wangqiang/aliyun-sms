package com.wangqiang.service;

import java.util.Map;

/**
 * @version : V1.0
 * @ClassName: SendSms
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/5/17 23:28
 */
public interface SendSms {
    public boolean send (String phoneNum, Map<String,Object> code);
}
