package com.iscas.mrecorder;

import java.net.URLEncoder;

//import com.actionbarsherlock.view.MenuItem;
import com.iscas.mrecorder.R;
import com.jeremyfeinstein.slidingmenu.example.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class RecorderHistory extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.recorder_history);
        setContentView(R.layout.recordhistory);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case android.R.id.home:
//			toggle();
//			return true;
		case R.id.github:
			Util.goToGitHub(this);
			return true;
		case R.id.about:
			new AlertDialog.Builder(this)
			.setTitle(R.string.about)
			.setMessage(Html.fromHtml(getString(R.string.about_msg)))
			.show();
			return true;
		case R.id.author:
			new AlertDialog.Builder(this)
			.setTitle(R.string.author)
			.setMessage(Html.fromHtml(getString(R.string.author_info)))
			.show();
			return true;
		case R.id.contact:
			final Intent email = new Intent(android.content.Intent.ACTION_SENDTO);
			String uriText = "mailto:zhensongzhang@gmail.com" +
					"?subject=" + URLEncoder.encode("MRecorder Feedback"); 
			email.setData(Uri.parse(uriText));
			try {
				startActivity(email);
			} catch (Exception e) {
				Toast.makeText(this, R.string.no_email, Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getSupportMenuInflater().inflate(R.menu.example_list, menu);
		getMenuInflater().inflate(R.menu.example_list, menu);
		return true;
	}
}
