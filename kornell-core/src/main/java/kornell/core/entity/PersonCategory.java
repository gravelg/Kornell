package kornell.core.entity;



public class PersonCategory {

	public static String getSexSuffix(Person person, String locale) {
		if (locale == "pt_BR") {
			if ("F".equals(person.getSex()))
				return "a";
			else if("M".equals(person.getSex()))
				return "o";
			else
				return "o(a)";
		} else {
			return "";
		}
	}
}
