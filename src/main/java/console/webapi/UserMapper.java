package console.webapi;

import org.apache.ibatis.annotations.Select;

public interface UserMapper {

	@Select("select 1;")
	public Long select();
}