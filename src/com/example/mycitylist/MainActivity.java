package com.example.mycitylist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.example.mycitylist.MyLetterListView.OnTouchingLetterChangedListener;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private BaseAdapter adapter;
	private ListView listView;
	private TextView overlay; // 对话框首字母textView
	private MyLetterListView letterListView; // A-Z listView
	private HashMap<String, Integer> alphaIndexer; // 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private Handler handler;
	private OverlayThread overlayThread; // 显示首字母对话框
	private ArrayList<City> allCityLists; // 所有城市列表
	private ArrayList<City> cityLists;// 城市列表

	ListAdapter.TopViewHolder topViewHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.list_view);
		allCityLists = new ArrayList<City>();
		
		letterListView = (MyLetterListView) findViewById(R.id.myLetterListView);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();
		hotCityInit();
		setAdapter(allCityLists);
	}

	public void hotCityInit() {
		City city = new City("", "-");
		allCityLists.add(city);
		city = new City("", "-");
		allCityLists.add(city);
		city = new City("上海", "");
		allCityLists.add(city);
		city = new City("北京", "");
		allCityLists.add(city);
		city = new City("广州", "");
		allCityLists.add(city);
		city = new City("深圳", "");
		allCityLists.add(city);
		city = new City("武汉", "");
		allCityLists.add(city);
		city = new City("天津", "");
		allCityLists.add(city);
		city = new City("西安", "");
		allCityLists.add(city);
		city = new City("南京", "");
		allCityLists.add(city);
		city = new City("杭州", "");
		allCityLists.add(city);
		city = new City("成都", "");
		allCityLists.add(city);
		city = new City("重庆", "");
		allCityLists.add(city);
		cityLists = getCityList();
		allCityLists.addAll(cityLists);
	}

	private ArrayList<City> getCityList() {
		DBHelper dbHelper = new DBHelper(this);
		ArrayList<City> list = new ArrayList<City>();
		try {
			dbHelper.createDataBase();
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery("select * from city", null);
			City city;
			while (cursor.moveToNext()) {
				city = new City(cursor.getString(1), cursor.getString(2));
				list.add(city);
			}
			cursor.close();
			db.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Collections.sort(list, comparator);
		return list;
	}

	Comparator<City> comparator = new Comparator<City>() {
		@Override
		public int compare(City lhs, City rhs) {
			// TODO Auto-generated method stub
			String a = lhs.getPinyi().substring(0, 1);
			String b = rhs.getPinyi().substring(0, 1);
			return a.compareTo(b);
		}
	};

	private void setAdapter(List<City> list) {
		adapter = new ListAdapter(this, list);
		listView.setAdapter(adapter);
	}

	public class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<City> list;
		final int VIEW_TYPE = 3;

		public ListAdapter(Context context, List<City> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// 当前拼音首字母
				String currentStr = getAlpha(list.get(i).getPinyi());
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
						.getPinyi()) : "";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(list.get(i).getPinyi());
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			int type = 0;
			if (position == 0) {
				type = 2;
			} else if (position == 1) {
				type = 1;
			}
			return type;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return VIEW_TYPE;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			int viewType = getItemViewType(position);
			if (viewType == 1) {
				if (convertView == null) {
					topViewHolder = new TopViewHolder();
					convertView = inflater.inflate(R.layout.first_list_item,
							null);
					topViewHolder.alpha = (TextView) convertView
							.findViewById(R.id.alpha);
					topViewHolder.name = (TextView) convertView
							.findViewById(R.id.lng_city);
					convertView.setTag(topViewHolder);
				} else {
					topViewHolder = (TopViewHolder) convertView.getTag();
				}

				topViewHolder.name.setText("正在定位所在位置..");
				topViewHolder.alpha.setVisibility(View.VISIBLE);
				topViewHolder.alpha.setText("定位城市");
			} else if (viewType == 2) {
				final ShViewHolder shViewHolder;
				if (convertView == null) {
					shViewHolder = new ShViewHolder();
					convertView = inflater.inflate(R.layout.search_item, null);
					shViewHolder.editText = (EditText) convertView
							.findViewById(R.id.sh);
					convertView.setTag(shViewHolder);
				} else {
					shViewHolder = (ShViewHolder) convertView.getTag();
				}
			} else {
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.list_item, null);
					holder = new ViewHolder();
					holder.alpha = (TextView) convertView
							.findViewById(R.id.alpha);
					holder.name = (TextView) convertView
							.findViewById(R.id.name);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if (position >= 1) {
					holder.name.setText(list.get(position).getName());
					String currentStr = getAlpha(list.get(position).getPinyi());
					String previewStr = (position - 1) >= 0 ? getAlpha(list
							.get(position - 1).getPinyi()) : " ";
					if (!previewStr.equals(currentStr)) {
						holder.alpha.setVisibility(View.VISIBLE);
						if (currentStr.equals("#")) {
							currentStr = "热门城市";
						}
						holder.alpha.setText(currentStr);
					} else {
						holder.alpha.setVisibility(View.GONE);
					}
				}
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha; // 首字母标题
			TextView name;// 城市名字
		}

		private class TopViewHolder {
			TextView alpha; // 首字母标题
			TextView name;// 城市名字
		}

		private class ShViewHolder {
			EditText editText;
		}

	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(String s) {
			// TODO Auto-generated method stub
			s = s.toLowerCase();
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				listView.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				//延迟1秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1000);
			}
		}

	}

	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			overlay.setVisibility(View.GONE);
		}

	}

	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.equals("-")) {
			return "&";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toLowerCase();
		} else {
			return "#";
		}
	}
}
