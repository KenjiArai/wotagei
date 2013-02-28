package com.wawawarai.android;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class MainActivity extends Activity implements OnClickListener {
	
	final static int UNSTARTED = -1;	// 準備中は、ボタン無効
	final static int ON_READY = 5;	// 準備完了で、ボタン有効、プレイに更新
	final static int PLAY_END = 0;	// 再生終了で、プレイに更新
	final static int PLAYING = 1;	// 再生中は、ポーズに更新
	final static int PAUSE = 2;		// ポーズ中は、プレイに更新
	final static int BUFFERING = 3;	// バッッファ中は、ポーズに更新
	
	private LinearLayout leftMixButtonContainer, rightMixButtonContainer;
	private FrameLayout flMixData;
	private static WebView wbScreen;
	private TextView txtMix, txtMixture;
	private ImageButton btnPlay, btnSave;
	private String callMix;
	private float mCurrentTime = 0, prevCurrentTime = 0;
	private int widthPx;
	private int heightPx;
	private int playerStatus = UNSTARTED;
	private int[] playBtnId = {0, android.R.drawable.ic_media_play, android.R.drawable.ic_media_pause};
	private int playBtnState = PLAYING;
	private int totalTime; 
	private HashMap<String, Runnable> jsResponder = new HashMap<String, Runnable>();
	private String fileName;
	private EditText txt;
	private OnTouchListener onTouchMixButton;

//	// The gesture threshold expressed in dip  
//	private static final float GESTURE_THRESHOLD_DIP = 16.0f;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wotagei_create);
		
		leftMixButtonContainer = (LinearLayout)findViewById(R.id.leftMixButtonContainer);
		rightMixButtonContainer = (LinearLayout)findViewById(R.id.rightMixButtonContainer);
		onTouchMixButton = new OnTouchMixButton();
		
		String[] leftMixString = {"ウリャ", "かなこぉ⬆⬆", "あーりん", "れにちゃん", "Ｚ"};
		MixButtonCrection left_mb = new MixButtonCrection(this, leftMixString);
		FrameLayout[] left_f = left_mb.getMixButtonContainer();
		for (int i = 0; i < left_f.length; i++) {
			left_f[i].setOnTouchListener(onTouchMixButton);
			leftMixButtonContainer.addView(left_f[i]);
		}
		Log.d("leftMixStringOnCreate", ""+left_f[0].getWidth());
		String[] rightMixString = {"オイ", "しおり", "ももか", "ふぅー！", "あ"};
		MixButtonCrection right_mb = new MixButtonCrection(this, rightMixString);
		FrameLayout[] right_f = right_mb.getMixButtonContainer();
		for (int i = 0; i < right_f.length; i++) {
			right_f[i].setOnTouchListener(onTouchMixButton);
			rightMixButtonContainer.addView(right_f[i]);
		}
		
		wbScreen = (WebView)findViewById(R.id.wbScreen);
//		flMixData = (FrameLayout)findViewById(R.id.flMixDataContainer);
		btnPlay  = (ImageButton)findViewById(R.id.btnPlay);
		btnPlay.setImageResource(android.R.drawable.ic_media_play);
		btnSave = (ImageButton)findViewById(R.id.btnSaveImg);
		txtMix = (TextView)findViewById(R.id.mixStringBottom);
        txtMix.setShadowLayer(10.0f, 10.0f, 10.0f, Color.BLACK);
        Typeface yunaji = Typeface.createFromAsset(getAssets(), "YuNaFont.ttf");
		txtMix.setTypeface(yunaji);
		txtMix.setTextColor(Color.WHITE);

		btnPlay.setEnabled(false);
		btnPlay.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		
		//リンクをタップしたときに標準ブラウザを起動させない
		wbScreen.setWebViewClient(new WebViewClient());
		//JavaScriptを有効に
		wbScreen.getSettings().setJavaScriptEnabled(true);
		wbScreen.getSettings().setPluginsEnabled(true);
		wbScreen.setVerticalScrollbarOverlay(true);
		wbScreen.addJavascriptInterface(this, "droid");
		wbScreen.setBackgroundColor(0);
		
		final DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
		final float resolution = metrics.density;
		widthPx = metrics.widthPixels;
		heightPx = metrics.heightPixels;
		
		final int webViewHeight = heightPx *3/5;
		int webViewWidth = webViewHeight *16/9;
		
		LayoutParams webViewSize = wbScreen.getLayoutParams();
		webViewSize.width = webViewWidth;
		webViewSize.height = webViewHeight;
		wbScreen.setLayoutParams(webViewSize);

		int screenWidth = (int)((float)webViewWidth / resolution);
		int screenHeight = (int)((float)webViewHeight / resolution);
		
		txtMix.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {			
			@Override
			public void onGlobalLayout() {
				// TODO 自動生成されたメソッド・スタブ
				LinearLayout.LayoutParams layoutMargin = 
						new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutMargin.setMargins(0, -(heightPx - wbScreen.getHeight() + txtMix.getHeight()), 0, 0);
				txtMix.setLayoutParams(layoutMargin);
				
//				int mixDataMarginValue = leftMixButtonContainer.getHeight()- wbScreen.getHeight();
//				FrameLayout.LayoutParams mixdataMagin = 
//						new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//				mixdataMagin.setMargins(0, -(mixDataMarginValue < 0? 0: mixDataMarginValue), 0, 0);
			}
		});
		txtMix.setText("");
		
		String movieTag = "V7sxfNSNOZk";

		wbScreen.loadUrl(String.format(
				"http://a2mixture.heteml.jp/wotagei/html5player.php?tag=%s&width=%d&height=%d",
				movieTag, screenWidth, screenHeight));
		wbScreen.showContextMenu();
		
		jsResponder = new HashMap<String, Runnable>();
		jsResponder.put("TouchDownMixButton", TouchDownMixButton);
		jsResponder.put("ClickPlayButton", ClickPlayButton);
		jsResponder.put("ClickPauseButton", ClickPauseButton);		
	}
	
	public class OnTouchMixButton implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO 自動生成されたメソッド・スタブ
		int historySize = event.getHistorySize();   //ACTION_MOVEイベントの数
	    int pointerCount = event.getPointerCount();

			FrameLayout f = (FrameLayout)v;
			ImageView img = (ImageView)f.getChildAt(0);
			TextView txt = (TextView)f.getChildAt(1);
			int getaction = event.getAction();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				boolean nulStr = (txt.getText() == "") || (txt.getText().toString().length() <= 0);
				callMix = nulStr? "": "＼" + txt.getText() + "／";
				Log.d("touchDownMixButton", callMix);
				wbScreen.loadUrl("javascript:getCurrentTime('TouchDownMixButton')");
				// Methods that run from the execution result of the previous step, wanted to write here.
				// Runnable TouchDownMixButton = ...  is out of the method described in this.			
				break;
			}
			
			return true;
		}
	}

	private class MixVoice{
		String voice;
		float time;
	}
	private ArrayList<MixVoice> mixPhrase = new ArrayList<MainActivity.MixVoice>();
	// TouchDownMixButton フィールド
	private Runnable TouchDownMixButton = new Runnable() {
		@Override
		public void run() {
			Log.d("returnJsTouchDownMixButton", callMix);
			txtMix.setText(callMix);
			if (mCurrentTime > 0) {
				if (prevCurrentTime == mCurrentTime) {
					mixPhrase.remove(mixPhrase.size()-1);
				}
				MixVoice mv = new MixVoice();
				mv.voice = callMix;
				mv.time = mCurrentTime;
				mixPhrase.add(mv);
				float currentTime = mixPhrase.get(mixPhrase.size()-1).time;
				String mixString = mixPhrase.get(mixPhrase.size()-1).voice;
				Log.d("returnJsTouchDownMixButton", String.format("%d, %f, %s", mixPhrase.size()-1, currentTime, mixString));
				prevCurrentTime = mCurrentTime;
			}
		}
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPlay:
			switch (playerStatus) {
			case PLAYING:
			case BUFFERING:
				playerStatus = PAUSE;
				btnPlay.setImageResource(playBtnId[PLAYING]);
				Log.d("playerPause", "");
				wbScreen.loadUrl("javascript:pause()");
				wbScreen.loadUrl("javascript:getCurrentTime('ClickPauseButton')");
				// Methods that run from the execution result of the previous step, wanted to write here.
				// Runnable ClickPause = ...  is out of the method described in this.
				break;
				
			case PLAY_END:
				// 再生が終了後にプレイを押した時、ストップ(頭出し)をしてから再生
				
				wbScreen.loadUrl("javascript:stop()");
				
			case UNSTARTED:
			case ON_READY:
			case PAUSE:
				playerStatus = PLAYING;
				btnPlay.setImageResource(playBtnId[PAUSE]);

				wbScreen.loadUrl("javascript:getCurrentTime('ClickPlayButton')");
				// Methods that run from the execution result of the previous step, wanted to write here.
				// Runnable ClickPlayButton = ...  is out of the method described in this.
				
				wbScreen.loadUrl("javascript:play()");
				break;
			default:
				break;
			}
			break;
		case R.id.btnSaveImg:		
			fileName = null;
			fileName = ""+ System.currentTimeMillis() + "__Mix__.xml";
			StringBuilder putString = new StringBuilder();
			putString.append("<mixPhrase>");
			float ct = 0.0f;
			String ms = null;
			for (int i = 0; i < mixPhrase.size(); i++ ) { 
				ct = mixPhrase.get(i).time;
				ms = mixPhrase.get(i).voice;
				putString.append("<mixCall>");
				putString.append(String.format("<currentTime>%s</currentTime>", ct));
				putString.append(String.format("<mixString>%s</mixString>", ms));
				putString.append("</mixCall>");
			}
			putString.append("</mixPhrase>");
			try {
				FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
				OutputStreamWriter writer = new OutputStreamWriter(fos);
				writer.write(putString.toString());
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			LayoutInflater li = LayoutInflater.from(MainActivity.this);
			View view = li.inflate(R.layout.save_dialog, null);
			txt = (EditText)view.findViewById(R.id.txtFileName);
			txt.setOnEditorActionListener(new SoftKeyBoardReturnToHidden(getApplicationContext()));
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setView(view);
			builder.setTitle("Mixのタイトルを設定");
			builder.setCancelable(true);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					txt.selectAll();
					fileName = txt.getText().toString();

					dialog.dismiss();
				}
			});
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			
			SCPConnector scp = new SCPConnector(this, fileName);
			scp.execute();
		}
	}
	
	private static class SCPConnector extends AsyncTask<Void, Void, Void> {

		private String _fileName = null;
		
		public SCPConnector(Context context, String fileName) {
			super();
			_fileName = fileName;
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO 自動生成されたメソッド・スタブ
			Connection conn = new Connection("ssh179.heteml.jp", 2222);
			try {
				conn.connect();
				conn.authenticateWithPassword("a2mixture", "kg1979ww");
				SCPClient scp = conn.createSCPClient();
				scp.put("/data/data/com.wawawarai.android/files/"+ _fileName, "~/web/wotagei/mixFiles/", "0604");
			} catch (IOException e) {
				Log.d("IOException",e.getMessage());
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			return null;
		}
	}
	
	// ClickPlaying フィールド
	private Runnable ClickPlaying = new Runnable() {
		@Override
		public void run() {
			
		}
	};
	// ClickPlaying フィールド
	private Runnable ClickPlayButton = new Runnable() {
		@Override
		public void run() {
			Log.d("ClickPlayButton", "playTime = "+ mCurrentTime);
		}
	};
	// ClickPlaying フィールド
	private Runnable ClickPauseButton = new Runnable() {
		@Override
		public void run() {
			Log.d("ClickPauseButton", "PauseTime = "+ mCurrentTime);
		}
	};

	public void onCatchPlayerReady() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				btnPlay.setEnabled(true);
			}
		});
	}
	public void onCatchCurrentTimeRequest (float currentTime, String runnablesKey) {
		mCurrentTime = currentTime;
		Log.d("onCatchCurrenttimeRequest", "runnablesKey = "+ runnablesKey);
		runOnUiThread(jsResponder.get(runnablesKey));
	}

	public void onCatchPlayerStateChange (int status) {		
		playerStatus = status;
		Log.d("returnJsOnChatchPlayerStateChange","" +status);
		switch (status) {
		case PLAYING:
		case BUFFERING:
			playBtnState = PAUSE;
			break;
		default:
			playBtnState = PLAYING;
			break;
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO 自動生成されたメソッド・スタブ
				btnPlay.setImageResource(playBtnId[playBtnState]);
			}
		});
	}
	@Override
	protected void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
		wbScreen.loadUrl("javascript:stop()");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public class scenery {
		public static final String m = "＼ウリャオイ／";

		public static final String z = 
				"幾多の困難を乗り越え 5人となったももいろクローバー " +
						"突然渡された変身ベルトに 少女たちは何を思う " +
						"第四話 「Z伝説！ 終わりなき革命」 " +
						"RED!!! " +
						"はーちはち でーこでこっぱち " +
						"えーく えくぼは 恋の落とし穴　" +
						"YELLOW!!! " +
						"泣き虫 甘えん坊 " +
						"食いしん坊 みんなの妹ですっ!!　" +
						"PINK!!! " +
						"あーりん☆ Yes!!あーりん☆ " +
						"ももクロちゃんのアイドルなのだ " +
						"GREEN!!! " +
						"チビだからってなめんな! " +
						"歌もダンスも誰にも負けないぜ! " +
						"PURPLE!!! " +
						"ぴぽー ぷぽー ぺぽー ぱーぽー " +
						"ぴーぽ ぱーぽ ぴぽ びりびり 感電!! " +
						"わたしたち 泣いてる人に 何が出来るだろう " +
						"それは 力いっぱい 歌って踊ること！ " +
						"よっしゃ やんぜー!! " +
						"われらはアイドル! " +
						"週末ヒロイン ももクロ! " +
						"こぶし握れ! 涙ぶちのめすぜ " +
						"やまない雨なんかない 光を信じて " +
						"強く 気高く 楽しく うるわしく 歌い続けよう!! " +
						"負けないよ! たちあがれ! 大丈夫! 弱くない! " +
						"絶対あきらめない WE ARE ももいろクローバー Z!!!!!!!!!!!!!!! " +

						"PURPLE!!! やりきれない時には  " +
						"GREEN!!! 私たちに任せてね  " +
						"BLUE!! あ… " +
						"PINK!!! おバカなことばっかだけど  " +
						"YELLOW!!! 笑顔をキミにあげるよ  " +
						"RED!!! 会いに来てね 会いに行くよ " +
						"いつだって味方だから　 " +
						"ひとりでも ひとりぼっちじゃない 忘れないでいて " +
						"いつも キミのことを想って歌うから " +
						"ワチャー " +
						"どんなに辛くて 長い戦いとしても " +
						"信じ続けりゃ いつか叶うんだ " +
						"明けない 夜なんかない 朝日は昇る！ " +
						"未来信じて ひたすらあきらめず 今を駆け抜けろ！ " +
						"大好きだよ ほんとだよ 信じてよね はらへったー " +
						"もっと強くなれる WE ARE ももいろクローバー Z!!!!!!!!!!!!!!! " +
						"悲しいことなど 一生続かないから!! " +
						"われらはアイドル! " +
						"週末ヒロイン ももクロ!! " +
						"こぶし握れ! 涙ぶちのめすぜ " +
						"やまない雨なんかない 光を信じて " +
						"強く 気高く 楽しく うるわしく 歌い続けよう!! " +
						"負けないし！STAND UP！ 大丈夫！弱くない！ " +
						"絶対あきらめない WE ARE ももいろクローバー Z!!!!!!!!! ";
	}
}
