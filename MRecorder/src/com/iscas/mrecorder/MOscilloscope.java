package com.iscas.mrecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ZoomControls;

public class MOscilloscope extends Activity {
	/** Called when the activity is first created. */
	Button btnStart,btnExit;
	SurfaceView sfv;
    ZoomControls zctlX,zctlY;
    
    /**监听对话框里面的button点击事件*/
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				clsOscilloscope.Stop();
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};	
    
    ClsOscilloscope clsOscilloscope=new ClsOscilloscope();
    
	static final int frequency = 16000;//分辨率
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	static final int xMax = 16;//X轴缩小比例最大值,X轴数据量巨大，容易产生刷新延时
	static final int xMin = 8;//X轴缩小比例最小值
	static final int yMax = 60;//Y轴缩小比例最大值
	static final int yMin = 20;//Y轴缩小比例最小值
	
	int recBufSize;//录音最小buffer大小
	AudioRecord audioRecord;
	Paint mPaint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oscillograph);
        //录音组件
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, audioEncoding, recBufSize);
		//按键
		btnStart = (Button) this.findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new ClickEvent());
		btnExit = (Button) this.findViewById(R.id.btnExit);
		btnExit.setOnClickListener(new ClickEvent());
		//画板和画笔
		sfv = (SurfaceView) this.findViewById(R.id.SurfaceView01); 
		sfv.setOnTouchListener(new TouchEvent());
        mPaint = new Paint();  
        mPaint.setColor(Color.GREEN);// 画笔为绿色  
        mPaint.setStrokeWidth(1);// 设置画笔粗细 
        //示波器类库
        clsOscilloscope.initOscilloscope(xMax/2, yMax/2, sfv.getHeight()/2);
        
        //缩放控件，X轴的数据缩小的比率高些
		zctlX = (ZoomControls)this.findViewById(R.id.zctlX);
		zctlX.setOnZoomInClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(clsOscilloscope.rateX>xMin)
					clsOscilloscope.rateX--;
				setTitle("X轴缩小"+String.valueOf(clsOscilloscope.rateX)+"倍"
						+","+"Y轴缩小"+String.valueOf(clsOscilloscope.rateY)+"倍");
			}
		});
		zctlX.setOnZoomOutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(clsOscilloscope.rateX<xMax)
					clsOscilloscope.rateX++;	
				setTitle("X轴缩小"+String.valueOf(clsOscilloscope.rateX)+"倍"
						+","+"Y轴缩小"+String.valueOf(clsOscilloscope.rateY)+"倍");
			}
		});
		zctlY = (ZoomControls)this.findViewById(R.id.zctlY);
		zctlY.setOnZoomInClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(clsOscilloscope.rateY>yMin)
					clsOscilloscope.rateY--;
				setTitle("X轴缩小"+String.valueOf(clsOscilloscope.rateX)+"倍"
						+","+"Y轴缩小"+String.valueOf(clsOscilloscope.rateY)+"倍");
			}
		});
		
		zctlY.setOnZoomOutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(clsOscilloscope.rateY<yMax)
					clsOscilloscope.rateY++;	
				setTitle("X轴缩小"+String.valueOf(clsOscilloscope.rateX)+"倍"
						+","+"Y轴缩小"+String.valueOf(clsOscilloscope.rateY)+"倍");
			}
		});
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		return super.onKeyDown(keyCode, event);
		
		if (keyCode == KeyEvent.KEYCODE_BACK )
		{
			// 创建退出对话框
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// 设置对话框标题
			isExit.setTitle("系统提示");
			// 设置对话框消息
			isExit.setMessage("确定要退出吗");
			// 添加选择按钮并注册监听
			isExit.setButton("确定", listener);
			isExit.setButton2("取消", listener);
			// 显示对话框
			isExit.show();

		}
		
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		android.os.Process.killProcess(android.os.Process.myPid());
		clsOscilloscope.Stop();
		audioRecord.release();
		audioRecord = null;
		finish();
	}
	
	/**
	 * 按键事件处理
	 * @author GV
	 *
	 */
	class ClickEvent implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (v == btnStart) {
				clsOscilloscope.baseLine=sfv.getHeight()/2;
				clsOscilloscope.Start(audioRecord,recBufSize,sfv,mPaint);
			} else if (v == btnExit) {
				clsOscilloscope.Stop();
			}
		}
	}
	/**
	 * 触摸屏动态设置波形图基线
	 * @author GV
	 *
	 */
	class TouchEvent implements OnTouchListener{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			clsOscilloscope.baseLine=(int)event.getY();
			return true;
		}
		
	}
}
