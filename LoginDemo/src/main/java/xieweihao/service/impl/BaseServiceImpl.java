package service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import dao.BaseMapper;
import dao.UserMapper;
import service.BaseService;

public class BaseServiceImpl<T> implements BaseService<T> {
	
	protected BaseMapper<T> baseMapper;

	@Autowired
    protected UserMapper userMapper;
	
	/*
	 * 初始化baseMapper，哪种类型的service实现调用该方法，baseMapper就是那种类型
	 */
	@PostConstruct	//?什么作用
	private void initBaseMapper() throws Exception{
		//获取泛型信息
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		Class clazz = (Class) type.getActualTypeArguments()[0];
		
		//拼接成泛型mapper字符串
		String localField = clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1) + "Mapper";
		
		//通过反射来获取成员变量的值
		Field field = this.getClass().getSuperclass().getDeclaredField(localField);
		Field baseField = this.getClass().getSuperclass().getDeclaredField("baseMapper");
	
		//讲baseDao来进行实例化
		baseField.set(this,field.get(this));
	}
	
	public int insert(T entity) {
        return baseMapper.insert(entity);
    }

    public int insertSelective(T entity) {
        return baseMapper.insertSelective(entity);
    }

    public int deleteByPrimaryKey(String id) {
        return baseMapper.deleteByPrimaryKey(id);
    }

    public T selectByPrimaryKey(String id) {
        return baseMapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(T entity) {
        return baseMapper.updateByPrimaryKeySelective(entity);
    }

    public int updateByPrimaryKey(T entity) {
        return baseMapper.updateByPrimaryKey(entity);
    }

}
