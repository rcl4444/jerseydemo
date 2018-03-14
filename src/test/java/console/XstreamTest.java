package console;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class XstreamTest {
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TreeNode {
		String nodeName;
		@JsonIgnore
		int orderIndex;
		List<TreeNode> children;
	}

	@Test
	public void test() {
		List<MenuData> result = new ArrayList<MenuData>();
		result.add(MenuData.builder().code("6").icon("companyInfo").vuexGetter(null).name("企业信息").isRoot(false)
				.outerLink(null).jumpWay("self").orderIndex(20).children(null).build());
		// List<MenuData> systemChildren = new ArrayList<MenuData>(){{
		// add(MenuData.builder().code("2").icon("role").vuexGetter(null).name("岗位设置").isRoot(false)
		// .outerLink(null).jumpWay("self").orderIndex(0).build());
		// add(MenuData.builder().code("3").icon("staff").vuexGetter(null).name("员工设置").isRoot(false)
		// .outerLink(null).jumpWay("self").orderIndex(1).build());
		// add(MenuData.builder().code("11").icon("voucherList").vuexGetter(null).name("权限设置").isRoot(false)
		// .outerLink(null).jumpWay("self").orderIndex(2).build());
		// }};
		List<MenuData> systemChildren = new ArrayList<MenuData>();
		systemChildren.add(MenuData.builder().code("2").icon("role").vuexGetter(null).name("岗位设置").isRoot(false)
				.outerLink(null).jumpWay("self").orderIndex(0).build());
		systemChildren.add(MenuData.builder().code("3").icon("staff").vuexGetter(null).name("员工设置").isRoot(false)
				.outerLink(null).jumpWay("self").orderIndex(1).build());
		systemChildren.add(MenuData.builder().code("11").icon("voucherList").vuexGetter(null).name("权限设置").isRoot(false)
				.outerLink(null).jumpWay("self").orderIndex(2).build());
		result.add(MenuData.builder().code(null).icon("setting").vuexGetter(null).name("系统设置").isRoot(true)
				.outerLink(null).jumpWay("self").orderIndex(30).children(systemChildren).build());
		result.add(MenuData.builder().code("12").icon("goodsList").vuexGetter(null).name("产品列表").isRoot(false)
				.outerLink(null).jumpWay("self").orderIndex(50).children(null).build());
		Object s = new XStreamSerializer()
				.serialize(new SessionPassiveLogoutEvent("123456", new LoginContext("123456", result, 1)), String.class)
				.getData();
		System.out.println(s);
	}
}