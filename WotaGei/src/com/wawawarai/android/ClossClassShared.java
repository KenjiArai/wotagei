package com.wawawarai.android;

import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ClossClassShared implements SharedPreferences {
	
	private Context context;
	private static ClossClassShared _instance;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private String fileList;

	private ClossClassShared(Context context) {
		sharedPreferences = _instance.getSharedPreferences("asd", android.content.Context.MODE_PRIVATE);
	}

	public SharedPreferences getSharedPreferences(String name, int mode) {
		sharedPreferences = _instance.getSharedPreferences(name, mode);
		return sharedPreferences;
	}

	public static ClossClassShared getInstance(Context context) {
		if (_instance == null) {
				_instance = new ClossClassShared(context);
		}
		return _instance;
	}
	
	public void setFileList(String fileList) {
		editor = sharedPreferences.edit();
		editor.putString("FILE_LIST", fileList);
		editor.commit();
		this.fileList = fileList;
	}
	
	public String getFileList() {
		return sharedPreferences.getString("FILE_LIST", fileList);
	}

	@Override
	public boolean contains(String key) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public Editor edit() {
		// TODO 自動生成されたメソッド・スタブ
		Editor e = _instance.sharedPreferences.edit();
		return e;
	}

	@Override
	public Map<String, ?> getAll() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public float getFloat(String key, float defValue) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public int getInt(String key, int defValue) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public long getLong(String key, long defValue) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public String getString(String key, String defValue) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Set<String> getStringSet(String arg0, Set<String> arg1) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Override
	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}

