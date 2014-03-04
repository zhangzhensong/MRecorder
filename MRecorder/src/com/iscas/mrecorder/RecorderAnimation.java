package com.iscas.mrecorder;

import android.os.Bundle;
import android.view.Menu;

import com.jeremyfeinstein.slidingmenu.example.BaseActivity;
import com.iscas.mrecorder.R;
import com.jeremyfeinstein.slidingmenu.example.SampleListFragment;
import com.iscas.mrecorder.R.id;
import com.iscas.mrecorder.R.layout;
import com.iscas.mrecorder.R.menu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public abstract class RecorderAnimation extends RecorderBase {
	
	private CanvasTransformer mTransformer;
	
	public RecorderAnimation(int titleRes, CanvasTransformer transformer) {
		super(titleRes);
		mTransformer = transformer;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, new SampleListFragment())
		.commit();
		
		SlidingMenu sm = getSlidingMenu();
		setSlidingActionBarEnabled(true);
		sm.setBehindScrollScale(0.0f);
		sm.setBehindCanvasTransformer(mTransformer);
	}

}
