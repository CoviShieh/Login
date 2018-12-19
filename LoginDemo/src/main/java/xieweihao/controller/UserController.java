package controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import utils.Captcha;
import utils.GifCaptcha;
import utils.ReadPropertiesUtil;
import entity.ActiveUser;
import entity.User;
import exception.UserException;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController{

	/**
	 * 用户注册：先对数据进行校验，再注册，发送邮件激活
	 * @param user
	 * @param bingingResult
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/register.do")
	public String register(@Validated User user,BindingResult bingingResult) throws Exception{
		//如果参数不对，就直接返回注册页面
		List<ObjectError> allErrors = bingingResult.getAllErrors();
		if(allErrors != null && allErrors.size() > 0){
			return "redirect:/goURL/user/toRegister.do";
		}
		
		userService.encryptedPassword(user);  //对密码进行加密md5(密码+salt)后才存到数据库中
		userService.insert(user);
		
		String url = getProjectPath()+"/user/activate.do?userId="+user.getUserId();
		emailService.sendEmail(user, "注册", url);
		return "redirect:/common/countDown.html";
	}
	
	/**
	 * 检测邮箱是否存在，如果存在则不给予注册
	 * @param userEmail
	 * @param writer
	 */
	@RequestMapping("/validateEmail.do")
	public void vaildateEmail(String userEmail,PrintWriter writer){
		User user = userService.validateEmailExist(userEmail);
		if(user != null && user.getUserId() !=null){
			writer.write("hasEmail");
		}else{
			writer.write("noEmail");
		}
	}
	
	@RequestMapping("/activate.do")
	public String activate(String userId) throws Exception{
		
		User user = userService.selectByPrimaryKey(userId);
		String title = "";
		String content = "";
		String subject = "";
		
		if(user != null){
			if(System.currentTimeMillis() - user.getTokenExptime().getTime() < 86400000){	//有效时间24小时内
				user.setActiState(User.ACTIVATION_SUCCESSFUL);
				userService.updateByPrimaryKeySelective(user);
				title = "用户激活页面";
                subject = "用户激活";
                content = "恭喜您成功激活账户";
			}else{
				title = "激活失败页面";
                subject = "用户激活";
                content = "激活链接已超时，请重新注册";
                userService.deleteByPrimaryKey(userId);	//删除记录，以便用户再次注册
			}
		}
		//根据模版生成页面，重定向到页面中
		Map<String,Object> map = new HashMap();
		map.put("title", title);
		map.put("content", content);
		map.put("subject", subject);
		map.put("path", getProjectPath());
		createCommonHtml("promptPages.ftl","promptPages.html",map);
		return "redirect:/proptPages.html";
	}
	
	/**
	 * 生成随机验证码
	 * @param request
	 * @param response
	 * @throws Exception 
	 * @deprecated 已被动态GIF验证码替代
	 */
	@RequestMapping("/createCaptcha.do")
	public void createCaptcha(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		BufferedImage img = new BufferedImage(68 ,22 ,1);
		Graphics g = img.getGraphics();
        Random random = new Random();
        Color c = new Color(253, 255, 238);
        g.setColor(c);
        g.fillRect(0, 0, 68, 22);
        StringBuffer sb = new StringBuffer();
        char[] ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        int len = ch.length;
        
        for (int i = 0; i < 4; ++i) {
            int index = random.nextInt(len);
            g.setColor(new Color(random.nextInt(88), random.nextInt(188), random.nextInt(255)));
            g.setFont(new Font("Arial", 3, 22));
            g.drawString("" + ch[index], i * 15 + 3, 18);
            sb.append(ch[index]);
        }
        request.getSession().setAttribute("captcha", sb.toString());
		ImageIO.write(img, "JPG", response.getOutputStream());
	}
	
	@RequestMapping(value="/getGifCode", method=RequestMethod.GET)
	public void getGifCode(HttpServletResponse response, HttpServletRequest request) throws Exception{
		response.setHeader("Prama", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/gif");
		
		Captcha captcha = new GifCaptcha(146,46,4);
		//这里可能会有异常
		ServletOutputStream out = response.getOutputStream();
		captcha.out(out);
		request.getSession().setAttribute("captcha", captcha.text().toLowerCase());
	}
	
	@RequestMapping("/login.do")
	public String login(HttpServletRequest request) throws Exception{
		//如果登陆失败从request中获取认证异常信息，shiroLoginFailure就是shiro异常类的全限定名
		String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
		
		//根据shiro返回的异常类路径判断，抛出指定异常信息
		if (exceptionClassName != null) {
            if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
                throw new UserException("账号不存在");
            } else if (IncorrectCredentialsException.class.getName().equals(
                    exceptionClassName)) {
                throw new UserException("密码错误了");
            } else if ("captchaCodeError".equals(exceptionClassName)) {
                throw new UserException("验证码错误了");
            } else {
                throw new Exception();//最终在异常处理器生成未知错误
            }
        }
		return "redirect:/goURL/user/toLogin.do";
	}
	
	@RequestMapping("/getUser.do")
	@ResponseBody
	public Map<String,Object> getUser(HttpSession session){
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		ActiveUser user = (ActiveUser) session.getAttribute("acativeUser");
		if(user != null){
			map.put("user", user);
		}
		
		return map;
	}
	
	 /**
     * 用户忘记密码，发送邮件让用户去修改密码
     *
     * @param userEmail
     * @return
     * @throws Exception
     */
    @RequestMapping("/forgetPassword.do")
    public String forgetPassword(String userEmail) throws Exception {
        //判断是否有该用户
        User user = null;
        if (userEmail != null) {
            user = userService.validateUserExist(userEmail);
        }
        //提示用户发送了邮件，让用户重新设置密码账户
        String url = ReadPropertiesUtil.readProp("projectPath") + "/common/resetView.html?userId=" + user.getUserId();
        emailService.sendEmail(user, "重置密码", url);

        //设置邮件发送时间、30分钟链接失效
        user.setTokenExptime(new Date());
        userService.updateByPrimaryKeySelective(user);

        return "redirect:/common/countDown.html";
    }
    
    /**
     * 修改密码
     *
     * @param user
     * @return
     */
    @RequestMapping("/resetPassword.do")
    @ResponseBody
    public Map<String, Object> resetPassword(User user) {

        Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

        if (user != null && user.getUserId() != null) {
            User oldUser = userService.selectByPrimaryKey(user.getUserId());

            //得到当前时间和邮件时间对比,24小时
            if (System.currentTimeMillis() - oldUser.getTokenExptime().getTime() < 86400000) {
                userService.encryptedPassword(user);
                int num = userService.updateByPrimaryKeySelective(oldUser);
                if (num > 0) {
                    resultMap.put("message", "修改成功");
                } else {
                    resultMap.put("message", "修改失败");
                }
                return resultMap;
            }else{
                resultMap.put("message", "链接已超时，请重新进行操作");

            }

        }

        return null;
    }
}
