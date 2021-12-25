package com.wangqiang.controller;

import com.wangqiang.service.SendSms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${my.sms.randomCodeNumber}")
    private String randomCodeNumber;

    @Value("${my.sms.expiryTime}")
    private String expiryTime;

    @Value("${my.sms.sendOneExistTime}")
    private String sendOneExistTime;

    @Value("${my.sms.maxUseCount}")
    private String maxUseCount;

    @Autowired
    private SendSms sendSms;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @GetMapping("/send/{phone}")
    public String code(@PathVariable("phone")String phone){

        String code = redisTemplate.opsForValue().get(phone);
        //判断redis缓存中是否有手机号验证码的记录
        if (!StringUtils.isEmpty(code)){
            return phone + ":" + code + "已存在，还没有过期,2分钟过期";
        }

        //生成纯6位数字验证码并存储redis
        code = String.valueOf((Math.random() * 9 + 1) * 1000000).toString().substring(0, Integer.valueOf(randomCodeNumber));

        // 判断该手机是否60秒内发送过验证码
        String smsPhone = "EXIST" + phone;
        String existPhone = redisTemplate.opsForValue().get(smsPhone);
        if (!StringUtils.isEmpty(existPhone)) {
            return phone + ":" + "每个手机号60秒内只能发送1次验证:";
        }


        // 阿里云短信发送服务
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",code);
        boolean isSend = sendSms.send(phone, map);

        if (isSend){
            // 验证码有效期分钟
            redisTemplate.opsForValue().set(phone,code, Long.valueOf(expiryTime),TimeUnit.MINUTES);
            // 每个手机号60秒只能发送1次验证码
            redisTemplate.opsForValue().set(smsPhone,null,Long.valueOf(sendOneExistTime),TimeUnit.SECONDS);
            // 每个验证码只能使用3次
            String phoneCount = "COUNT" + phone;
            redisTemplate.opsForValue().set(phoneCount,maxUseCount,2,TimeUnit.MINUTES);
            return phone + ":" + code + "发送成功！";
        }else {
            return "发送失败！";
        }

    }

    @GetMapping("/vaile/{phone}/{code}")
    public String vaile(@PathVariable("phone")String phone,@PathVariable("code")String code){
        String phoneCode = redisTemplate.opsForValue().get(phone);
        //判断redis缓存中是否有手机号验证码的记录
        if (!StringUtils.isEmpty(phoneCode)){
            return phone + ":" + phoneCode + "手机号验证码已过期，请重新发送验证码";
        }
        // 判断验证码是否正确
        if (!code.equals(phoneCode)) {
            return phone+ ":" +phoneCode + "输入验证码有误，请重新输入";
        }else {
            // 验证码可验证次数减少
            String phoneCount = "COUNT" + phone;
            String s = redisTemplate.opsForValue().get(phoneCount);
            int count = Integer.valueOf(s);
            if (count > 0) {
                count--;
                redisTemplate.opsForValue().set(phoneCount,String.valueOf(count),2,TimeUnit.MINUTES);
                return phone+ ":" +phoneCode + "验证次数还有" +count+ "次";
            }else {
                return phone+ ":" +phoneCode + "验证成功";
            }
        }
    }
}
