package com.example.stockquotes1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Map<String, StockData> StockMap = null;
	private Map<String, String> NameMap = null;
	private LinearLayout quotes;
	private Thread updatethread = null;
	private static String Request_api = "http://hq.sinajs.cn/list=";

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO: httpRequest
			if (StockMap == null || StockMap.isEmpty())
				return;

			String request_stock = "";
			Iterator<String> it = StockMap.keySet().iterator();
			while (it.hasNext()) {
				String key;
				String value;
				key = it.next().toString();
				value = StockMap.get(key).stockid;
				request_stock = request_stock + value + ",";
			}

			try {
				URL url = new URL(Request_api + request_stock);
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.connect();
				if (urlConn.getResponseCode() == 200) {
					BufferedReader bf = new BufferedReader(
							new InputStreamReader(urlConn.getInputStream(),
									"GB2312"));
					StringBuffer buffer = new StringBuffer();
					String line = "";
					while ((line = bf.readLine()) != null) {
						buffer.append(line);
					}

					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("value", buffer.toString());
					data.putString("status", "0");
					msg.setData(data);
					handler.sendMessage(msg);
				}
				urlConn.disconnect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("status", "1");
				msg.setData(data);
				handler.sendMessage(msg);
			}

		}
	};

	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			if (data.getString("status") == "1") {
				Toast.makeText(MainActivity.this, "网络连接错误", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			String val = data.getString("value");

			String[] datas = val.split(";");
			ArrayList<StockRtData> rawlistdata = new ArrayList<StockRtData>();
			for (int j = 0; j < datas.length; j++) {
				rawlistdata.add(new StockRtData(datas[j]));
			}
			// calculate
			if (rawlistdata.isEmpty())
				return;

			// update UI
			quotes.removeAllViews();
			NameMap.clear();

			int referwidth = findViewById(R.id.LinearLayout1).getWidth();
			for (int i = 0; i < rawlistdata.size(); i++) {

				LinearLayout layout = new LinearLayout(MainActivity.this);
				layout.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, 20, 0, 20);
				layout.setLayoutParams(lp);

				LinearLayout layoutTT = new LinearLayout(MainActivity.this);
				layoutTT.setOrientation(LinearLayout.VERTICAL);
				LinearLayout.LayoutParams lpTT = new LinearLayout.LayoutParams(
						referwidth * 11 / 20,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				layoutTT.setLayoutParams(lpTT);
				TextView tv11 = new TextView(MainActivity.this);
				tv11.setTextSize(20);
				tv11.setTextColor(Color.BLACK);
				tv11.setText(rawlistdata.get(i).name);
				layoutTT.addView(tv11);
				TextView tv12 = new TextView(MainActivity.this);
				tv12.setTextSize(14);
				tv12.setTextColor(Color.BLUE);
				tv12.setText(rawlistdata.get(i).code);
				layoutTT.addView(tv12);
				layout.addView(layoutTT);

				// set name map
				NameMap.put(rawlistdata.get(i).code, rawlistdata.get(i).name);

				TextView tv2 = new TextView(MainActivity.this);
				tv2.setTextSize(20);
				tv2.setTextColor(Color.BLACK);
				if (rawlistdata.get(i).price == 0) {
					// stop today then use yesterday price
					tv2.setText(String.valueOf(rawlistdata.get(i).yesterday_price));
				} else
					tv2.setText(String.valueOf(rawlistdata.get(i).price));
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
						referwidth * 2 / 10,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp2.setMargins(0, 18, 0, 0);
				tv2.setLayoutParams(lp2);
				layout.addView(tv2);

				TextView tv3 = new TextView(MainActivity.this);
				tv3.setTextSize(20);
				double interest = (rawlistdata.get(i).price - StockMap
						.get(rawlistdata.get(i).code).cost)
						/ StockMap.get(rawlistdata.get(i).code).cost;
				if (interest == -1) {
					// stop today then use yesterday price
					interest = (rawlistdata.get(i).yesterday_price - StockMap
							.get(rawlistdata.get(i).code).cost)
							/ StockMap.get(rawlistdata.get(i).code).cost;
				}
				if (interest > 0) {
					tv3.setTextColor(Color.WHITE);
					tv3.setBackgroundColor(Color.RED);
				} else if (interest < 0) {
					tv3.setTextColor(Color.WHITE);
					tv3.setBackgroundColor(Color.GREEN);
				} else {
					tv3.setTextColor(Color.WHITE);
					tv3.setBackgroundColor(Color.GRAY);
				}
				tv3.setText(String.format(" %.2f %%", interest * 100));

				LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
						referwidth * 1 / 4,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp3.setMargins(0, 18, 0, 0);
				tv3.setLayoutParams(lp3);
				layout.addView(tv3);

				quotes.addView(layout);

				layout.setOnClickListener(quoteListener);
				layout.setTag(rawlistdata.get(i).code);

				TextView line = new TextView(MainActivity.this);
				line.setLayoutParams(new LinearLayout.LayoutParams(referwidth,
						1));
				line.setBackgroundColor(Color.LTGRAY);

				quotes.addView(line);

			}
			Toast.makeText(MainActivity.this, "更新 " + rawlistdata.get(0).time,
					Toast.LENGTH_SHORT).show();
		}
	};

	private OnClickListener quoteListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, EditActivity.class);
			String cur_code = v.getTag().toString();
			Bundle bundle = new Bundle();
			bundle.putString("name", NameMap.get(cur_code));
			bundle.putString("code", cur_code);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		quotes = (LinearLayout) findViewById(R.id.quotes);
		quotes.setOnClickListener(quoteListener);

		findViewById(R.id.freshbutton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (updatethread.isAlive())
							return;
						else {
							updatethread = new Thread(runnable);
							updatethread.start();
						}
					}

				});

		NameMap = new HashMap<String, String>();

	}

	private void loadstockinfo() {
		// TODO Auto-generated method stub
		if (StockMap != null)
			StockMap.clear();
		// read
		try {
			FileInputStream fin = openFileInput("data");
			StockMap = StockFile.read(fin);
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// update UI
		if (StockMap == null || StockMap.isEmpty()) {
			quotes.removeAllViews();
			NameMap.clear();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.add) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, AddActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadstockinfo();
		if (updatethread == null || !updatethread.isAlive()) {
			updatethread = new Thread(runnable);
			updatethread.start();
		}
	}
}
