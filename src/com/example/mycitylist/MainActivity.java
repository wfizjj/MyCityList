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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private BaseAdapter adapter;
	private ListView listView;
	private TextView overlay; // 对话框首字母textView
	private MyLetterListView letterListView; // A-Z listView
	private HashMap<String, Integer> alphaIndexer; // 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;//存放存在的汉语拼音首字母
	
	private ArrayList<City> allCityLists; //所有城市列表
	private ArrayList<City> cityLists;//城市列表
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.list_view);

		letterListView = (MyLetterListView) findViewById(R.id.myLetterListView);

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
	
	private void setAdapter(List<City> list){
		adapter = new ListAdapter(this,list);
		listView.setAdapter(adapter);
	}

	public class ListAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		private List<City> list;
		final int VIEW_TYPE = 3;
		
		public ListAdapter(Context context,List<City> list){
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String,Integer>();
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	
	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(String s) {
			// TODO Auto-generated method stub

		}

	}
	
	
	private String getAlpha(String str) {
		if (str == null){
			return "#";
		}
		if(str.equals("-")){
			return "&";
		}
		if (str.trim().length()==0){
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
