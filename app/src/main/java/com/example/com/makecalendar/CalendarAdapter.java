package com.example.com.makecalendar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 日历GridView中的每一个item显示的textView
 * @author Hesky
 *
 */
public class CalendarAdapter extends BaseAdapter {
	private boolean isLeapyear = false;  //是否为闰年
	private int daysOfMonth = 0;      //某月的天数
	private int dayOfWeek = 0;        //具体某一天是星期几
	private int lastDaysOfMonth = 0;  //上个月的总天数
	private Context context;
	private String[] dayNumber = new String[42];  //一个GridView中的日期存入此数组中
	private SpecialCalendar sc = null;
	private LunarCalendar lc = null;
	
	private String currentYear = "";
	private String currentMonth = "";

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d",Locale.CHINA);
	private int currentFlag = -1;     //用于标记当天
	private int[] schDateTagFlag = null;  //存储当月所有的日程日期
	
	private String showYear = "";   //用于在头部显示的年份
	private String showMonth = "";  //用于在头部显示的月份
	private String animalsYear = ""; 
	private String leapMonth = "";   //闰哪一个月
	private String cyclical = "";   //天干地支
	//当前时间
	private String sysDate = "";  
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";
	private List<String> list=new ArrayList<>();

	public CalendarAdapter(Date date){
		sysDate = sdf.format(date);  //当前日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];

	}
	
	public CalendarAdapter(Context context,Resources rs,Date date,int jumpMonth,int jumpYear,int year_c,int month_c,int day_c){
		this(date);
		this.context= context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		
		int stepYear = year_c+jumpYear;
		int stepMonth = month_c+jumpMonth ;
		if(stepMonth > 0){
			//往下一个月滑动
			if(stepMonth%12 == 0){
				stepYear = year_c + stepMonth/12 -1;
				stepMonth = 12;
			}else{
				stepYear = year_c + stepMonth/12;
				stepMonth = stepMonth%12;
			}
		}else{
			//往上一个月滑动
			stepYear = year_c - 1 + stepMonth/12;
			stepMonth = stepMonth%12 + 12;
			if(stepMonth%12 == 0){
				
			}
		}
	
		currentYear = String.valueOf(stepYear);;  //得到当前的年份
		currentMonth = String.valueOf(stepMonth);  //得到本月(jumpMonth为滑动的次数,每滑动一次就增加一月或减一月

		getCalendar(Integer.parseInt(currentYear),Integer.parseInt(currentMonth));

		initDutyList();
		
	}

	private void initDutyList(){
		for (int i=0;i<daysOfMonth;i++){
			if (i%3==0){
				list.add("1");
			}else if (i%2==0){
				list.add("123");
			}else if(i%5==0){
				list.add("13");
			}else if(i%7==0){
				list.add("3");
			}else{
				list.add("0");
			}
		}
	}

	
	@Override
	public int getCount() {
		return dayNumber.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.activity_calendar_item, parent,false);
		 }
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		ImageView imageView=(ImageView)convertView.findViewById(R.id.iv_item_onduty_flag);
		String d = dayNumber[position].split("\\.")[0];

		textView.setText(d);
		textView.setTextColor(Color.GRAY);
		

		
		if (position < daysOfMonth + dayOfWeek && position >= dayOfWeek) {
			// 当前信息显示
			textView.setTextColor(Color.BLACK);
			String dutyFlag=list.get(position-dayOfWeek);
			switch (dutyFlag){
				case "0":
					imageView.setImageResource(R.drawable.onduty_rest);
					break;
				case "1":
					imageView.setImageResource(R.drawable.onduty_morning);
					break;
				case "3":
					imageView.setImageResource(R.drawable.onduty_night);
					break;
				case "13":
					imageView.setImageResource(R.drawable.onduty_morning_nignt);
					break;
				case "123":
					imageView.setImageResource(R.drawable.onduty_all);
					break;
			}
		}
		if(schDateTagFlag != null && schDateTagFlag.length >0){
			for(int i = 0; i < schDateTagFlag.length; i++){
				if(schDateTagFlag[i] == position){
					textView.setBackgroundResource(R.drawable.mark);
				}
			}
		}
		if(currentFlag == position){
			textView.setBackgroundResource(R.mipmap.bg_calendar_date);
			textView.setTextColor(Color.WHITE);
		}
		return convertView;
	}
	//得到某年的某月的天数切这月的第一天是星期几
	public void getCalendar(int year, int month){
		isLeapyear = sc.isLeapYear(year);              //是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month);  //某月的天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month);      //某月的第一天是星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month-1);  //上一个月的总天数
		Log.d("DAY", isLeapyear+" ======  "+daysOfMonth+"  ============  "+dayOfWeek+"  =========   "+lastDaysOfMonth);
		getweek(year,month);
	}
	
	//将一个月中的每一天的值添加入数组dayNumber
	private void getweek(int year, int month) {
		int j = 1;
		String lunarDay = "";
		
		//得到当前月的所有日程日期

		for (int i = 0; i < dayNumber.length; i++) {

			 if(i < dayOfWeek){  //前一个月
				int temp = lastDaysOfMonth - dayOfWeek+1;
				lunarDay = lc.getLunarDate(year, month-1, temp+i,false);
				dayNumber[i] = (temp + i)+"."+lunarDay;
				
			}else if(i < daysOfMonth + dayOfWeek){   //本月
				String day = String.valueOf(i-dayOfWeek+1);   //得到的日期
				lunarDay = lc.getLunarDate(year, month, i-dayOfWeek+1,false);
				dayNumber[i] = i-dayOfWeek+1+"."+lunarDay;
				//对于当前月才去标记当前日期
				if(sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)){
					//标记当前日期
					currentFlag = i;
				}	
				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0?"":String.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			}else{   //下一个月
				lunarDay = lc.getLunarDate(year, month+1, j,false);
				dayNumber[i] = j+"."+lunarDay;
				j++;
			}
		}
        
        String abc = "";
        for(int i = 0; i < dayNumber.length; i++){
        	 abc = abc+dayNumber[i]+":";
        }
        Log.d("DAYNUMBER",abc);


	}
	
	
	public void matchScheduleDate(int year, int month, int day){
		
	}
	
	/**
	 * 点击每一个item时返回itemn中的日期
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position){
		return dayNumber[position];
	}
	
	/**
	 * 在点击gridView时,得到这个月中的第一天的位置
	 * @return
	 */
	public int getStartPositon(){
		return dayOfWeek+7;
	}
	
	/**
	 * 点击gridView时,得到这个月中最后一天的位置
	 * @return
	 */
	public int getEndPosition(){
		return  (dayOfWeek+daysOfMonth+7)-1;
	}
	
	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}
	
	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}
	
	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}
	
	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}
}
