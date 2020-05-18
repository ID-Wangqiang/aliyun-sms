package com.wangqiang.controller;

import com.wangqiang.service.SendSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @version : V1.0
 * @ClassName: SmsApiController
 * @Description: TODO
 * @Auther: wangqiang
 * @Date: 2020/5/17 23:35
 */
@RestController
@CrossOrigin //处理跨域问题
public class SmsApiController {

    @Autowired
    private SendSms sendSms;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @GetMapping("/send/{phone}")
    public String code(@PathVariable("phone")String phone){

        String code = redisTemplate.opsForValue().get(phone);
        //判断redis缓存中是否有手机号验证码的记录
        if (!StringUtils.isEmpty(code)){
            return phone + ":" + code + "已存在，还没有过期";
        }

        //生成纯6位数字验证码并存储
        code = String.valueOf((Math.random() * 9 + 1) * 1000000).toString().substring(0, 6);
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",code);
        boolean isSend = sendSms.send(phone, map);

        if (isSend){
            redisTemplate.opsForValue().set(phone,code, 5,TimeUnit.MINUTES);
            return phone + ":" + code + "发送成功！";
        }else {
            return "发送失败！";
        }

    }
}
