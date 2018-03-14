package console;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.Test;

import lombok.Data;

public class ReflectTest {

	@Data
	public static class Entity {
		boolean judge;
		Boolean judge2;
		String name;
		Integer age;
		List<String> products;
		Map<String, Object> map;
	}

	@Test
	public void test() throws ArrayIndexOutOfBoundsException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		System.out.println(returnDefault(Entity.class));
		// prepareMap(Entity.class);
	}

	protected Object returnDefault(Class<?> type)
			throws InstantiationException, IllegalAccessException, ArrayIndexOutOfBoundsException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {

		Map<Class<?>, Object> typeMap = new HashMap<Class<?>, Object>() {
			{
				put(byte.class, (byte) 1);
				put(Byte.class, (byte) 1);
				put(short.class, (short) 1);
				put(Short.class, (short) 1);
				put(int.class, 1);
				put(Integer.class, 1);
				put(long.class, 1L);
				put(Long.class, 1L);
				put(float.class, 1F);
				put(Float.class, 1F);
				put(double.class, 1D);
				put(Double.class, 1F);
				put(boolean.class, true);
				put(Boolean.class, true);
				put(char.class, 'a');
				put(Character.class, 'a');
				put(String.class, "123");
			}
		};
		if (typeMap.containsKey(type)) {
			return typeMap.get(type);
		} else if (type.isAssignableFrom(Enum.class)) {
			return Array.get(type.getMethod("values").invoke(null), 0);
		} else if (type.isAssignableFrom(Map.class)) {

		} else if (type.isAssignableFrom(List.class)) {
			Type fc = type.getGenericSuperclass();
			if (fc == null)
				return new ArrayList<>();
			if (fc instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) fc;
				Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
				System.out.println(genericClazz.getName());
			}
		} else {
			Object model = type.newInstance();
			Field[] fields = model.getClass().getDeclaredFields();
			BiConsumer<Object, Field> assignmentAction = (o, f) -> {
				String fileName = f.getName();
				Class<?> fileType = f.getType();
				fileName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1);
				Method m = null;
				try {
					m = o.getClass().getMethod("set" + fileName, fileType);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (m != null) {
					try {
						m.invoke(o, returnDefault(fileType));
					} catch (ArrayIndexOutOfBoundsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			for (Field field : fields) {
				assignmentAction.accept(model, field);
			}
			return model;
		}
		return null;
	}

	@Test
	public void test1() {
		boolean bool = true;
		Object abc = bool;
		if(abc instanceof Boolean)
		{
			System.out.print("123");
		}
		prepareMap(Entity.class);
	}

	private Map<String, Class<?>> prepareMap(Class<?> clazz) {
		Map<String, Class<?>> result = new HashMap<>();
		Field[] fs = clazz.getDeclaredFields();
		for (Field f : fs) {
			Class<?> fieldClazz = f.getType();
			if (fieldClazz.isPrimitive()) {
				result.put(f.getName(), fieldClazz);
				System.out.println(fieldClazz.getName());
				continue;
			}
			if (fieldClazz.getName().startsWith("java.lang")) {
				result.put(f.getName(), fieldClazz);
				System.out.println(fieldClazz.getName());
				continue;
			}
			if (fieldClazz.isAssignableFrom(List.class)) {
				Type fc = f.getGenericType();
				if (fc == null)
					continue;
				if (fc instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) fc;
					Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
					result.put(f.getName(), genericClazz);
					System.out.println(genericClazz.getName());
				}
			}
			if (fieldClazz.isAssignableFrom(Map.class)) {

			}
		}
		return result;
	}
	// private void prepareMap(Class<?> clazz) {
	// Field[] fs = clazz.getDeclaredFields();
	// for (Field f : fs) {
	// Class<?> fieldClazz = f.getType();
	// if (fieldClazz.isPrimitive()) {
	// System.out.println(fieldClazz.getName());
	// continue;
	// }
	// if (fieldClazz.getName().startsWith("java.lang")) {
	// System.out.println(fieldClazz.getName());
	// continue;
	// }
	// if (fieldClazz.isAssignableFrom(List.class)) {
	// Type fc = f.getGenericType();
	// if (fc == null)
	// continue;
	// if (fc instanceof ParameterizedType) {
	// ParameterizedType pt = (ParameterizedType) fc;
	// Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
	// System.out.println(genericClazz.getName());
	// }
	// }
	// if (fieldClazz.isAssignableFrom(Map.class)) {
	//
	// }
	// }
	// }
}