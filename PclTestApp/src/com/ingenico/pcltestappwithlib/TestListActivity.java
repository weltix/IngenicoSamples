package com.ingenico.pcltestappwithlib;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TestListActivity extends Activity {

	public static final int	TEST_CONFIGURATION = 0;
	public static final int	TEST_TRANSACTION = 100;
	public static final int	TEST_PRINTER = 200;
	public static final int	TEST_BCR = 300;
	public static final int	TEST_NETWORK = 400;
	public static final int	TEST_UPDATE = 500;
	public static final int	TEST_IPA_PRINTER = 600;
	public static final int	TEST_SIGNATURE_CAPTURE = 700;
		
	private String[] mStrings = {
		"Configuration", "Transaction", "Printer", "Barcode reader", "Network", "Update", "Print from Telium", "Signature capture"	
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list);
        ListView lv = (ListView)findViewById(R.id.lvTest);
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStrings));
        lv.setOnItemClickListener(listener);
	}

    OnItemClickListener listener = new OnItemClickListener() {
	
		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			Intent i = new Intent(TestListActivity.this, DetailedTestListActivity.class);
			i.putExtra("Tests", position*100);
			startActivity(i);
		}
    };
}
