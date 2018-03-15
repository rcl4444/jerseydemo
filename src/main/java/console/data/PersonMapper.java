package console.data;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import console.person.Person;

public interface PersonMapper {
	@Insert("insert into person(name,age) values (#{name},#{age})")
	@Options(useGeneratedKeys = true)
	long insert(Person p);

	@Update("update person set check_code=#{checkCode}")
	int update(@Param("personId") long personId, @Param("checkCode") String checkCode);
}