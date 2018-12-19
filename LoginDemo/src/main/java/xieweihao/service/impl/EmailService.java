package service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import entity.User;
import service.UserService;
import utils.FreeMarkerUtils;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private SimpleMailMessage simpleMailMessage;
	
	@Autowired 
	private UserService userService;
	
	/**
	 * 使用mimeMessage发送html格式的邮件
	 * @param user
	 * @param content
	 * @param url
	 * @throws Exception
	 */
	public void sendEmail(User user, String content, String url) throws Exception{
		
		String returnText = createSendData(user,content,url);
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		messageHelper.setFrom(simpleMailMessage.getFrom());
		messageHelper.setSubject(simpleMailMessage.getSubject());
		messageHelper.setTo(user.getUserEmail()); //接收人
		messageHelper.setText(returnText, true); //内容，是html格式
		
		mailSender.send(mimeMessage);
	}

	/**
	 * 使用freemarker创建要发送的邮件内容
	 * @param user
	 * @param content
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String createSendData(User user, String content, String url) throws Exception {
		
		Map<String,Object> map = new HashMap();
		map.put("nickName", user.getUserNickname());
		map.put("content", content);
		map.put("url", url);
		map.put("encodeUrl", "");
		
		String text = new FreeMarkerUtils().returnText("email.ftl", map);
		return text;
	}
	
}
