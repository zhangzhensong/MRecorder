package com.iscas.mrecorder;

import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioRecord;
import android.view.SurfaceView;

public class ClsOscilloscope {
	private ArrayList<short[]> inBuf = new ArrayList<short[]>();
	private boolean isRecording = false;// �߳̿��Ʊ��
	/**
	 * X����С�ı���
	 */
	public int rateX = 4;
	/**
	 * Y����С�ı���
	 */
	public int rateY = 4;
	/**
	 * Y�����
	 */
	public int baseLine = 0;
	/**
	 * ��ʼ��
	 */
	public void initOscilloscope(int rateX, int rateY, int baseLine) {
		this.rateX = rateX;
		this.rateY = rateY;
		this.baseLine = baseLine;
	}
	/**
	 * ��ʼ
	 * 
	 * @param recBufSize
	 *            AudioRecord��MinBufferSize
	 */
	public void Start(AudioRecord audioRecord, int recBufSize, SurfaceView sfv,
			Paint mPaint) {
		isRecording = true;
		new RecordThread(audioRecord, recBufSize).start();// ��ʼ¼���߳�
		new DrawThread(sfv, mPaint).start();// ��ʼ�����߳�
	}
	/**
	 * ֹͣ
	 */
	public void Stop() {
		isRecording = false;
		
		inBuf.clear();// ���
	}
	/**
	 * �����MIC�������ݵ�inBuf
	 * 
	 * @author GV
	 * 
	 */
	class RecordThread extends Thread {
		private int recBufSize;
		private AudioRecord audioRecord;
		public RecordThread(AudioRecord audioRecord, int recBufSize) {
			this.audioRecord = audioRecord;
			this.recBufSize = recBufSize;
		}
		public void run() {
			try {
				short[] buffer = new short[recBufSize];
				audioRecord.startRecording();// ��ʼ¼��
				while (isRecording) {
					// ��MIC�������ݵ�������
					int bufferReadResult = audioRecord.read(buffer, 0,
							recBufSize);
					short[] tmpBuf = new short[bufferReadResult / rateX];
					for (int i = 0, ii = 0; i < tmpBuf.length; i++, ii = i
							* rateX) {
						tmpBuf[i] = buffer[ii];
					}
					synchronized (inBuf) {//
						inBuf.add(tmpBuf);// �������
					}
				}
				audioRecord.stop();
			} catch (Throwable t) {
			}
		}
	};
	/**
	 * �������inBuf�е�����
	 * 
	 * @author GV
	 * 
	 */
	class DrawThread extends Thread {
		private int oldX = 0;// �ϴλ��Ƶ�X����
		private int oldY = 0;// �ϴλ��Ƶ�Y����
		private SurfaceView sfv;// ����
		private int X_index = 0;// ��ǰ��ͼ������ĻX�������
		private Paint mPaint;// ����
		public DrawThread(SurfaceView sfv, Paint mPaint) {
			this.sfv = sfv;
			this.mPaint = mPaint;
		}
		public void run() {
			while (isRecording) {
				ArrayList<short[]> buf = new ArrayList<short[]>();
				synchronized (inBuf) {
					if (inBuf.size() == 0)
						continue;
					buf = (ArrayList<short[]>) inBuf.clone();// ����
					inBuf.clear();// ���
				}
				for (int i = 0; i < buf.size(); i++) {
					short[] tmpBuf = buf.get(i);
					SimpleDraw(X_index, tmpBuf, rateY, baseLine);// �ѻ��������ݻ�����
					X_index = X_index + tmpBuf.length;
					if (X_index > sfv.getWidth()) {
						X_index = 0;
					}
				}
			}
		}
		/**
		 * ����ָ������
		 * 
		 * @param start
		 *            X�Ὺʼ��λ��(ȫ��)
		 * @param buffer
		 *            ������
		 * @param rate
		 *            Y��������С�ı���
		 * @param baseLine
		 *            Y�����
		 */
		void SimpleDraw(int start, short[] buffer, int rate, int baseLine) {
			if (start == 0)
				oldX = 0;
			Canvas canvas = sfv.getHolder().lockCanvas(
					new Rect(start, 0, start + buffer.length, sfv.getHeight()));// �ؼ�:��ȡ����
			canvas.drawColor(Color.BLACK);// �������
			int y;
			for (int i = 0; i < buffer.length; i++) {// �ж��ٻ�����
				int x = i + start;
				y = buffer[i] / rate + baseLine;// ������С���������ڻ�׼��
				canvas.drawLine(oldX, oldY, x, y, mPaint);
				oldX = x;
				oldY = y;
			}
			sfv.getHolder().unlockCanvasAndPost(canvas);// �����������ύ���õ�ͼ��
		}
	}
}

