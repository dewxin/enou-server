package fun.enou.alpha.controller;

import fun.enou.alpha.dto.dtoweb.DtoWebUser;
import fun.enou.alpha.dto.dtoweb.DtoWebUserThirdInfo;
import fun.enou.alpha.msg.MsgEnum;
import fun.enou.alpha.service.UserService;
import fun.enou.core.msg.AutoWrapMsg;
import fun.enou.core.msg.EnouMessageException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping
@AutoWrapMsg
public class UserController {

	@Autowired
    UserService userService;
	
    /**
     * 用户登录获取token
     * @param user 账号密码
     * @return 返回封装后的token
     * @throws EnouMessageException
     */
	@PostMapping("/login")
	public ResponseEntity<String> getToken(@RequestBody @Valid DtoWebUser user) throws EnouMessageException {
		DtoWebUser webUser = userService.findByAccountAndPassword(user);
		if (webUser == null) {
			log.warn("account or pwd is wrong, user:{}", user.getAccount());
			MsgEnum.ACCOUNT_OR_PWD_WRONG.ThrowException();
		}

		String result = userService.loginGetToken(webUser);
		return ResponseEntity.ok(result);
	}
	
	/**
	 * 用户登出，清理token
	 */
	@PostMapping("/logout")
	public void logout(){
		
		userService.logout();
	}
	
	/**
	 * 用户注册
	 * @param user 账号密码
	 * @throws EnouMessageException
	 */
    @PostMapping("/register")
    public ResponseEntity<DtoWebUser> registerUser(@RequestBody @Valid DtoWebUser user) throws EnouMessageException {
        DtoWebUser webUser = userService.saveUser(user);
        return ResponseEntity.ok(webUser);
    }
    
    /**
     * 检测token是否有效， 实际代码在intercepter中实现
     */
    @GetMapping("/token/check")
    public void checkToken(){
    	// It's done in the intercepter.
    }
    
    @PostMapping("/third/info")
    public void updateThirdInfo(@RequestBody @Valid DtoWebUserThirdInfo thirdInfo) throws EnouMessageException {
    	userService.saveUserThirdInfo(thirdInfo);
    }
    
    @GetMapping("/third/info/{thirdParty}")
    public ResponseEntity<DtoWebUserThirdInfo> getThirdInfo(@PathVariable("thirdParty") String thirdParty) {
    	DtoWebUserThirdInfo result = userService.getUserThirdInfo(thirdParty);
    	return ResponseEntity.ok(result);
    }
    
}
