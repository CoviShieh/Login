package controller;

import java.util.Map;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import service.UserService;
import service.impl.EmailService;
import utils.FreeMarkerUtils;
import utils.ReadPropertiesUtil;

@Controller
public class BaseController {

	@Autowired
	protected UserService userService;
	@Autowired
	protected EmailService emailService;
	@Autowired
	protected Scheduler sche;
	
	protected String indexName = ReadPropertiesUtil.readProp("indexName");
	protected String type = ReadPropertiesUtil.readProp("type");
	
	@RequestMapping("/goURL/{folder}/{file}.do")
	public String goURL(@PathVariable("folder") String folder,@PathVariable("file") String file){
		
		return "/"+folder+"/"+file+".ftl";
	}
	
	public String getProjectPath(){
		return ReadPropertiesUtil.readProp("projectPath");
	}
	
	public void createCommonHtml(String ftlName, String fileName, Map<String, Object> map) throws Exception{
		
		FreeMarkerUtils markerUtils = new FreeMarkerUtils();
		markerUtils.outputFile(ftlName, fileName, map);
	}
}
