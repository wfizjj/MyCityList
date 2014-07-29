package com.example.mycitylist;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// 用户数据库文件的版本
	private static final int DB_VERSION = 1;
	// 数据库文件目标存放路径为系统默认位置，cn.arthur.examples 是你的包名
	private static String DB_PATH = "/data/data/com.example.mycitylist/databases/";
	/*
	 * //如果你想把数据库文件存放在SD卡的话 private static String DB_PATH =
	 * android.os.Environment.getExternalStorageDirectory().getAbsolutePath() +
	 * "/arthurcn/drivertest/packfiles/";
	 */
	private static String DB_NAME = "meituan_cities.db";
	private static String ASSETS_NAME = "meituan_cities.db";
	private SQLiteDatabase myDataBase = null;
	private final Context myContext;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		this.myContext = context;
	}

	public DBHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DBHelper(Context context, String name) {
		this(context, name, DB_VERSION);
	}

	public DBHelper(Context context) {
		this(context, DB_PATH + DB_NAME);
	}

	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			//数据库已存在
		} else {
			//创建数据库
			try {
				File dir = new File(DB_PATH);
				if (!dir.exists()){
					dir.mkdirs();
				}
				File dbf = new File(DB_PATH + DB_NAME);
				if (dbf.exists()) {
					dbf.delete();
				}
				SQLiteDatabase.openOrCreateDatabase(dbf, null);
				// 复制asseets中的db文件到DB_PATH下
				copyDataBase();
			} catch (IOException e){
				throw new Error("数据库创建失败！");
			}
		}
	}

	// 检查数据库是否有效
	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		String myPath = DB_PATH + DB_NAME;
		try {
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// database does't exist yet.
		}
		if (checkDB != null) {
			checkDB.close();
		}
		return checkDB != null ? true : false;
	}
	
	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException{
		InputStream myInput = myContext.getAssets().open(ASSETS_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length=myInput.read(buffer)) > 0){
			myOutput.write(buffer,0,length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void close() {
		if (myDataBase != null) {
			myDataBase.close();
		}
		super.close();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
