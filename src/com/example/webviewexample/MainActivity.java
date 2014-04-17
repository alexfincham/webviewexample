package com.example.webviewexample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends Activity {

	static final public String MYPREFS = "myprefs";
	static final public String PREF_URL = "restore_url";
	static final public String WEBPAGE_NOTHING = "about:blank";
	static final public String MY_WEBPAGE = "http://users.soe.ucsc.edu/~luca/android.html";
	static final public String LOG_TAG = "webview_example";
	
	WebView myWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myWebView = (WebView) findViewById(R.id.webView1);
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// Binds the Javascript interface
		myWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		myWebView.loadUrl(MY_WEBPAGE);
		myWebView.loadUrl("javascript:alert(\"Hello\")");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public class JavaScriptInterface {
		Context mContext; // Having the context is useful for lots of things, 
		// like accessing preferences.

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;
		}

		@JavascriptInterface
		public void myFunction(String args) {
			final String myArgs = args;
			Log.i(LOG_TAG, "I am in the javascript call.");
			runOnUiThread(new Runnable() {
				public void run() {
					Button v = (Button) findViewById(R.id.button1);
					v.setText(myArgs);
				}
			});

		}

	}
	
	@Override
	public void onPause() {

		Method pause = null;
		try {
			pause = WebView.class.getMethod("onPause");
		} catch (SecurityException e) {
			// Nothing
		} catch (NoSuchMethodException e) {
			// Nothing
		}
		if (pause != null) {
			try {
				pause.invoke(myWebView);
			} catch (InvocationTargetException e) {
			} catch (IllegalAccessException e) {
			}
		} else {
			// No such method.  Stores the current URL.
			String suspendUrl = myWebView.getUrl();
			SharedPreferences settings = getSharedPreferences(MainActivity.MYPREFS, 0);
			Editor ed = settings.edit();
			ed.putString(PREF_URL, suspendUrl);
			ed.commit();
			// And loads a URL without any processing.
			myWebView.clearView();
			myWebView.loadUrl(WEBPAGE_NOTHING);
		}
		super.onPause();
	}    


	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
