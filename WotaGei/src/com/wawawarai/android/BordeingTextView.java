package com.wawawarai.android;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

public class BordeingTextView extends TextView {

	
	private Context _context;
	private FrameLayout f;
	private TextView txtTopBorder, txtMainBottom;
	public BordeingTextView(Context context) {
		super(context);
		_context = context;
		
		f = new FrameLayout(_context);
		txtTopBorder = new TextView(_context);
		txtMainBottom = new TextView(_context);
	}
}
