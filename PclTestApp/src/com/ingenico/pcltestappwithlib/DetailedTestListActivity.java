package com.ingenico.pcltestappwithlib;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DetailedTestListActivity extends Activity {

	public static final int	TEST_CONFIGURATION_GET_TERM_COMP = 0;
	public static final int	TEST_CONFIGURATION_GET_COMPANION_INFO = 1;
	public static final int	TEST_CONFIGURATION_INPUT_SIMUL = 2;
	public static final int	TEST_CONFIGURATION_GET_DATE_TIME = 3;
	public static final int	TEST_CONFIGURATION_SET_DATE_TIME = 4;
	public static final int	TEST_CONFIGURATION_SEND_MESSAGE = 5;
	public static final int	TEST_CONFIGURATION_RECEIVE_MESSAGE = 6;
	public static final int	TEST_CONFIGURATION_FLUSH_MESSAGE = 7;
	public static final int	TEST_CONFIGURATION_SHORTCUT = 8;
	public static final int	TEST_CONFIGURATION_RESET = 9;
	public static final int	TEST_CONFIGURATION_GET_FULL_SN = 10;
	public static final int	TEST_CONFIGURATION_GET_SPMCI_VERSION = 11;
	public static final int TEST_CONFIGURATION_LOCK_BACKLIGHT = 12;
	public static final int TEST_CONFIGURATION_GET_BATTERY_LEVEL = 13;
	public static final int TEST_CONFIGURATION_POWER_OFF = 14;
	

	public static final int	TEST_TRANSACTION_DO = 100;
	public static final int	TEST_TRANSACTION_DO_EX = 101;
	
	public static final int	TEST_PRINTER_PRINT_TEXT = 200;
	public static final int	TEST_PRINTER_PRINT_BITMAP = 201;
	public static final int	TEST_PRINTER_PRINT_STORE_LOGO = 202;
	public static final int	TEST_PRINTER_PRINT_PRINT_LOGO = 203;
	public static final int	TEST_PRINTER_STATUS= 204;
	public static final int	TEST_PRINTER_OPEN= 205;
	public static final int	TEST_PRINTER_CLOSE= 206;
	public static final int	TEST_PRINTER_ISO_FONTS= 207;
	public static final int TEST_PRINTER_CASH_DRAWER = 208;
	
	
	public static final int	TEST_BCR_OPEN = 300;
	public static final int	TEST_BCR_CLOSE = 301;
	public static final int	TEST_BCR_READ = 302;
	public static final int	TEST_BCR_SETUP = 303;
	public static final int TEST_BCR_RESET = 304;
	public static final int TEST_BCR_START_STOP_SCAN = 305;
	public static final int TEST_BCR_GET_VERSION = 306;
	
	
	public static final int	TEST_NETWORK_UPLOAD= 400;
	public static final int	TEST_NETWORK_DOWNLOAD= 401;
	
	public static final int	TEST_UPDATE_TMS_LOCAL = 500;
	public static final int	TEST_UPDATE_TMS_REMOTE = 501;
	public static final int	TEST_UPDATE_REMOTE = 502;
	public static final int	TEST_UPDATE_PARAMETERS = 503;
	
	public static final int	TEST_IPA_PRINTER_PRINTTEXT = 600;
	
	public static final int	TEST_SIGNATURE_CAPTURE_EXTERNAL = 700;
	
	public static final int	TEST_TRANSACTION = 100;
	public static final int	TEST_PRINTER = 200;
	public static final int	TEST_BCR = 300;
	public static final int	TEST_NETWORK = 400;
	public static final int	TEST_UPDATE = 500;
	public static final int	TEST_IPA_PRINTER = 600;
	public static final int	TEST_SIGNATURE_CAPTURE = 700;
	
		
	public static String[] mStringsConfiguration = {
		"C001 - Get Terminal Components",
		"C002 - Companion Information",
		"C003 - Input Simulation",
		"C004 - Get Date & Time",
		"C005 - Set Date & Time",		
		"C006 - Send Message",
		"C007 - Receive Message",
		"C008 - Flush Message",
		"C009 - Shortcut",
		"C010 - Reset",
		"C011 - Get Full Serial Number",
		"C012 - Get SPMCI Version",
		"C013 - Backlight Lock",
		"C014 - Get Battery Level",
		"C015 - Power Off"
	};
	
	public static String[] mStringsTransaction = {
			"T001 - Do Transaction",
			"T002 - Do Transaction Extended"
	};
	
	public static String[] mStringsPrinter = {
			"P001 - Print Text",
			"P002 - Print Bitmap",
			"P003 - Store Logo",
			"P004 - Print Logo",
			"P005 - Get Printer Status",
			"P006 - Open Printer",
			"P007 - Close Printer",
			"P008 - Test ISO fonts",
			"P009 - Open Cash Drawer"
	};
	
	public static String[] mStringsBarcodeReader = {
			"B001 - Open Barcode Reader",
			"B002 - Close Barcode Reader",
			"B003 - Scan Barcode",
			"B004 - Setup Barcode Reader",
			"B005 - Reset Barcode Reader",
			"B006 - Start/Stop Scan",
			"B007 - Get version"
	};
	
	public static String[] mStringsNetwork = {
			"N001 - Android to Telium Bridge",
			"N002 - Telium to Android Bridge"
	};
	
	public static String[] mStringsUpdate = {
			"U001 - Local Update TMS Agent",
			"U002 - Remote Update TMS Agent",
			"U003 - Remote Update",
			"U004 - TMS Parameters"
	};
	
	public static String[] mStringsIpaPrinter = {
		"I001 - Print from Telium"
	};
	
	public static String[] mStringsSignatureCapture = {
		"S001 - Signature capture (external)"
	};
	
	private int mTest;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_test_list);
        Intent i = getIntent();
        mTest = i.getIntExtra("Tests", 0);
        String[] strList = null;
        switch (mTest) {
        case TestListActivity.TEST_CONFIGURATION:
        	strList = mStringsConfiguration;
        	break;
        case TestListActivity.TEST_TRANSACTION:
        	strList = mStringsTransaction;
        	break;
        case TestListActivity.TEST_PRINTER:
        	strList = mStringsPrinter;
        	break;
        case TestListActivity.TEST_BCR:
        	strList = mStringsBarcodeReader;
        	break;
        case TestListActivity.TEST_NETWORK:
        	strList = mStringsNetwork;
        	break;
        case TestListActivity.TEST_UPDATE:
        	strList = mStringsUpdate;
        	break;
        case TestListActivity.TEST_IPA_PRINTER:
        	strList = mStringsIpaPrinter;
        	break;
        case TestListActivity.TEST_SIGNATURE_CAPTURE:
        	strList = mStringsSignatureCapture;
        	break;
        default:
        	strList = mStringsConfiguration;
        	break;
        }
        ListView lv = (ListView)findViewById(R.id.lvDetailedTest);
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strList));
        lv.setOnItemClickListener(listener);
    }

    OnItemClickListener listener = new OnItemClickListener() {
	
		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			// Specific case for barcode reader setup
			if (mTest+position == DetailedTestListActivity.TEST_BCR_SETUP)
			{
				Intent i = new Intent(DetailedTestListActivity.this, BcrSetupActivity.class);
				startActivity(i);
			}
			else
			{
				Intent i = new Intent(DetailedTestListActivity.this, TestActivity.class);
				i.putExtra("Test", mTest + position);
				startActivity(i);
			}
			
		}
    };
}
