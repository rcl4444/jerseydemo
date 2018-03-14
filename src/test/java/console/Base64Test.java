package console;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.function.LongSupplier;

import org.junit.Test;

public class Base64Test {

	/*
	 * url编码是把字符通过编码把字符转成%+十六进制的一串字符 base64是任意二进制到文本字符串的编码方法
	 */
	@Test
	public void base64Test() throws UnsupportedEncodingException {
		String str = "长生不死臭老头";
		String s1 = Base64.getEncoder().encodeToString(str.getBytes());
		String s2 = URLEncoder.encode(str, "UTF-8");
		assertFalse(s1.equals(s2));
	}

	@Test
	public void longTest() {
		LongSupplier returnLong = ()->{
			return new Long(10);
		};
		long a = new Long(10);
		Long bb = 20L;
		Object o = 10;
		assertTrue(a == returnLong.getAsLong());
		assertTrue(a == (bb/2));
		assertTrue(a == Long.parseLong(o.toString()));
		assertTrue(new Long(10) == returnLong.getAsLong());
	}
	
	@Test
	public void stringTest(){
		String a = "1234";
		String b = String.valueOf(1234);
		assertFalse(a==b);
		assertTrue(a.equals(b));
	}
}