package com.sunbird.entity.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

	public static final String UTC_TIMEZONE = "UTC";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Date getCurrentDateTimeInUTC() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
		format.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE));
		String currentDate = format.format(new Date());
		return format.parse(currentDate);
	}

	public static Long getCurrentTimestamp() {
		return new Date().getTime();
	}

	public static String getCurrentDateTime() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
		format.setTimeZone(TimeZone.getTimeZone(UTC_TIMEZONE));
		return format.format(new Date());
	}

}
