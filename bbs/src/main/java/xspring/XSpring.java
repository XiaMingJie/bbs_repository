package xspring;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.ui.Model;

public class XSpring
{
	public static void putModel(Model model, Object pojo)
	{
		Class cls = pojo.getClass();
		
		//获得所有属性
		Field[] fields = cls.getDeclaredFields();
		
		for(Field f : fields)
		{
			//属性名
			String name = f.getName();
			
			//如果是public属性
			if(Modifier.isPublic(f.getModifiers()))
			{
				try{
					Object value = f.get(pojo);
					model.addAttribute(name, value);
				}
				catch(Exception e){
					
				}
			}
			//若不是public则取出Getter
			else
			{
				//取得Getter名
				String getterName = getterName(name);
				
				try{
					Method getter = cls.getDeclaredMethod(getterName);
					Object value = getter.invoke(pojo);
					model.addAttribute("name", value);
				}
				catch(Exception e){
					
				}
			}
		}
	}
	
	//取出Getter名
	public static String getterName(String field)
	{
		//将属性第一个字符大写
		char firstChar = Character.toUpperCase(field.charAt(0));
		//拼接字符串
		StringBuffer strbuf = new StringBuffer("get" + field);
		//替换第四个字符
		strbuf.setCharAt(3, firstChar);
		return strbuf.toString();
	}
}
