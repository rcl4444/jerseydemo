package console.data;

import org.apache.ibatis.annotations.Select;

public interface UserMapper {

	@Select("select 1;")
	public Long select();
}