package com.example.stockquotes1;

public class StockRtData {
	
	public String name;
	public String code;
	public double open_price;
	public double yesterday_price;
	public double price;
	public double highest_price;
	public double lowest_price;
	public double bibuy_price;
	public double bisell_price;
	public int volume;
	public double turnover;
	public int b1;
	public double b1p;
	public int b2;
	public double b2p;
	public int b3;
	public double b3p;
	public int b4;
	public double b4p;
	public int b5;
	public double b5p;
	public int s1;
	public double s1p;
	public int s2;
	public double s2p;
	public int s3;
	public double s3p;
	public int s4;
	public double s4p;
	public int s5;
	public double s5p;
	public String date;
	public String time;

	public StockRtData(String str) {
		// TODO Auto-generated constructor stub
		code=str.substring(13,19);
		String[] items=str.substring(21).split(",");
		name=items[0];
		open_price= Double.valueOf(items[1]);
		yesterday_price= Double.valueOf(items[2]);
		price= Double.valueOf(items[3]);
		highest_price= Double.valueOf(items[4]);
		lowest_price= Double.valueOf(items[5]);
		bibuy_price= Double.valueOf(items[6]);
		bisell_price= Double.valueOf(items[7]);
		volume=Integer.valueOf(items[8]);
		turnover=Double.valueOf(items[9]);
		b1=Integer.valueOf(items[10]);
		b1p=Double.valueOf(items[11]);
		b2=Integer.valueOf(items[12]);
		b2p=Double.valueOf(items[13]);
		b3=Integer.valueOf(items[14]);
		b3p=Double.valueOf(items[15]);
		b4=Integer.valueOf(items[16]);
		b4p=Double.valueOf(items[17]);
		b5=Integer.valueOf(items[18]);
		b5p=Double.valueOf(items[19]);
		s1=Integer.valueOf(items[20]);
		s1p=Double.valueOf(items[21]);
		s2=Integer.valueOf(items[22]);
		s2p=Double.valueOf(items[23]);
		s3=Integer.valueOf(items[24]);
		s3p=Double.valueOf(items[25]);
		s4=Integer.valueOf(items[26]);
		s4p=Double.valueOf(items[27]);
		s5=Integer.valueOf(items[28]);
		s5p=Double.valueOf(items[29]);
		date=items[30];
		time=items[31];
	}

}
