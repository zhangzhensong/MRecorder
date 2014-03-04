package com.iscas.mrecorder;

import java.net.URLEncoder;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.example.Util;
import com.iscas.mrecorder.R;

public class MainActivity extends SherlockPreferenceActivity {
	
	@SuppressWarnings({ "deprecation" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.app_name);
		
//		Crittercism.init(getApplicationContext(), "508ab27601ed857a20000003");
		this.addPreferencesFromResource(R.xml.main_activity);
//		setContentView(R.layout.activity_main);
//		button01 = (MyImageButton)findViewById(R.id.button01);
//		button01.setText("Wifi");
//		button02 = (MyImageButton)findViewById(R.id.button02);
//		button02.setText("Recorder");
//		button03 = (MyImageButton)findViewById(R.id.button03);
//		button03.setText("History");
		
//		Intent intent = new Intent(this, CustomScaleAnimation.class);
//		startActivity(intent);
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference pref) {
		Class<?> cls = null;
		String title = pref.getTitle().toString();
//		if (title.equals(getString(R.string.wifi_connect))) {
//			cls = ConnectWifi.class;
//		} else 
		if (title.equals(getString(R.string.recorder_activity1))) {
			cls = MAudiuRecorder.class;
		} else if (title.equals(getString(R.string.recorder_activity2))) {
			cls = MOscilloscope.class;
		}else if (title.equals(getString(R.string.recorder_history1))) {
			cls = MRecorder.class;
		} else {
			new AlertDialog.Builder(this)
			.setTitle(R.string.sorry)
			.setMessage(Html.fromHtml(getString(R.string.under_developing)))
			.show();
			return false;
		}
//		if (title.equals(getString(R.string.properties))) {
//			cls = PropertiesActivity.class;	
//		} else if (title.equals(getString(R.string.attach))) {
//			cls = AttachExample.class;
//		} else if (title.equals(getString(R.string.changing_fragments))) {
//			cls = FragmentChangeActivity.class;
//		} else if (title.equals(getString(R.string.left_and_right))) {
//			cls = LeftAndRightActivity.class;
//		} else if (title.equals(getString(R.string.responsive_ui))) {
//			cls = ResponsiveUIActivity.class;
//		} else if (title.equals(getString(R.string.viewpager))) {
//			cls = ViewPagerActivity.class;
//		} else if (title.equals(getString(R.string.title_bar_slide))) {
//			cls = SlidingTitleBar.class;
//		} else if (title.equals(getString(R.string.title_bar_content))) {
//			cls = SlidingContent.class;
//		} else if (title.equals(getString(R.string.anim_zoom))) {
//			cls = CustomZoomAnimation.class;
//		} else if (title.equals(getString(R.string.anim_scale))) {
//			cls = CustomScaleAnimation.class;
//		} else if (title.equals(getString(R.string.anim_slide))) {
//			cls = CustomSlideAnimation.class;
//		}
		Intent intent = new Intent(this, cls);
		startActivity(intent);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.github:
			Util.goToGitHub(this);
			return true;
		case R.id.about:
			new AlertDialog.Builder(this)
			.setTitle(R.string.about)
			.setMessage(Html.fromHtml(getString(R.string.about_msg)))
			.show();
			break;
		case R.id.author:
			new AlertDialog.Builder(this)
			.setTitle(R.string.author)
			.setMessage(Html.fromHtml(getString(R.string.author_info)))
			.show();
			break;
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
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.example_list, menu);
		return true;
	}
	
}
