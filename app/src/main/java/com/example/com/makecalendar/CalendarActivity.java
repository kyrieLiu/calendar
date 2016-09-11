package com.example.com.makecalendar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 日历显示activity
 * 
 * 
 */
public class CalendarActivity extends Activity {

	private GestureDetector gestureDetector = null;
	private CalendarAdapter calV = null;
	private GridView gridView = null;
	private TextView topText = null;
	private static int jumpMonth = 0;
	private static int jumpYear = 0;
	private int year_c = 0;
	private int month_c = 0;
	private int day_c = 0;
	private String currentDate = "";
	private Bundle bd = null;
	private String ruzhuTime;
	private String lidianTime;
	private String state = "";

	private ImageView nextMonth; // 下一月文本框
	private ImageView preMonth; // 上一月文本框
	private TextView selectDateAndTime;
	private TimePicker timePicker;
	//修改文件

	private List<String> dutyList;//值班标识集合

	public CalendarActivity() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
		currentDate = sdf.format(date); // 当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		gestureDetector = new GestureDetector(this, new MyGestureListener());
		calV = new CalendarAdapter(this, getResources(), new Date(),jumpMonth, jumpYear,
				year_c, month_c, day_c);
		addGridView();
		gridView.setAdapter(calV);

		topText = (TextView) findViewById(R.id.tv_month);
		addTextToTopTextView(topText);
		nextMonth = (ImageView) this.findViewById(R.id.right_img);
		preMonth = (ImageView) this.findViewById(R.id.left_img);
		nextMonth.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addGridView();
				jumpMonth++; // 下一个月

				calV = new CalendarAdapter(CalendarActivity.this,
						getResources(), new Date(),jumpMonth, jumpYear, year_c, month_c,
						day_c);
				gridView.setAdapter(calV);
				addTextToTopTextView(topText);
			}			
		});
	
		preMonth.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addGridView();
				jumpMonth--; // 上一个月

				calV = new CalendarAdapter(CalendarActivity.this,
						getResources(), new Date(),jumpMonth, jumpYear, year_c, month_c,
						day_c);
				gridView.setAdapter(calV);
				// gvFlag++;
				addTextToTopTextView(topText);
			}			
		});
	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, Menu.FIRST, Menu.FIRST, "今天");
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 选择菜单
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			jumpYear = 0;
			addGridView();
			year_c = Integer.parseInt(currentDate.split("-")[0]);
			month_c = Integer.parseInt(currentDate.split("-")[1]);
			day_c = Integer.parseInt(currentDate.split("-")[2]);
			calV = new CalendarAdapter(CalendarActivity.this, getResources(), new Date(),jumpMonth,
					jumpYear, year_c, month_c, day_c);
			gridView.setAdapter(calV);
			addTextToTopTextView(topText);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.gestureDetector.onTouchEvent(event);
	}

	// 添加头部的年份闰哪月等信息
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		textDate.append(calV.getShowYear()).append("年")
				.append(calV.getShowMonth()).append("月").append("\t");
		view.setText(textDate);
		view.setTextColor(Color.GRAY);
	}

	// 添加gridview
	private void addGridView() {

		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				// 将Gridview中的触摸事件回传给gestureDetector
				return CalendarActivity.this.gestureDetector
						.onTouchEvent(event);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {
			// gridView中的每一个item的点击事�?
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				int startPosition = calV.getStartPositon();
				int endPosition = calV.getEndPosition();
				if (startPosition <= position + 7
						&& position <= endPosition - 7) {
					String scheduleDay = calV.getDateByClickItem(position)
							.split("\\.")[0]; // 这一天的阳历
					// //这一天的阴历
					String scheduleMonth = calV.getShowMonth();

					ruzhuTime = scheduleMonth + "�?" + scheduleDay + "�?";
					lidianTime = scheduleMonth + "�?" + scheduleDay + "�?";
					// Intent intent=new Intent();
					if (state.equals("ruzhu")) {

						bd.putString("ruzhu", ruzhuTime);
						System.out.println("ruzhuuuuuu" + bd.getString("ruzhu"));
					} else if (state.equals("lidian")) {

						bd.putString("lidian", lidianTime);
					}

				}
			}
		});
	}

	private class MyGestureListener extends
			GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return super.onSingleTapUp(e);
		}

		// 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
		}

		// 用户按下触摸屏，并拖动
		// 多个ACTION_MOVE触发
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		// 多个ACTION_MOVE, 1个ACTION_UP触发
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// int gvFlag = 0; //每次添加gridview到viewflipper中时给的标记
			if (e1.getX() - e2.getX() > 120) {
				// 向左滑动
				nextMonth.performClick();
				return true;
			} else if (e1.getX() - e2.getX() < -120) {
				// 向右滑动
				preMonth.performClick();
				return true;
			}
			return false;
		}

		// 用户轻触触摸屏，尚未松开或拖动
		public void onShowPress(MotionEvent e) {
			super.onShowPress(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return super.onDown(e);
		}

		public boolean onDoubleTap(MotionEvent e) {
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return super.onDoubleTapEvent(e);
		}

		// onDown->onShowPress->onSingleTapUp->onSingleTapConfirmed
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return super.onSingleTapConfirmed(e);
		}

	}
}