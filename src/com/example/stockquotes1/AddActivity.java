package com.example.stockquotes1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends Activity {
	
	private EditText stockid;
	private EditText cost;
	private EditText amount;
	private boolean flag;
	private Map<String,StockData> StockMap = null;
	StockData sdata=new StockData();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        
        stockid=(EditText)findViewById(R.id.editText1);
        cost=(EditText)findViewById(R.id.editText2);
        amount=(EditText)findViewById(R.id.editText3);
        
        findViewById(R.id.finish).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(stockid.getText().toString().length()==0)
				{
					Toast.makeText(AddActivity.this, "请输入股票代码", Toast.LENGTH_SHORT).show();  
					return;
				}
				if(cost.getText().toString().length()==0)
				{
					Toast.makeText(AddActivity.this, "请输入每股成本", Toast.LENGTH_SHORT).show();  
					return;
				}
				if(amount.getText().toString().length()==0)
				{
					Toast.makeText(AddActivity.this, "请输入持有股数", Toast.LENGTH_SHORT).show();  
					return;
				}
				//valid code
				sdata.stockid="sh"+stockid.getText().toString();
				if(!valid())
				{
					sdata.stockid="sz"+stockid.getText().toString();
					if(!valid())
					{
						Toast.makeText(AddActivity.this, "无效股票代码", Toast.LENGTH_SHORT).show();  
						return;		
					}
				}
				sdata.cost=Double.valueOf(cost.getText().toString());
				sdata.amount=Integer.valueOf(amount.getText().toString());
				
				//load and add and save
				try {
					FileInputStream fin = openFileInput("data");
					StockMap = StockFile.read(fin);
					fin.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(StockMap==null)
				{
					//tree map for auto-sort
					StockMap=new TreeMap<String,StockData>();
				}
				StockMap.put(sdata.stockid.substring(2),sdata);
				
				try {
					FileOutputStream fout = openFileOutput("data",MODE_PRIVATE);
					StockFile.write(StockMap,fout);
					fout.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				AddActivity.this.finish();
				
			}});
	}
	protected boolean valid() {
		// TODO Auto-generated method stub
		flag=false;
		Runnable runnable =new Runnable(){
            @Override
            public void run() {
            	try {
        			URL url = new URL("http://hq.sinajs.cn/list="+sdata.stockid);
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

        				if(buffer.toString().length()>20)
        				{
        					flag=true;
        				}
        			}
        			urlConn.disconnect();
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        			flag=false;
        		}
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
        while(thread.isAlive())//wait
        {}
		
		return flag;
	}
}
