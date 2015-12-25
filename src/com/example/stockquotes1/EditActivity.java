package com.example.stockquotes1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditActivity extends Activity {
	private StockData data;
	private String name;
	private String code;
	private EditText editCost;
	private EditText editAmount;
	private Map<String, StockData> StockMap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		Bundle bundle = this.getIntent().getExtras();

		name = bundle.getString("name");
		code = bundle.getString("code");
		// read file with code
		try {
			FileInputStream fin = openFileInput("data");
			StockMap = StockFile.read(fin);
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		data = StockMap.get(code);
		editCost = (EditText) findViewById(R.id.editCost);
		editAmount = (EditText) findViewById(R.id.editAmount);

		LinearLayout board = (LinearLayout) findViewById(R.id.board);
		TextView tv1 = new TextView(this);
		tv1.setText(name);
		tv1.setTextSize(50);
		tv1.setTextColor(Color.BLACK);
		board.addView(tv1);
		TextView tv2 = new TextView(this);
		tv2.setText("\t\t\t\t" + code);
		tv2.setTextSize(30);
		tv2.setTextColor(Color.BLUE);
		board.addView(tv2);

		findViewById(R.id.line).setBackgroundColor(Color.LTGRAY);
		editCost.setText(String.valueOf(data.cost));
		editAmount.setText(String.valueOf(data.amount));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// save change
		if(StockMap.containsKey(code))
		{
			StockMap.get(code).cost = Double.valueOf(editCost.getText().toString());
			StockMap.get(code).amount = Integer.valueOf(editAmount.getText()
					.toString());
		}
		try {
			FileOutputStream fout = openFileOutput("data", MODE_PRIVATE);
			StockFile.write(StockMap, fout);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.del) {
			dialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void dialog() {

		AlertDialog.Builder builder = new Builder(EditActivity.this);
		builder.setMessage("删除这个股票？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				StockMap.remove(code);
				EditActivity.this.finish();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
