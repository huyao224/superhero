package com.next.api.controller;

import com.google.common.collect.Maps;
import com.next.api.config.MPWXConfig;
import com.next.pojo.Movie;
import com.next.pojo.Users;
import com.next.pojo.bo.MPWXUserBO;
import com.next.pojo.bo.WXSessionBO;
import com.next.pojo.vo.UsersVO;
import com.next.service.CarouselService;
import com.next.service.MovieService;
import com.next.service.UserService;
import com.next.utils.HttpClientUtil;
import com.next.utils.JsonUtils;
import com.next.utils.NEXTJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//@RequestMapping("stu")
@Api(value = "微信小程序授权登录",tags = {"微信小程序授权登录"})
public class MPWXController extends BasicController{
    @Autowired
    private UserService userService;

    @ApiOperation(value = "微信登录",notes = "微信登录",httpMethod = "POST")
    @PostMapping("/mpWXLogin/{code}")
    public NEXTJSONResult login(
            @ApiParam(name = "keywords",value = "jscode,授权凭证",required = true)
            @PathVariable String code,
            @RequestBody MPWXUserBO wxUserBO){
        String appId = MPWXConfig.APPID;
        String secret = MPWXConfig.SECRET;

        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String,String> param = Maps.newHashMap();
        param.put("appid",appId);
        param.put("secret",secret);
        param.put("js_code",code);
        param.put("grant_type","authorization_code");

        String wxResult = HttpClientUtil.doGet(url,param);

        WXSessionBO model = JsonUtils.jsonToPojo(wxResult,WXSessionBO.class);
        String openId = model.getOpenid();
        //根据openId决定注册还是登陆
        Users user = userService.queryUserForLoginByMAWX(openId);
        if(user == null){
            user = userService.saveUserMAWX(openId,wxUserBO);
        }

        //设置用户分布式会话，并且return到前端
        return NEXTJSONResult.ok(setResdisUserToken(user));
    }

}
