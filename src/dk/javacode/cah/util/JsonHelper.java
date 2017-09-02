package dk.javacode.cah.util;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonHelper {

	public static <E>JSONArray toArray(List<E> list) {
		JSONArray rv = new JSONArray();
		for (E e : list) {
			JSONObject o = new JSONObject(e);
//			System.out.println(o);
			rv.put(o);
		}
		return rv;
	}
}
