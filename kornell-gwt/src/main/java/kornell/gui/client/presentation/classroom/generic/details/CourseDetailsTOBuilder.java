package kornell.gui.client.presentation.classroom.generic.details;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import kornell.core.to.coursedetails.CourseDetailsTO;
import kornell.core.to.coursedetails.HintTO;
import kornell.core.to.coursedetails.InfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPresenter;

public class CourseDetailsTOBuilder {
	private final static Logger logger = Logger.getLogger(AdminInstitutionPresenter.class.getName());
	
	private String jsonString;
	private CourseDetailsTO courseDetailsTO;
	
	private enum ParseType {
		INFOS("infos"),
		HINTS("hints");

		private String code;
		private ParseType(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}
	} 

	public CourseDetailsTOBuilder(String jsonString) {
		this.jsonString = jsonString;
		this.courseDetailsTO = new CourseDetailsTO();
	}
	
	public boolean buildCourseDetails(){
		JSONValue jsonValue;
	    JSONObject jsonObject;
	    
	    if(StringUtils.isNone(jsonString) || "".equals(jsonString.trim())){
	    	return false;
	    }
	    
	    jsonValue = JSONParser.parseStrict(jsonString);
	
		if ((jsonObject = jsonValue.isObject()) == null) {
			logger.warning("Error parsing the JSON");
		}
		boolean ret = true;
		for (ParseType parseType : ParseType.values()) {
			ret = ret && parseJSON(jsonObject.get(parseType.getCode()), parseType);
		}
	    return ret;
	}
	
	private boolean parseJSON(JSONValue jsonValue, ParseType parseType){
	    JSONArray jsonArray;
	    JSONObject jsonObject;
		JSONString type;
		JSONString text;
		
		if (jsonValue == null) {
			logger.warning("Error parsing the JSON");
			return false;
		}
		if ((jsonArray = jsonValue.isArray()) == null) {
			logger.warning("Error parsing the JSON");
			return false;
		}


		switch (parseType) {
		
		case HINTS:
			List<HintTO> hints = new ArrayList<HintTO>();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonValue = jsonArray.get(i);
				if ((jsonObject = jsonValue.isObject()) == null) {
					logger.warning("Error parsing the JSON");
					return false;
				}
				jsonValue = jsonObject.get("type");
				if ((type = jsonValue.isString()) == null) {
					logger.warning("Error parsing the JSON");
					return false;
				}
				
				jsonValue = jsonObject.get("text");
				if ((text = jsonValue.isString()) == null) {
					logger.warning("Error parsing the JSON");
					return false;
				}		
				hints.add(new HintTO(type.stringValue(),text.stringValue()));
				courseDetailsTO.setHints(hints);	
			}
			break;
			
			
		case INFOS:
			List<InfoTO> infos = new ArrayList<InfoTO>();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonValue = jsonArray.get(i);
				if ((jsonObject = jsonValue.isObject()) == null) {
					logger.warning("Error parsing the JSON");
					return false;
				}
				jsonValue = jsonObject.get("type");
				if ((type = jsonValue.isString()) == null) {
					logger.warning("Error parsing the JSON");
					return false;
				}
				
				jsonValue = jsonObject.get("text");
				if ((text = jsonValue.isString()) == null) {
					logger.warning("Error parsing the JSON");
					return false;
				}		
				infos.add(new InfoTO(type.stringValue(),text.stringValue()));
				courseDetailsTO.setInfos(infos);	
			}
			break;
		default:
			break;
		}
		return true;
	}

	public CourseDetailsTO getCourseDetailsTO() {
		return courseDetailsTO;
	}

	public void setCourseDetailsTO(CourseDetailsTO courseDetailsTO) {
		this.courseDetailsTO = courseDetailsTO;
	}
	
}
