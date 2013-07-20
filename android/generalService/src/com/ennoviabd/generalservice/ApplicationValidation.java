package com.ennoviabd.generalservice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationValidation {
	public void ApplicationValidation()
	{
		
	}
	public static boolean  isvalidDate(String registerdate) {
		// dd/mm/yyyy
		if (registerdate.length()<1)
			return true;
		
        String regEx =  "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
          Matcher matcherObj = Pattern.compile(regEx).matcher(registerdate);
          if (matcherObj.matches())
            {
                return true;
            }
            else
            {
                return false;
            }
    }
}
