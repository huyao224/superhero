package com.next.api.controller;

import com.next.pojo.Users;
import com.next.pojo.bo.ModifiedUserBO;
import com.next.pojo.bo.RegistLoginUsersBO;
import com.next.pojo.vo.UsersVO;
import com.next.service.UserService;
import com.next.utils.DateUtil;
import com.next.utils.MD5Utils;
import com.next.utils.NEXTJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("user")
@Api(value = "用户",tags = {"用户相关接口"})
public class UsersController extends BasicController{
    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册登录",notes = "用户注册登录",httpMethod = "POST")
    @PostMapping("/registOrLogin")
    public NEXTJSONResult registOrLogin (
            @ApiParam(name = "userId",value = "用户ID",required = true)
            @RequestBody RegistLoginUsersBO userBO) throws Exception {
        //判断用户名和密码不能为空
        if(StringUtils.isBlank(userBO.getPassword()) || StringUtils.isBlank(userBO.getUsername())){
            return NEXTJSONResult.errorMsg("用户名或密码不能为空");
        }

        //1.根据用户名判断用户是否存在
        boolean usernameIsExist =  userService.queryUsernameIsExist(userBO.getUsername());
        Users userResult = null;
        if(usernameIsExist){
            //1.1 用户登录
            userResult = userService.queryUserForLogin(userBO.getUsername(), MD5Utils.getMD5Str(userBO.getPassword()));
        }else {
            //1.2 用户注册
            userResult = userService.saveUser(userBO);
        }

        //设置用户分布式会话，并且return到前端
        return NEXTJSONResult.ok(setResdisUserToken(userResult));
    }

    @ApiOperation(value = "用户退出",notes = "用户退出",httpMethod = "POST")
    @PostMapping("/logout")
    public NEXTJSONResult logout(
            @ApiParam(name = "userId",value = "用户ID",required = true)
            @RequestParam String userId){
        redis.del(REDIS_USER_TOKEN+":"+userId);
        return NEXTJSONResult.ok();
    }

    @ApiOperation(value = "用户头像上传",notes = "用户头像上传",httpMethod = "POST")
    @PostMapping(value = "/uploadFace" ,headers = "content-type=multipart/form-data")
    public NEXTJSONResult uploadFace(
            @ApiParam(name = "userId",value = "用户ID",required = true)
            @RequestParam String userId,
            @ApiParam(name = "multipartFile",value = "用户头像",required = true)
            @RequestParam MultipartFile file){
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        //获取文件保存的命名空间
        String fileSpace = faceConfig.getFaceFileSpace();
        //定义前缀，用于保存到数据库中，进行拼接的路径
        String uploadPathPrefix = "/"+userId;
        try {
            if(file != null){
                //获取文件上传时的文件名
                String fileName = file.getOriginalFilename();
                if(StringUtils.isNoneBlank(fileName)){
                    //重新组装文件名
                    String filenameArr[] = fileName.split("\\.");
                    String suffixe = filenameArr[filenameArr.length-1];
                    String newFileName = "face-" + userId + "." + suffixe;

                    //组装文件保存路径
                    uploadPathPrefix += ("/" + newFileName);
                    String finalFilePath = fileSpace + uploadPathPrefix;
                    File outFace = new File(finalFilePath);
                    if(outFace.getParentFile()!= null || !outFace.getParentFile().isDirectory()){
                        outFace.getParentFile().mkdir();
                    }

                    //拷贝文件到输出文件对象
                    fileOutputStream = new FileOutputStream(outFace);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(fileOutputStream != null){
                    fileOutputStream.flush();;
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //将用户新头像保存到数据库
        String newUserFaceUrl = faceConfig.getUrl() + uploadPathPrefix;
        //为了保证前端永远最新，并且是需要刷新的，加上时间戳
        newUserFaceUrl += ("?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN));

        //保存地址到数据库
        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(newUserFaceUrl);
        user = userService.updateUserInfo(user);
        user.setPassword(null);

        return NEXTJSONResult.ok(conventusersVO(user));
    }

    @ApiOperation(value = "修改昵称",notes = "修改昵称",httpMethod = "POST")
    @PostMapping("/modifyUserinfo")
    public NEXTJSONResult modifyUserinfo(
            @RequestBody ModifiedUserBO userBO){
        String userId = userBO.getUserId();
        if(StringUtils.isBlank(userId)){
            return NEXTJSONResult.errorMsg("用户id不能为空");
        }
        //以下三个属性至少有一项不为空
        String birthday = userBO.getBirthday();
        Integer sex = userBO.getSex();
        String  nickName = userBO.getNickname();
        if(StringUtils.isBlank(birthday) && null == sex && StringUtils.isBlank(nickName)){
            return NEXTJSONResult.errorMsg("修改用户信息不能为空");
        }
        if(sex != null && sex !=0 && sex != 1){
            return NEXTJSONResult.errorMsg("性别不正确 - [1:男][2:女]");
        }
        if(StringUtils.isNotBlank(nickName) && nickName.length() > 12){
            return NEXTJSONResult.errorMsg("昵称长度不能超过12位");
        }
        if(StringUtils.isNotBlank(birthday)){
            if(!DateUtil.isValidDate(birthday,DateUtil.ISO_EXPANDED_DATE_FORMAT)){
                return NEXTJSONResult.errorMsg("生日日期格式不正确");
            }
        }
        //保存到数据库
        Users user = new Users();
        user.setId(userId);
        user.setNickname(nickName);
        user.setSex(sex);
        user.setBirthday(birthday);
        user = userService.updateUserInfo(user);
        user.setPassword(null);

        return NEXTJSONResult.ok(conventusersVO(user));
    }

    private UsersVO conventusersVO(Users user){
        String token = redis.get(REDIS_USER_TOKEN+":"+user.getId());
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserUniqueToken(token);
        return usersVO;
    }
}
