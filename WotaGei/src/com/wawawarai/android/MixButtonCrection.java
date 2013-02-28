package com.wawawarai.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MixButtonCrection {
	
	private static Context _context;
	private static FrameLayout[] _mixButtonContainer;
	private static ImageView[] _mixButtonFrame;
	private static TextView[] _mixButtonString;
	
	public MixButtonCrection(Context context, String[] mixString) {
		_context = context;
		LayoutInflater inflater =
				(LayoutInflater)_context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_mixButtonContainer = new FrameLayout[mixString.length];
		_mixButtonFrame = new ImageView[mixString.length];
		_mixButtonString =  new TextView[mixString.length];
        Typeface yunaji = Typeface.createFromAsset(_context.getAssets(), "YuNaFont.ttf");
		for (int i = 0; i < mixString.length; i++) {
			_mixButtonContainer[i] = (FrameLayout)inflater.inflate(R.layout.mix_button, null);
			_mixButtonFrame[i] = (ImageView)_mixButtonContainer[i].findViewById(R.id.mixButtonFrame);
			_mixButtonString[i] = (TextView)_mixButtonContainer[i].findViewById(R.id.mixButtonString);
			_mixButtonString[i].setText(mixString[i]);
//			_mixButtonString[i].setTextColor(Color.GRAY);
	        _mixButtonString[i].setShadowLayer(1, 2, 2, Color.WHITE);
			_mixButtonString[i].setTypeface(yunaji);
			float fontSize = mixString[i].length() > 4? 0.7f : 1;
			fontSize =mixString[i].length() > 7? 0.5f : fontSize;
			_mixButtonString[i].setTextScaleX(fontSize);
		}
	}
	
	public FrameLayout[] getMixButtonContainer() {
		return _mixButtonContainer;
	}
	public ImageView[] getMixButtonFrame() {
		return _mixButtonFrame;
	}
	public TextView[] getMixButtonString() {
		return _mixButtonString;
	}
	public void setText(Character[] mixString) {
		for (int i = 0; i < mixString.length; i++ ) {
			_mixButtonString[i].setText(mixString[i]);
		}
	}
}
