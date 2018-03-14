package console;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuData {
	String code;
	String icon;
	String vuexGetter;
	String name;
	Boolean isRoot;
	String outerLink;
	String jumpWay;
	@JsonIgnore
	int orderIndex;
	List<MenuData> children;
}