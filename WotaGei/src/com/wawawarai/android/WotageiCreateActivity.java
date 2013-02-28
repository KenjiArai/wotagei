package com.wawawarai.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class WotageiCreateActivity extends Activity implements OnClickListener, OnTouchListener {
		
		final static int LOADING = -1;	// 準備中は、ボタン無効
		final static int ON_READY = 5;	// 準備完了で、ボタン有効、プレイに更新
		final static int PLAY_END = 0;	// 再生終了で、プレイに更新
		final static int PLAYING = 1;	// 再生中は、ポーズに更新
		final static int PAUSE = 2;		// ポーズ中は、プレイに更新
		final static int BUFFERING = 3;	// バッッファ中は、ポーズに更新
		
		private WebView wbScreen;
		private TextView txtMix, txtMixture;
		private Button btnUrya, btnOi;
		private ImageButton btnPlay;
		private String callMix;
		private float mCurrentTime = 0;
		private int playerStatus;
		private int[] playBtnId = {0, android.R.drawable.ic_media_play, android.R.drawable.ic_media_pause};
		private Runnable[] runnables = new Runnable[RunnableKeys.num.ordinal()];
		private boolean btnEnable = false;
		private int playBtnState = PLAYING;
//		private FrameLayout fLayout;

//		// The gesture threshold expressed in dip  
//		private static final float GESTURE_THRESHOLD_DIP = 16.0f;  
		
		enum RunnableKeys {
			TouchDownMixButton,
			ClickPlaying,
			num;
		};
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.wotagei_create);
			
			final DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
			final float resolution = metrics.density;
			final int widthPx = metrics.widthPixels;
			final int heightPx = metrics.heightPixels;
			
			int webViewWidth = widthPx *5/7;
			int webViewHeight = webViewWidth *9/16;
			LayoutParams params = 
					new LayoutParams(webViewWidth, webViewHeight);

			wbScreen = (WebView)findViewById(R.id.wbScreen);
			wbScreen.setLayoutParams(params);
			txtMix = (TextView)findViewById(R.id.txtFileName);
//			fLayout = new FrameLayout(this);
//			FrameLayout.LayoutParams fLayoutParams = new FrameLayout.LayoutParams(
//					FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
//			fLayoutParams.gravity = Gravity.BOTTOM;
//			fLayout.setLayoutParams(fLayoutParams);
//			
//			txtMixture = new TextView(this);
	//	
//			txtMixture.setText("!!!!!");
//			LayoutParams inScreenParmas = 
//					new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			txtMixture.setTextColor(Color.YELLOW);
//			fLayout.addView(txtMixture);
//			txtMixture.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
//			txtMixture.setLayoutParams(inScreenParmas);
//			btnUrya = (Button)findViewById(R.id.btnUrya);
//			btnOi = (Button)findViewById(R.id.btnOi);
			btnPlay  = (ImageButton)findViewById(R.id.btnPlay);
			btnPlay.setImageResource(android.R.drawable.ic_media_play);
			btnPlay.setEnabled(false);
			btnUrya.setOnClickListener(this);
			btnOi.setOnClickListener(this);
			btnUrya.setOnTouchListener(this);
			btnOi.setOnTouchListener(this);
			btnPlay.setOnClickListener(this);
			
			//リンクをタップしたときに標準ブラウザを起動させない
			wbScreen.setWebViewClient(new WebViewClient());
			//JavaScriptを有効に
			wbScreen.getSettings().setJavaScriptEnabled(true);
			wbScreen.getSettings().setPluginsEnabled(true);
			wbScreen.setVerticalScrollbarOverlay(true);
			wbScreen.addJavascriptInterface(this, "droid");
			wbScreen.setBackgroundColor(0);
	        Typeface yunaji = Typeface.createFromAsset(getAssets(), "YuNaFont.ttf");
			txtMix.setTypeface(yunaji);
			txtMix.setTextColor(Color.BLACK);
			btnUrya.setTypeface(yunaji);
			btnOi.setTypeface(yunaji);
			
//			wbScreen.setLayoutParams(new LayoutParams(webViewWidth, webViewHeight));
			
			int screenWidth = (int)((float)webViewWidth / resolution);
			int screenHeight = (int)((float)webViewHeight / resolution);
			
			String movieTag = "V7sxfNSNOZk";
	        
			wbScreen.loadUrl("http://www48.atpages.jp/a2mixture/wotagei/movieInit.php?tag=" +
								movieTag +"&width=" + screenWidth + "&height=" + screenHeight );
			wbScreen.showContextMenu();
			
			runnables[RunnableKeys.TouchDownMixButton.ordinal()] = TouchDownMixButton;
			runnables[RunnableKeys.ClickPlaying.ordinal()] = ClickPlaying;
		}
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO 自動生成されたメソッド・スタブ
			if(event.getAction() == MotionEvent.ACTION_DOWN) {
//				switch (v.getId()) {
//				case R.id.btnUrya:
//					callMix = "＼ウリャ／";
//					break;
//				case R.id.btnOi:
//					callMix = "＼オイ／";
//				default:
//					break;
//				}
				wbScreen.loadUrl("javascript:getCurrentTime("+ RunnableKeys.TouchDownMixButton.ordinal() + ")");
				// Methods that run from the execution result of the previous step, wanted to write here.
				// Runnable TouchDownMixButton = ...  is out of the method described in this.
			} 
			return false;
		}
		// TouchDownMixButton フィールド
		private Runnable TouchDownMixButton  = new Runnable() {
			@Override
			public void run() {
				txtMix.setText(callMix + ", "+mCurrentTime);
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
					wbScreen.loadUrl("javascript:pause()");
					break;
				case ON_READY:
				case PLAY_END:
				case PAUSE:
					playerStatus = PLAYING;
					btnPlay.setImageResource(playBtnId[PAUSE]);
					wbScreen.loadUrl("javascript:getCurrentTime(" + RunnableKeys.ClickPlaying.ordinal() + ")");
					// Methods that run from the execution result of the previous step, wanted to write here.
					// Runnable ClickPlaying = ...  is out of the method described in this.
					wbScreen.loadUrl("javascript:play()");
					break;
				default:
					break;
				}
			}
		}
		// ClickPlaying フィールド
		private Runnable ClickPlaying = new Runnable() {
			@Override
			public void run() {
				
			}
		};

		public void onCatchCurrentTimeRequest (float currentTime,int runnablesKey) {
			mCurrentTime = currentTime;
			Log.d("onCatchCurrenttimeReauest", "runnablesKey = "+ runnablesKey);
			runOnUiThread(runnables[runnablesKey]);				
		}

		public void getPlayerStateRequest (int status, int runnablesKey) {
			playerStatus = status;
			Log.d("onCatchCurrenttimeReauest", "runnablesKey = "+ runnablesKey);
			runOnUiThread(runnables[runnablesKey]);
		}
		public void onCatchPlayerStateChange (int status) {		
			playerStatus = status;

			switch (status) {
			case LOADING:
				btnEnable = false;
				break;
			case ON_READY:
				btnEnable = true;
				playBtnState = PLAYING;
				break;
			case PLAYING:
			case BUFFERING:
				playBtnState = PAUSE;
				break;
			case PLAY_END:
			case PAUSE:
				playBtnState = PLAYING;
				break;

			default:
				break;
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO 自動生成されたメソッド・スタブ
					btnPlay.setEnabled(btnEnable);
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
