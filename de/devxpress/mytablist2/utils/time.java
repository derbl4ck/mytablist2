package de.devxpress.mytablist2.utils;

import java.util.Calendar;

public class time {

	public enum type {
		YEAR,
		MONTH,
		DAY,
		HOURS,
		MINUTES,
		SECONDS,
		FULL,
		TIME,
		DATE,
	}
	
	public String getTime(type type){
		Calendar now = Calendar.getInstance();
		int hours = now.get(Calendar.HOUR_OF_DAY);
		int minutes = now.get(Calendar.MINUTE);
		int seconds = now.get(Calendar.SECOND);
		int day = now.get(Calendar.DAY_OF_MONTH);
		int month = now.get(Calendar.MONTH);
		int year = now.get(Calendar.YEAR);
		
		if(type == de.devxpress.mytablist2.utils.time.type.YEAR){
			return String.valueOf(year);
		}else if(type == de.devxpress.mytablist2.utils.time.type.MONTH){
			return String.valueOf(month);
		}else if(type == de.devxpress.mytablist2.utils.time.type.DAY){
			return String.valueOf(day);
		}else if(type == de.devxpress.mytablist2.utils.time.type.HOURS){
			return String.valueOf(hours);
		}else if(type == de.devxpress.mytablist2.utils.time.type.MINUTES){
			return String.valueOf(minutes);
		}else if(type == de.devxpress.mytablist2.utils.time.type.SECONDS){
			return String.valueOf(seconds);
		}else if(type == de.devxpress.mytablist2.utils.time.type.FULL){
			return String.valueOf(day + "." + month + "." + year + " " + hours + ":" + minutes + ":" + seconds);
		}else if(type == de.devxpress.mytablist2.utils.time.type.TIME){
			return String.valueOf(hours + ":" + minutes + ":" + seconds);
		}else if(type == de.devxpress.mytablist2.utils.time.type.DATE){
			return day + "." + month + "." + year;
		}else{
			return "";
		}
	}
	
}
