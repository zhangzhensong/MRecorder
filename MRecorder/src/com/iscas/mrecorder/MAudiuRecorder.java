package com.iscas.mrecorder;

import com.iscas.helper.Helper;
import com.iscas.mrecorder.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;


public class MAudiuRecorder extends Activity {
	/** Called when the activity is first created. */
	
	/**监听对话框里面的button点击事件*/
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				isRecording = false;
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};	
	
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

	Button btnRecord, btnStop, btnExit;
	SeekBar skbVolume;//调节音量
	boolean isRecording = false;//是否录放的标记
	// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	static final int frequency = 16000;
	// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
	// Notice：录音时双声道有问题
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	int recBufSize,playBufSize;
	AudioRecord audioRecord;
	AudioTrack audioTrack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maudiorecorder);
		setTitle(R.string.recorder_activity);
		recBufSize = AudioRecord.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);
		Helper.SetBufferSizeInBytes(recBufSize);
		
		playBufSize=AudioTrack.getMinBufferSize(frequency,
				channelConfiguration, audioEncoding);
		// -----------------------------------------
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
				channelConfiguration, audioEncoding, recBufSize);
		//// 音频获取源
		//private int audioSource = MediaRecorder.AudioSource.VOICE_DOWNLINK;

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
				channelConfiguration, audioEncoding,
				playBufSize, AudioTrack.MODE_STREAM);
		//------------------------------------------
		btnRecord = (Button) this.findViewById(R.id.btnRecord);
		btnRecord.setOnClickListener(new ClickEvent());
		btnStop = (Button) this.findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new ClickEvent());
		btnExit = (Button) this.findViewById(R.id.btnExit);
		btnExit.setOnClickListener(new ClickEvent());
		skbVolume=(SeekBar)this.findViewById(R.id.skbVolume);
		skbVolume.setMax(100);//音量调节的极限
		skbVolume.setProgress(70);//设置seekbar的位置值
		audioTrack.setStereoVolume(0.7f, 0.7f);//设置当前音量大小
		skbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float vol=(float)(seekBar.getProgress())/(float)(seekBar.getMax());
				audioTrack.setStereoVolume(vol, vol);//设置音量
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isRecording = false;
		audioRecord.release();
		audioTrack.release();
		audioRecord = null;
		audioTrack = null;
//		android.os.Process.killProcess(android.os.Process.myPid());
	
	}

	class ClickEvent implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == btnRecord) {
				isRecording = true;
				new RecordPlayThread().start();// 开一条线程边录边放
			} else if (v == btnStop) {
				isRecording = false;
			} else if (v == btnExit) {
				isRecording = false;
//				MAudiuRecorder.this.finish();
				finish();
			}
		}
	}

	class RecordPlayThread extends Thread {
		public void run() {
			try {
				byte[] buffer = new byte[recBufSize];
				audioRecord.startRecording();//开始录制
				audioTrack.play();//开始播放
				
				while (isRecording) {
					//从MIC保存数据到缓冲区
					int bufferReadResult = audioRecord.read(buffer, 0,
							recBufSize);
					
					byte[] tmpBuf = new byte[bufferReadResult];
					System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
					
					//写入数据即播放
					audioTrack.write(tmpBuf, 0, tmpBuf.length);
				}
				audioTrack.stop();
				audioRecord.stop();
			} catch (Throwable t) {
				Toast.makeText(MAudiuRecorder.this, t.getMessage(), 1000);
			}
		}
	};

}
