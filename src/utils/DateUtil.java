package utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
	
	public static Date addDays(Date now,int amount){
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DATE, amount);
		return cal.getTime();
	}

	public static long subDate(Date date1, Date date2) {
		long diff = date1.getTime() - date2.getTime();
		return TimeUnit.MILLISECONDS.toHours(diff);		
	}
}
