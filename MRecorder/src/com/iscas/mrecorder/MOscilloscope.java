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
    
    /**�����Ի��������button����¼�*/
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
			case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����
				clsOscilloscope.Stop();
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���
				break;
			default:
				break;
			}
		}
	};	
    
    ClsOscilloscope clsOscilloscope=new ClsOscilloscope();
    
	static final int frequency = 16000;//�ֱ���
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	static final int xMax = 16;//X����С�������ֵ,X���������޴����ײ���ˢ����ʱ
	static final int xMin = 8;//X����С������Сֵ
	static final int yMax = 60;//Y����С�������ֵ
	static final int yMin = 20;//Y����С������Сֵ
	
	int recBufSize;//¼����Сbuffer��С
	AudioRecord audioRecord;
	Paint mPaint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oscillograph);
        //¼�����
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, audioEncoding, recBufSize);
		//����
		btnStart = (Button) this.findViewById(R.id.btnStart);
		btnStart.setOnClickListener(new ClickEvent());
		btnExit = (Button) this.findViewById(R.id.btnExit);
		btnExit.setOnClickListener(new ClickEvent());
		//����ͻ���
		sfv = (SurfaceView) this.findViewById(R.id.SurfaceView01); 
		sfv.setOnTouchListener(new TouchEvent());
        mPaint = new Paint();  
        mPaint.setColor(Color.GREEN);// ����Ϊ��ɫ  
        mPaint.setStrokeWidth(1);// ���û��ʴ�ϸ 
        //ʾ�������
        clsOscilloscope.initOscilloscope(xMax/2, yMax/2, sfv.getHeight()/2);
        
        //���ſؼ���X���������С�ı��ʸ�Щ
		zctlX = (ZoomControls)this.findViewById(R.id.zctlX);
		zctlX.setOnZoomInClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(clsOscilloscope.rateX>xMin)
					clsOscilloscope.rateX--;
				setTitle("X����С"+String.valueOf(clsOscilloscope.rateX)+"��"
						+","+"Y����С"+String.valueOf(clsOscilloscope.rateY)+"��");
			}
		});
		zctlX.setOnZoomOutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(clsOscilloscope.rateX<xMax)
					clsOscilloscope.rateX++;	
				setTitle("X����С"+String.valueOf(clsOscilloscope.rateX)+"��"
						+","+"Y����С"+String.valueOf(clsOscilloscope.rateY)+"��");
			}
		});
		zctlY = (ZoomControls)this.findViewById(R.id.zctlY);
		zctlY.setOnZoomInClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(clsOscilloscope.rateY>yMin)
					clsOscilloscope.rateY--;
				setTitle("X����С"+String.valueOf(clsOscilloscope.rateX)+"��"
						+","+"Y����С"+String.valueOf(clsOscilloscope.rateY)+"��");
			}
		});
		
		zctlY.setOnZoomOutClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(clsOscilloscope.rateY<yMax)
					clsOscilloscope.rateY++;	
				setTitle("X����С"+String.valueOf(clsOscilloscope.rateX)+"��"
						+","+"Y����С"+String.valueOf(clsOscilloscope.rateY)+"��");
			}
		});
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		return super.onKeyDown(keyCode, event);
		
		if (keyCode == KeyEvent.KEYCODE_BACK )
		{
			// �����˳��Ի���
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// ���öԻ������
			isExit.setTitle("ϵͳ��ʾ");
			// ���öԻ�����Ϣ
			isExit.setMessage("ȷ��Ҫ�˳���");
			// ���ѡ��ť��ע�����
			isExit.setButton("ȷ��", listener);
			isExit.setButton2("ȡ��", listener);
			// ��ʾ�Ի���
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
	 * �����¼�����
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
	 * ��������̬���ò���ͼ����
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
