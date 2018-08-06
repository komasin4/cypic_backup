package util;

public class StringUtil {
	public static String getVariable(String src, String var)	{
		String value = null;

		try	{

			int idx = src.indexOf(var);

			if(idx > -1)	{
				String s1 = src.substring(idx);
				String s2 = s1.substring(s1.indexOf("\"") + 1);
				value = s2.substring(0, s2.indexOf("\""));
			}
		} catch (Exception e)	{
			e.printStackTrace();
		}

		return value;
	}
}
