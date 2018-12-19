package service;
/**
 * 将通用的业务方法抽取到BaseService中，那么实现它的Service都有这些方法了。
 * @author xieweihao
 *
 * @param <T>
 */
public interface BaseService<T> {

	int insert(T entity);

    int insertSelective(T entity);

    int deleteByPrimaryKey(String id);

    T selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(T entity);

    int updateByPrimaryKey(T entity);
}
