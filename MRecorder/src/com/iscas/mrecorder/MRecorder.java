package com.iscas.mrecorder;

import android.graphics.Canvas;

import com.iscas.mrecorder.R;
import com.iscas.mrecorder.R.string;
import com.jeremyfeinstein.slidingmenu.example.anim.CustomAnimation;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class MRecorder extends RecorderAnimation {

	public MRecorder() {
		super(R.string.recorder_activity, new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				canvas.scale(percentOpen, 1, 0, 0);
			}			
		});
	}

}
