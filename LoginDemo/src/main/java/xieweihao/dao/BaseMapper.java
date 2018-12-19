package dao;

/**
 * 将通用的方法抽取在BaseMapper中，那么就不用在每个单独的Mapper都要写对应的方法了。
 * @param <T> 泛型参数
 */
public interface BaseMapper<T> {

    int insert(T entity);

    int insertSelective(T entity);

    int deleteByPrimaryKey(String id);

    T selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(T entity);

    int updateByPrimaryKey(T entity);

}
