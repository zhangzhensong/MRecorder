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
	
	/**�����Ի��������button����¼�*/
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which)
		{
			switch (which)
			{
			case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����
				isRecording = false;
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���
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

	Button btnRecord, btnStop, btnExit;
	SeekBar skbVolume;//��������
	boolean isRecording = false;//�Ƿ�¼�ŵı��
	// ������Ƶ�����ʣ�44100��Ŀǰ�ı�׼������ĳЩ�豸��Ȼ֧��22050��16000��11025
	static final int frequency = 16000;
	// ������Ƶ��¼�Ƶ�����CHANNEL_IN_STEREOΪ˫������CHANNEL_CONFIGURATION_MONOΪ������
	// Notice��¼��ʱ˫����������
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	// ��Ƶ���ݸ�ʽ:PCM 16λÿ����������֤�豸֧�֡�PCM 8λÿ����������һ���ܵõ��豸֧�֡�
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
		//// ��Ƶ��ȡԴ
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
		skbVolume.setMax(100);//�������ڵļ���
		skbVolume.setProgress(70);//����seekbar��λ��ֵ
		audioTrack.setStereoVolume(0.7f, 0.7f);//���õ�ǰ������С
		skbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float vol=(float)(seekBar.getProgress())/(float)(seekBar.getMax());
				audioTrack.setStereoVolume(vol, vol);//��������
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
				new RecordPlayThread().start();// ��һ���̱߳�¼�߷�
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
				audioRecord.startRecording();//��ʼ¼��
				audioTrack.play();//��ʼ����
				
				while (isRecording) {
					//��MIC�������ݵ�������
					int bufferReadResult = audioRecord.read(buffer, 0,
							recBufSize);
					
					byte[] tmpBuf = new byte[bufferReadResult];
					System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);
					
					//д�����ݼ�����
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
