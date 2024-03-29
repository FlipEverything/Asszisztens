package tools;

import java.io.File;

public class Const {
	/**
	 * DoctorSchedule
	 * DataWindow
	 * NewWindow
	 * DeleteWindow
	 */
	public static final int CALENDAR_TIME_BETWEEN_TIME = 5;
	public static final int CALENDAR_START_HOUR = 7;
	public static final int CALENDAR_END_HOUR = 21;
	
	public static final String PROJECT_PATH = new File("").getAbsolutePath();//Const.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("/bin/", "");
	//public static final String ICON_PATH = PROJECT_PATH+"\\icon\\";
	//public static final String RESOURCES_PATH = PROJECT_PATH+"\\resources\\";
	public static final String ICON_PATH = PROJECT_PATH+"\\";
	public static final String RESOURCES_PATH = PROJECT_PATH+"\\";
	
	
	public static String getDayOfTheWeek(int i) {
		String day = null;
        
        switch (i){
	        case 1: day="Vasárnap"; break;
	        case 2: day="Hétfő"; break;
	        case 3: day="Kedd"; break;
	        case 4: day="Szerda"; break;
	        case 5: day="Csütörtök"; break;
	        case 6: day="Péntek"; break;
	        case 7: day="Szombat"; break;
        }
        return day;
    }
	
}
