package console.webapi;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;

import com.loginbox.dropwizard.mybatis.AbstractMybatisBundle;

public class MyBatisInitializer extends AbstractMybatisBundle {

	public SqlSessionManager dbInitializer(){
		SqlSessionFactory sqlSessionFactory = getSqlSessionFactory();
		SqlSessionManager manager = SqlSessionManager.newInstance(sqlSessionFactory);
		manager.getConfiguration().setMapUnderscoreToCamelCase(true);
		return manager;
	}
}