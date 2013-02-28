package com.wawawarai.android;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SoftKeyBoardReturnToHidden implements OnEditorActionListener {
	
	private Context _context;
	
	public SoftKeyBoardReturnToHidden(Context context) {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
		_context = context;
		
	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO 自動生成されたメソッド・スタブ
        Log.d("onEditorAction", "actionId = " + actionId + " event = " + (event == null ? "null" : event));
        if(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if(event.getAction() == KeyEvent.ACTION_UP) {
                Log.d("onEditorAction", "check");
                // ソフトキーボードを隠す
                ((InputMethodManager)_context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            return true;
        }
        return false;	
    }

}
