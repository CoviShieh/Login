package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

/*
 * FreeMarker工具类，生成html或者html数据
 */
public class FreeMarkerUtils {

	public void outputFile(String ftlName,String fileName, Map<String,Object> map) throws Exception{
		//创建FreeMarker的配置
		Configuration config = new Configuration();
		//指定默认编码格式
		config.setDefaultEncoding("UTF-8");
		//设置模板的包路径
		config.setClassForTemplateLoading(this.getClass(), "/ftl");
		//获得包的模板
		Template template = config.getTemplate(ftlName);
		//定义输出流，注意：必须指定编码，输出到根项目中
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(UtilPath.getRootPath() + "/" + fileName)), "UTF-8"));
		//生成模板
		template.process(map, writer);
	}
	
	public String returnText(String ftlName, Map<String, Object> map) throws Exception{
		
		Configuration config = new Configuration();
		config.setDefaultEncoding("UTF-8");
		config.setClassForTemplateLoading(this.getClass(), "/ftl");
		
		Template template = config.getTemplate(ftlName);
		return FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
	}
	
	public static void main(String[] args) throws Exception {
		FreeMarkerUtils freeMarkerUtils = new FreeMarkerUtils();
		freeMarkerUtils.outputFile("navbar.ftl", "navbar.html", null);
	}
}
