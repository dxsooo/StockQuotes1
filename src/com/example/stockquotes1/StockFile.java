package com.example.stockquotes1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class StockFile {
	@SuppressWarnings("unchecked")
	public static Map<String,StockData> read(FileInputStream fin) throws IOException{
		Map<String,StockData> stockmap= null;
		ObjectInputStream ois = null;
		try{   
	         ois = new ObjectInputStream(fin);
	         Object o = ois.readObject();
	         stockmap = (Map<String,StockData>)o;  
	         fin.close();       
	     }   
	     catch(Exception e){   
	         e.printStackTrace();   
	     }   
		return stockmap;
	}
	public static void write(Map<String,StockData> stockmap,FileOutputStream fout) throws IOException{
		ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fout);
            oos.writeObject(stockmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                oos.flush();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	

}
