package com.wawawarai.android;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SplashActivity extends Activity implements OnItemSelectedListener {

	private Cursor cursor;
	private SharedPreferences pref;
	private Editor e;
	private MediaPlayer mp;
	private int soundIdx = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		
		Spinner spnSoundList = (Spinner)findViewById(R.id.spnSoundTitle);
		Button btnStart = (Button)findViewById(R.id.btnStart);
		Button btnCreate = (Button)findViewById(R.id.btnCreate);
		Button btnOption = (Button)findViewById(R.id.btnOption);

		Typeface huiji = Typeface.createFromAsset(getAssets(), "HuiFont109.ttf");
		btnStart.setTypeface(huiji);
		btnCreate.setTypeface(huiji);
		btnOption.setTypeface(huiji);

		ArrayAdapter<String> adapter 
				= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
		
		new ArrayList<File>();
		
		ContentResolver cr = getApplicationContext().getContentResolver();
		cursor = cr.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,  //データの種類
				new String[]{
        				MediaStore.Audio.Media.ALBUM ,
        				MediaStore.Audio.Media.ARTIST ,
        				MediaStore.Audio.Media.TITLE,
        				MediaStore.Audio.Media._ID
				} ,//取得する
				null , //フィルター条件 nullはフィルタリング無し 
				null , //フィルター用のパラメータ
				null   //並べ替え
		);
		cursor.moveToFirst();
		do{
			adapter.add(cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Media.TITLE)));
		}while(cursor.moveToNext());

		spnSoundList.setAdapter(adapter);
		spnSoundList.setOnItemSelectedListener(this);
		pref = getSharedPreferences("sound_resource", MODE_PRIVATE);
		String title = pref.getString("slected", "");
		if (title == "") {
			spnSoundList.setPrompt("サウンドを選択してください。");
		}
		
		ImageButtonAnimation btnAnime;
		btnAnime = new ImageButtonAnimation().setImage(
				R.drawable.sutaato_s, R.drawable.sutaato_s_down);
		btnStart.setOnTouchListener(btnAnime);
		btnAnime = new ImageButtonAnimation().setImage(
				R.drawable.kurieto_ss, R.drawable.kurieto_ss_down);
		btnCreate.setOnTouchListener(btnAnime);
		btnAnime = new ImageButtonAnimation().setImage(
				R.drawable.wopusyo_ss, R.drawable.wopusyo_ss_down);
		btnOption.setOnTouchListener(btnAnime);

	
	}

	@Override
	protected void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		mp.stop();
		super.onStop();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		cursor.moveToPosition(arg2);
		String soundId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
		mp = new MediaPlayer();
		try {
			mp.setDataSource(this, Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, soundId));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mp.start();

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	public class ImageButtonAnimation implements OnTouchListener {
		
		private int up_drawable, down_drawable;
		
		public ImageButtonAnimation setImage(int up_drawable, int down_drawable) {
			this.up_drawable = up_drawable;
			this.down_drawable = down_drawable;
			return this;
		}
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO 自動生成されたメソッド・スタブ
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(down_drawable);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.setBackgroundResource(up_drawable);
				
			}
			return false;
		}
	}

}
