package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * 根据key读取配置文件的内容
 */
public class ReadPropertiesUtil {

	public static String readProp(String key){
		InputStream in = ReadPropertiesUtil.class.getClassLoader().getResourceAsStream("system.properties");
		Properties prop = new Properties();
		try{
			prop.load(in);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return prop.getProperty(key);
	}
}
