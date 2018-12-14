package com.ingenico.pcltestappwithlib;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.ingenico.pclservice.IPclServiceCallback;
import com.ingenico.TmsAgent;
import com.ingenico.pclservice.Fonts;
import com.ingenico.pclservice.PclService;
import com.ingenico.pclservice.TransactionIn;
import com.ingenico.pclservice.TransactionOut;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.InputType;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class TestActivity extends CommonActivity {
	
	private RadioButton cbs[];
	private RadioGroup rg;
	ScrollView sv;
	LinearLayout ll;
	private int mTest;
	private TextView mtvTitle;
	private TextView mtvDescription;
	private TextView mtvStatus;
	private EditText mtvResult;
	private TextView mtvStatic2;
	private TextView mtvStatic3;
	private TextView mtvStatic4;
	private TextView mtvStatic5;
	private TextView mtvStatic6;
	private TextView mtvStatic7;
	private TextView mtvStatic8;
	private TextView mtvStatic9;
	private TextView mtvStatic10;
	private EditText metStatic2;
	private EditText metStatic3;
	private EditText metStatic4;
	private EditText metStatic5;
	private EditText metStatic6;
	private EditText metStatic7;
	private Spinner  msSpinner1;
	private Spinner  msSpinner2;
	private Spinner  msSpinner3;
	private CheckBox mcbHexa;
	private CheckBox mcbLocalBridge;
	private Button mbtnRun;
	private Typeface mtfStrato;
	
	/* VARIABLES USED DURING TESTS */
	private static final String TAG = "PCLTESTAPP";
	private static final int JUSTIFIED_CENTER = 0;
	private static final int JUSTIFIED_RIGHT = 1;
	private static final int JUSTIFIED_LEFT = 2;
	private static final String EXTRA_SIGNATURE_BMP = "signature";
	private String[] slistOfBmp;
	AssetManager assetManager;
	static int id = 1;
	private boolean mBcrRead;
	private Context mContext;
	private boolean mCallbackRegistered = false;
	private static Bitmap mLastSignature = null;

    private NetworkTask mNetworkTask;
	private int miScanState = 0;
	private static final int NB_ISO_FONTS = 7;		
	static class PclObject {
		PclServiceConnection serviceConnection;
		PclService service;
		NetworkTask nwTask;
		int iScanState;
	}
	private long mTestStartTime;
	private long mTestEndTime;

	// Returns a valid id that isn't in use
	public int findId(){  
	    View v = findViewById(id);  
	    while (v != null){  
	        v = findViewById(++id);  
	    }  
	    return id++;  
	}
	
	class TextObject extends Object {
    	private String text;
    	private byte font;
    	private byte justification;
    	private byte xfactor;
    	private byte yfactor;
    	private byte underline;
    	private byte bold;
    	public TextObject(String text, byte font, byte justification, byte xfactor, byte yfactor, byte underline, byte bold) {
    		this.setText(text);
    		this.setFont(font);
    		this.setJustification(justification);
    		this.setXFactor(xfactor);
    		this.setYFactor(yfactor);
    		this.setUnderline(underline);
    		this.setBold(bold);
    	}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public byte getFont() {
			return font;
		}
		public void setFont(byte font) {
			this.font = font;
		}
		public byte getJustification() {
			return justification;
		}
		public void setJustification(byte justification) {
			this.justification = justification;
		}
		public byte getXFactor() {
			return xfactor;
		}
		public void setXFactor(byte xfactor) {
			this.xfactor = xfactor;
		}
		public byte getYFactor() {
			return yfactor;
		}
		public void setYFactor(byte yfactor) {
			this.yfactor = yfactor;
		}
		public byte getUnderline() {
			return underline;
		}
		public void setUnderline(byte underline) {
			this.underline = underline;
		}
		public byte getBold() {
			return bold;
		}
		public void setBold(byte bold) {
			this.bold = bold;
		}
    }
	
	class SignObject extends Object {
    	private int x_pos;
    	private int y_pos;
    	private int width;
    	private int height;
    	private int timeout;
    	
    	public SignObject(int pos_x, int pos_y, int width2, int height2, int timeout) {
    		this.setXpos(pos_x);
    		this.setYpos(pos_y);
    		this.setWidth(width2);
    		this.setHeight(height2);
    		this.setTimeout(timeout);
    	}
		public int getXpos() {
			return x_pos;
		}
		public void setXpos(int x_pos) {
			this.x_pos = x_pos;
		}
		public int getYpos() {
			return y_pos;
		}
		public void setYpos(int y_pos) {
			this.y_pos = y_pos;
		}
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			this.height = height;
		}
		public int getTimeout() {
			return timeout;
		}
		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}
	}
	
	// ----------------------------------------------------------------------
    // Code showing how to deal with callbacks.
    // ----------------------------------------------------------------------
    
    /**
     * This implementation is used to receive callbacks from the local
     * service.
     */
    private IPclServiceCallback mCallback = new IPclServiceCallback() {
        
        public void shouldFeedPaper(int lines) {
        	Log.d(TAG, "shouldFeedPaper");
        	mtvStatus.setText("Feed Paper: " + lines + " lines");
        	byte[] tmp = new byte[lines];
        	for (int i=0; i<lines; i++)
        		tmp[i] = '\n';
        	mtvResult.append(new String(tmp));
        }
        
        public void shouldCutPaper() {
        	Log.d(TAG, "shouldCutPaper");
        	mtvStatus.setText("Cut Paper");
        	Bitmap image = Bitmap.createBitmap(mtvResult.getWidth(), mtvResult.getHeight(), Bitmap.Config.RGB_565);
        	mtvResult.draw(new Canvas(image));
        	String url = Images.Media.insertImage(getContentResolver(), image, "title", null);
        	Intent sendIntent = new Intent(Intent.ACTION_SEND); 
        	sendIntent.putExtra("sms_body", "some text"); 
        	sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
        	sendIntent.setType("image/bmp"); 
        	startActivity(sendIntent);
        }
        
        public void shouldPrintText(String text, byte font, byte justification, byte xfactor, byte yfactor, byte underline, byte bold) {
        	Log.d(TAG, "shouldPrintText");
        	mtvStatus.setText(String.format("Print Text %s", text));
        	int style;
        	if (bold == 1)
        		style = Typeface.BOLD;
        	else
        		style = Typeface.NORMAL;
        	
        	String family = "monospace";
        	Typeface face = Typeface.DEFAULT;
        	Alignment align = Alignment.ALIGN_NORMAL;
        	float scaleX = 1;
        	int size = (int) mtvResult.getTextSize();
        	switch(font)
        	{
        	case 0:
        		face = Typeface.DEFAULT;
        		break;
        	case 1:
        		family = "serif";
        		face = Typeface.SANS_SERIF;
        		break;
        	case 2:
        		family = "sans-serif";
        		face = mtfStrato;
        		break;
        	}
        	
        	switch(justification) {
        	case JUSTIFIED_CENTER:
        		align = Alignment.ALIGN_CENTER;
        		break;
        	case JUSTIFIED_LEFT:
        		align = Alignment.ALIGN_NORMAL;
        		break;
        	case JUSTIFIED_RIGHT:
        		align = Alignment.ALIGN_OPPOSITE;
        		break;
        	}
        	
        	if (yfactor == 1)
        	{
        		if (xfactor == 1)
        		{
        			scaleX = 1;
        		}
        		else if (xfactor == 2)
        		{
        			scaleX = 2;
        		}
        		else
        		{
        			scaleX = 4;
        		}
        	}
        	else if (yfactor == 2)
        	{
        		size *= 2;
        		if (xfactor == 1)
        		{
        			scaleX = (float) 0.5;
        		}
        		else if (xfactor == 2)
        		{
        			scaleX = 1;
        		}
        		else
        		{
        			scaleX = 2;
        		}
        	}
        	else
        	{
        		size *= 4;
        		if (xfactor == 1)
        		{
        			scaleX = (float) 0.25;
        		}
        		else if (xfactor == 2)
        		{
        			scaleX = (float) 0.5;
        		}
        		else
        		{
        			scaleX = 1;
        		}
        	}
        	
        	int prev_len = mtvResult.getText().length();
        	int len = text.length();
        	mtvResult.append(text);
			
        	Spannable str = mtvResult.getText();
        	// Create our span sections, and assign a format to each.
        	str.setSpan(new StyleSpan(style), prev_len, prev_len+len-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        	str.setSpan(new CustomTypefaceSpan(family, face), prev_len, prev_len+len-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        	str.setSpan(new AlignmentSpan.Standard(align), prev_len, prev_len+len-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        	str.setSpan(new ScaleXSpan(scaleX), prev_len, prev_len+len-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        	str.setSpan(new AbsoluteSizeSpan(size, false), prev_len, prev_len+len-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        	if (underline == 1)
        		str.setSpan(new UnderlineSpan(), prev_len, prev_len+len-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        public void shouldPrintRawText(byte[] text, byte charset, byte font, byte justification, byte xfactor, byte yfactor, byte underline, byte bold) {
        	if (charset == 127)
        	{
	        	try {
					shouldPrintText(new String(text, "Cp1251"), font, justification, xfactor, yfactor, underline, bold);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
        }
        
        public void shouldPrintImage(Bitmap image, byte justification) {
        	Log.d(TAG, "shouldPrintImage");
        	int prev_len = mtvResult.getText().length();
        	Alignment align;
        	mtvResult.append(" \n");
        	Spannable str = mtvResult.getText();
        	
        	switch(justification) {
        	case JUSTIFIED_CENTER:
        		align = Alignment.ALIGN_CENTER;
        		break;
        	case JUSTIFIED_LEFT:
        		align = Alignment.ALIGN_NORMAL;
        		break;
        	case JUSTIFIED_RIGHT:
        		align = Alignment.ALIGN_OPPOSITE;
        		break;
        	default:
        		align = Alignment.ALIGN_NORMAL;
            	break;
        	}
        	
        	str.setSpan(new ImageSpan(mContext, image), prev_len, prev_len+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        	str.setSpan(new AlignmentSpan.Standard(align), prev_len, prev_len+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        	mtvResult.setText(str);
        }

		@Override
		public void shouldDoSignatureCapture(int pos_x, int pos_y, int width,
				int height, int timeout) {
			Log.d(TAG, "shouldDoSignatureCapture");
        	Intent intent = new Intent(mContext, CaptureSignature.class);
    		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    		intent.putExtra("POS_X", pos_x);
    		intent.putExtra("POS_Y", pos_y);
    		intent.putExtra("WIDTH", width);
    		intent.putExtra("HEIGHT", height);
    		intent.putExtra("TIMEOUT", timeout);
    		intent.putExtra("FINISH", false);
    		startActivityForResult(intent, 1);
		}

		@Override
		public void signatureTimeoutExceeded() {
			Log.d(TAG, "signatureTimeoutExceeded");
			Intent intent = new Intent(mContext, CaptureSignature.class);
    		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        	intent.putExtra("FINISH", true);
        	startActivity(intent);
		}

		@Override
		public void shouldAddSignature() {
			Log.d(TAG, "shouldAddSignature");
			if (mLastSignature != null)
			{
				//mHandler.sendMessage(mHandler.obtainMessage(MSG_ADD_SIGNATURE, mLastSignature));
            	int prev_len = mtvResult.getText().length();
            	mtvResult.append(" \n");
            	Spannable str = mtvResult.getText();
            	str.setSpan(new ImageSpan(mContext, mLastSignature), prev_len, prev_len+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            	mtvResult.setText(str);
			}
		}

		@Override
		public int shouldStartReceipt(byte type) {
			mtvResult.append(new String("******** START RECEIPT ***********\n"));
			switch (type) {
        	case 0:
        		mtvResult.append(new String("MERCHANT\n"));
        		break;
        	case 1:
        		mtvResult.append(new String("CUSTOMER\n"));
        		break;
        	}
			return 0;
		}

		@Override
		public int shouldEndReceipt() {
			mtvResult.append(new String("********  END RECEIPT  ***********"));
			return 0;
		}
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mReleaseService = 1;
        /*
        final PclObject data = (PclObject) getLastNonConfigurationInstance();
		if (data == null || data.service == null || data.serviceConnection == null) {
			Log.d(TAG, "onCreate: Init service" );
			initService();
		}
		else
		{
			Log.d(TAG, "onCreate: Service already initialized" );
			mServiceConnection = data.serviceConnection;
			mPclService = data.service;
			mNetworkTask = data.nwTask;
			miScanState = data.iScanState;
			mBound = true;
		}
		*/
        Log.d(TAG, "onCreate: Init service" );
		initService();
        mContext = this;
        Intent i = getIntent();
        mTest = i.getIntExtra("Test", 0);
        mBcrRead = false;
        mtvTitle = (TextView)findViewById(R.id.tvTitle);
        mtvDescription = (TextView)findViewById(R.id.tvDescription);
        mtvStatus = (TextView)findViewById(R.id.tvStatus);
        mtvResult = (EditText)findViewById(R.id.tvResult);
        mtvStatic2 = (TextView)findViewById(R.id.tvStatic2);
        mtvStatic3 = (TextView)findViewById(R.id.tvStatic3);
        mtvStatic4 = (TextView)findViewById(R.id.tvStatic4);
        mtvStatic5 = (TextView)findViewById(R.id.tvStatic5);
        mtvStatic6 = (TextView)findViewById(R.id.tvStatic6);
        mtvStatic7 = (TextView)findViewById(R.id.tvStatic7);
        mtvStatic8 = (TextView)findViewById(R.id.tvStatic8);
        mtvStatic9 = (TextView)findViewById(R.id.tvStatic9);
        mtvStatic10 = (TextView)findViewById(R.id.tvStatic10);
        metStatic2 = (EditText)findViewById(R.id.etStatic2);
        metStatic3 = (EditText)findViewById(R.id.etStatic3);
        metStatic4 = (EditText)findViewById(R.id.etStatic4);
        metStatic5 = (EditText)findViewById(R.id.etStatic5);
        metStatic6 = (EditText)findViewById(R.id.etStatic6);
        metStatic7 = (EditText)findViewById(R.id.etStatic7);
        msSpinner1 = (Spinner)findViewById(R.id.spinner1);
        msSpinner2 = (Spinner)findViewById(R.id.spinner2);
        msSpinner3 = (Spinner)findViewById(R.id.spinner3);
        mcbHexa = (CheckBox)findViewById(R.id.checkBox1);
        mcbLocalBridge = (CheckBox)findViewById(R.id.checkBoxlocalBridge);
        mbtnRun = (Button)findViewById(R.id.buttonRun);
        
        
        
        /* Initialisation */
    	mtvStatic2.setVisibility(View.GONE);
    	metStatic2.setVisibility(View.GONE);
    	mtvStatic3.setVisibility(View.GONE);
    	metStatic3.setVisibility(View.GONE);
    	mtvStatic4.setVisibility(View.GONE);
    	metStatic4.setVisibility(View.GONE);
    	metStatic5.setVisibility(View.GONE);
    	metStatic6.setVisibility(View.GONE);
    	metStatic7.setVisibility(View.INVISIBLE);
    	mtvStatic5.setVisibility(View.GONE);
    	mtvStatic6.setVisibility(View.GONE);
    	mtvStatic7.setVisibility(View.GONE);
    	mtvStatic8.setVisibility(View.GONE);
    	mtvStatic9.setVisibility(View.GONE);
    	mtvStatic10.setVisibility(View.INVISIBLE);
    	msSpinner1.setVisibility(View.GONE);
    	msSpinner2.setVisibility(View.GONE);
    	msSpinner3.setVisibility(View.GONE);
    	mcbHexa.setVisibility(View.GONE);
    	mcbLocalBridge.setVisibility(View.GONE);
    	metStatic2.setText("");
    	metStatic3.setText("");
    	metStatic4.setText("");
    	metStatic5.setText("");
    	metStatic6.setText("");
    	metStatic7.setText("");
    	
    	mtvResult.setClickable(false);
    	mtvResult.setEnabled(false);
    	int type = mtvResult.getInputType();
    	type |= InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
    	type &= ~InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
    	mtvResult.setInputType(type);
        
        String strTitle = null;
        int pos = mTest % 100;
        ScrollView sv;
        LinearLayout ll;
        switch (mTest-pos) {
        case TestListActivity.TEST_CONFIGURATION:
        	strTitle = DetailedTestListActivity.mStringsConfiguration[pos];
        	break;
        case TestListActivity.TEST_TRANSACTION:
        	strTitle = DetailedTestListActivity.mStringsTransaction[pos];
        	break;
        case TestListActivity.TEST_PRINTER:
        	strTitle = DetailedTestListActivity.mStringsPrinter[pos];
        	break;
        case TestListActivity.TEST_BCR:
        	strTitle = DetailedTestListActivity.mStringsBarcodeReader[pos];
        	break;
        case TestListActivity.TEST_NETWORK:
        	strTitle = DetailedTestListActivity.mStringsNetwork[pos];
        	break;
        case TestListActivity.TEST_UPDATE:
        	strTitle = DetailedTestListActivity.mStringsUpdate[pos];
        	break;
        case TestListActivity.TEST_IPA_PRINTER:
        	strTitle = DetailedTestListActivity.mStringsIpaPrinter[pos];
        	break;
        case TestListActivity.TEST_SIGNATURE_CAPTURE:
        	strTitle = DetailedTestListActivity.mStringsSignatureCapture[pos];
        	break;
        }
        
        ArrayAdapter<String> spinnerArrayAdapter;
		switch (mTest) {
    
        case DetailedTestListActivity.TEST_CONFIGURATION_GET_TERM_COMP:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to display all the components running on the Bluetooth companion.");
        	break; 
        case DetailedTestListActivity.TEST_CONFIGURATION_GET_COMPANION_INFO:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to get the informations from the Bluetooth companion.");
        	break; 
        case DetailedTestListActivity.TEST_CONFIGURATION_INPUT_SIMUL:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to simulate Bluetooth companion's keyboard.");
        	break;
        case DetailedTestListActivity.TEST_CONFIGURATION_GET_DATE_TIME:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to get the date and time from the Bluetooth companion.");
        	break;      	
        case DetailedTestListActivity.TEST_CONFIGURATION_SET_DATE_TIME:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to synchronize its date to the Bluetooth companion.");
        	break;      	
        case DetailedTestListActivity.TEST_CONFIGURATION_SEND_MESSAGE:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to send a message to the Bluetooth companion.");
        	mtvStatic10.setVisibility(View.VISIBLE);
        	mtvStatic10.setText("Message to send: ");
        	metStatic7.setText("");
        	metStatic7.setVisibility(View.VISIBLE);
        	break;      	
        case DetailedTestListActivity.TEST_CONFIGURATION_RECEIVE_MESSAGE:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to receive a message from the Bluetooth companion.");
        	break;      	
        case DetailedTestListActivity.TEST_CONFIGURATION_FLUSH_MESSAGE:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to flush a message received from the Bluetooth companion.");
        	break;
        case DetailedTestListActivity.TEST_CONFIGURATION_SHORTCUT:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to send a shortcut to the Bluetooth companion.");
        	mtvStatic10.setVisibility(View.VISIBLE);
        	mtvStatic10.setText("Shortcut to send: ");
        	metStatic7.setText("");
        	metStatic7.setVisibility(View.VISIBLE);
        	break;
        case DetailedTestListActivity.TEST_CONFIGURATION_RESET:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to reset the Bluetooth companion.");
        	break;
        	
        case DetailedTestListActivity.TEST_CONFIGURATION_GET_FULL_SN:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to get the full Serial Number from the Bluetooth companion.");
        	break;
        	
        case DetailedTestListActivity.TEST_CONFIGURATION_GET_SPMCI_VERSION:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to get SPMCI version from the Bluetooth companion.");
        	break;
        	
        case DetailedTestListActivity.TEST_CONFIGURATION_LOCK_BACKLIGHT:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to lock/unlock companion backlight.");
        	mtvStatic5.setVisibility(View.VISIBLE);
        	mtvStatic5.setText("Lock/Unlock");
        	spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { "Unlock", "Lock (unlocked with keyboard)" });
        	msSpinner1.setAdapter(spinnerArrayAdapter);
        	msSpinner1.setVisibility(View.VISIBLE);
        	break;
        	
        case DetailedTestListActivity.TEST_CONFIGURATION_GET_BATTERY_LEVEL:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to get battery level from the Bluetooth companion.");
        	break;
        	
        case DetailedTestListActivity.TEST_TRANSACTION_DO:
        	mtvDescription.setText("" + 
        			"Description:\nThe Android device shall be able to do a transaction on the Bluetooth companion using a payment application.");
        	// POS number
        	mtvStatic2.setVisibility(View.VISIBLE);
        	mtvStatic2.setText("Pos Number: ");
        	metStatic2.setText("58");
        	metStatic2.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic2.setVisibility(View.VISIBLE);
        	
        	// Currency code
        	mtvStatic3.setVisibility(View.VISIBLE);
        	mtvStatic3.setText("Currency Code: ");
        	metStatic3.setText("978");
        	metStatic3.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic3.setVisibility(View.VISIBLE);
        	
        	// Amount
        	mtvStatic4.setVisibility(View.VISIBLE);
        	mtvStatic4.setText("Amount: ");
        	metStatic4.setText("1110");
        	metStatic4.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic4.setVisibility(View.VISIBLE);
        	
        	// Transaction type
        	mtvStatic5.setText("Trans. Type: ");
        	mtvStatic5.setVisibility(View.VISIBLE);
        	spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { "C Debit", "D Credit", "J Discard","K Duplicata" });
        	msSpinner1.setAdapter(spinnerArrayAdapter);
        	msSpinner1.setVisibility(View.VISIBLE);
        	
        	// Authorization type
        	mtvStatic6.setText("Auth. Type: ");
        	mtvStatic6.setVisibility(View.VISIBLE);
        	spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { "1", "2" });
        	msSpinner2.setAdapter(spinnerArrayAdapter);
        	msSpinner2.setVisibility(View.VISIBLE);
        	msSpinner2.setSelection(1);
        	
        	// Authorization required
        	mtvStatic7.setText("Auth. Required: ");
        	mtvStatic7.setVisibility(View.VISIBLE);
        	spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { "1", "2" });
        	msSpinner3.setAdapter(spinnerArrayAdapter);
        	msSpinner3.setVisibility(View.VISIBLE);
        	msSpinner3.setSelection(1);
        	
        	// User data
        	mtvStatic8.setText("User Data: ");
        	mtvStatic8.setVisibility(View.VISIBLE);
        	metStatic5.setText("");
        	metStatic5.setVisibility(View.VISIBLE);
        	break;
        
        case DetailedTestListActivity.TEST_TRANSACTION_DO_EX:
        	mtvDescription.setText("" + 
        			"Description:\nThe Android device shall be able to do a transaction with extended data on the Bluetooth companion using a payment application.");
        	// POS number
        	mtvStatic2.setVisibility(View.VISIBLE);
        	mtvStatic2.setText("Pos Number: ");
        	metStatic2.setText("58");
        	metStatic2.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic2.setVisibility(View.VISIBLE);
        	
        	// Currency code
        	mtvStatic3.setVisibility(View.VISIBLE);
        	mtvStatic3.setText("Currency Code: ");
        	metStatic3.setText("978");
        	metStatic3.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic3.setVisibility(View.VISIBLE);
        	
        	// Amount
        	mtvStatic4.setVisibility(View.VISIBLE);
        	mtvStatic4.setText("Amount: ");
        	metStatic4.setText("1110");
        	metStatic4.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic4.setVisibility(View.VISIBLE);
        	
        	// Transaction type
        	mtvStatic5.setText("Trans. Type: ");
        	mtvStatic5.setVisibility(View.VISIBLE);
        	spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { "C Debit", "D Credit", "I Iso2", "J Discard","K Duplicata" });
        	msSpinner1.setAdapter(spinnerArrayAdapter);
        	msSpinner1.setVisibility(View.VISIBLE);
        	
        	// Authorization type
        	mtvStatic6.setText("Auth. Type: ");
        	mtvStatic6.setVisibility(View.VISIBLE);
        	spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { "1", "2" });
        	msSpinner2.setAdapter(spinnerArrayAdapter);
        	msSpinner2.setVisibility(View.VISIBLE);
        	msSpinner2.setSelection(1);
        	
        	// Authorization required
        	mtvStatic7.setText("Auth. Required: ");
        	mtvStatic7.setVisibility(View.VISIBLE);
        	spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] { "1", "2" });
        	msSpinner3.setAdapter(spinnerArrayAdapter);
        	msSpinner3.setVisibility(View.VISIBLE);
        	msSpinner3.setSelection(1);
        	
        	// User data
        	mtvStatic8.setText("User Data: ");
        	mtvStatic8.setVisibility(View.VISIBLE);
        	metStatic5.setText("");
        	metStatic5.setVisibility(View.VISIBLE);
        	
        	// Extended data
        	mtvStatic9.setText("Extended Data: ");
        	mtvStatic9.setVisibility(View.VISIBLE);
        	metStatic6.setText("");
        	metStatic6.setVisibility(View.VISIBLE);
        	
        	// Application number
        	mtvStatic10.setText("Application Number: ");
        	mtvStatic10.setVisibility(View.VISIBLE);
        	metStatic7.setText("");
        	metStatic7.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic7.setVisibility(View.VISIBLE);
        	
        	// Check box hexa
        	mcbHexa.setVisibility(View.VISIBLE);
        	break;	 
        	
        case DetailedTestListActivity.TEST_PRINTER_PRINT_TEXT:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to print text on the Bluetooth companion printer.");
        	mtvStatic10.setVisibility(View.VISIBLE);
        	mtvStatic10.setText("Message to print: ");
        	metStatic7.setText("");
        	metStatic7.setVisibility(View.VISIBLE);
        	break;
        	
        case DetailedTestListActivity.TEST_PRINTER_PRINT_BITMAP:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to print a bitmap on the Bluetooth companion printer.");
        	mtvStatic10.setVisibility(View.VISIBLE);
        	mtvStatic10.setText("Select image to print: ");
            assetManager = getAssets();
            try {
            	slistOfBmp = assetManager.list("pics");
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
            cbs = new RadioButton[slistOfBmp.length];
            sv = (ScrollView) findViewById(R.id.sv);
        	ll = (LinearLayout) findViewById(R.id.ll);
        	rg = new RadioGroup(this);
        	ll.setVisibility(View.VISIBLE);
        	sv.setVisibility(View.VISIBLE);
        	ll.addView(rg);
        	
        	for(int nbOfBmp=0; nbOfBmp < slistOfBmp.length; nbOfBmp++)
        	{
        		cbs[nbOfBmp] = new RadioButton(this);
				cbs[nbOfBmp].setText(slistOfBmp[nbOfBmp]);
				cbs[nbOfBmp].setTextSize((float) 16.0);
				cbs[nbOfBmp].setId(nbOfBmp);
				rg.addView(cbs[nbOfBmp]);
        		Log.d(TAG, String.format("image to print [%d]=%s", nbOfBmp,slistOfBmp[nbOfBmp]));
        	}
        	break;
        	
        case DetailedTestListActivity.TEST_PRINTER_PRINT_PRINT_LOGO:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to print a bitmap previously stored on the Bluetooth companion.");
        	mtvStatic10.setVisibility(View.VISIBLE);
        	metStatic7.setVisibility(View.VISIBLE);
        	mtvStatic10.setText("Logo name: ");
        	metStatic7.setText("");
        	break;
        	
        case DetailedTestListActivity.TEST_PRINTER_PRINT_STORE_LOGO:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to store a bitmap on the Bluetooth companion.");
        	mtvStatic8.setVisibility(View.VISIBLE);
        	mtvStatic8.setText("Logo name: ");
        	metStatic5.setText("");
        	metStatic5.setVisibility(View.VISIBLE);
        	mtvStatic10.setVisibility(View.VISIBLE);
        	mtvStatic10.setText("Select image to store: ");
            AssetManager assetManager = getAssets();
            try {
            	slistOfBmp = assetManager.list("pics");
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
            cbs = new RadioButton[slistOfBmp.length];
            sv = (ScrollView) findViewById(R.id.sv);
        	ll = (LinearLayout) findViewById(R.id.ll);
        	rg = new RadioGroup(this);
        	ll.setVisibility(View.VISIBLE);
        	sv.setVisibility(View.VISIBLE);
        	ll.addView(rg);
        	int idx = 0;
        	for(int nbOfBmp=0; nbOfBmp < slistOfBmp.length; nbOfBmp++)
        	{
        		if (slistOfBmp[nbOfBmp].endsWith("bmp"))
        		{
	        		cbs[idx] = new RadioButton(this);
					cbs[idx].setText(String.format(slistOfBmp[nbOfBmp].substring(0, slistOfBmp[nbOfBmp].indexOf('.'))));
					cbs[idx].setTextSize((float) 18.0);
					cbs[idx].setId(idx);
					rg.addView(cbs[idx]);
					idx++;
	        		Log.d(TAG, String.format("bmp to store [%d]=%s", nbOfBmp,slistOfBmp[nbOfBmp]));
        		}
        	}
        	break;  
        	
        case DetailedTestListActivity.TEST_PRINTER_STATUS:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to get the printer status.");
        	break;
        	
        case DetailedTestListActivity.TEST_PRINTER_OPEN:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to open the printer.");
        	break;
        	
        case DetailedTestListActivity.TEST_PRINTER_CLOSE:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to close the printer.");
        	break;
        	
        case DetailedTestListActivity.TEST_PRINTER_ISO_FONTS:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to print with ISO fonts defined on Telium.");
        	mtvStatic10.setVisibility(View.VISIBLE);
        	mtvStatic10.setText("Select font: ");
            
            cbs = new RadioButton[NB_ISO_FONTS];
            sv = (ScrollView) findViewById(R.id.sv);
        	ll = (LinearLayout) findViewById(R.id.ll);
        	rg = new RadioGroup(this);
        	ll.setVisibility(View.VISIBLE);
        	sv.setVisibility(View.VISIBLE);
        	ll.addView(rg);
        	
        	for(int nbFonts=0; nbFonts < NB_ISO_FONTS; nbFonts++)
        	{
	        		cbs[nbFonts] = new RadioButton(this);
	        		switch (nbFonts)
	        		{
	        		case 0:
	        			cbs[nbFonts].setText("/SYSTEM/ISO1.SGN");
	        			break;
	        		case 1:
	        			cbs[nbFonts].setText("/SYSTEM/ISO2.SGN");
	        			break;
	        		case 2:
	        			cbs[nbFonts].setText("/SYSTEM/ISO3.SGN");
	        			break;
	        		case 3:
	        			cbs[nbFonts].setText("/SYSTEM/ISO5.SGN");
	        			break;
	        		case 4:
	        			cbs[nbFonts].setText("/SYSTEM/ISO6.SGN");
	        			break;
	        		case 5:
	        			cbs[nbFonts].setText("/SYSTEM/ISO7.SGN");
	        			break;
	        		case 6:
	        			cbs[nbFonts].setText("/SYSTEM/ISO15.SGN");
	        			break;
	        		
	        		}
					
					cbs[nbFonts].setTextSize((float) 18.0);
					cbs[nbFonts].setId(nbFonts);
					rg.addView(cbs[nbFonts]);
        	}
        	break;
        	
        case DetailedTestListActivity.TEST_PRINTER_CASH_DRAWER:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to open a cash drawer.");
        	break;
        	
        case DetailedTestListActivity.TEST_BCR_OPEN:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to open barcode reader peripheral on the Bluetooth companion.");
        	// Inactivity timeout
        	mtvStatic2.setVisibility(View.VISIBLE);
        	mtvStatic2.setText("Inactivity TO (in minutes): ");
        	metStatic2.setText("");
        	metStatic2.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic2.setVisibility(View.VISIBLE);
        	break;
        	
        case DetailedTestListActivity.TEST_BCR_CLOSE:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to close barcode reader peripheral on the Bluetooth companion.");
        	break;
        	
        case DetailedTestListActivity.TEST_BCR_READ:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to read data scanned from Bluetooth companion's barcode reader.");
        	break;
        
        case DetailedTestListActivity.TEST_BCR_SETUP:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to configure Bluetooth companion's barcode reader.");
        	break;
        	
        case DetailedTestListActivity.TEST_BCR_RESET:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to reset Bluetooth companion's barcode reader.");
        	break;
        	
        case DetailedTestListActivity.TEST_BCR_START_STOP_SCAN:
        	mtvDescription.setText("" + 
        			"Description:\n The Android device shall be able to start/stop scan on Bluetooth companion's barcode reader.");
        	if (miScanState == 0)
        	{
        		miScanState = 1;
        	}
        	if (miScanState == 1)
        	{
        		mbtnRun.setText("Start Scan");
        	}
        	else
        	{
        		mbtnRun.setText("Stop Scan");
        	}
        	break;
        
        case DetailedTestListActivity.TEST_NETWORK_UPLOAD:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to establish a TCP connection with the Bluetooth companion and exchange data.");
        	// Port
        	mtvStatic4.setText("Port: ");
        	mtvStatic4.setVisibility(View.VISIBLE);
        	metStatic4.setText("");
        	metStatic4.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic4.setVisibility(View.VISIBLE);
        	
        	// Packet size
        	mtvStatic8.setText("Packet size: ");
        	mtvStatic8.setVisibility(View.VISIBLE);
        	metStatic5.setText("");
        	metStatic5.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic5.setVisibility(View.VISIBLE);
        	
        	// Packets number
        	mtvStatic10.setText("Packets number: ");
        	mtvStatic10.setVisibility(View.VISIBLE);
        	metStatic7.setText("");
        	metStatic7.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic7.setVisibility(View.VISIBLE);
        	
        	//Local Bridge
        	mcbLocalBridge.setVisibility(View.VISIBLE);
        	mcbLocalBridge.setClickable(true);
        	break;
        
        case DetailedTestListActivity.TEST_NETWORK_DOWNLOAD:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to accept a TCP connection from the Bluetooth companion and exchange data.");
        	// Port
			mtvStatic4.setText("Port: ");
        	mtvStatic4.setVisibility(View.VISIBLE);
        	metStatic4.setText("");
        	metStatic4.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic4.setVisibility(View.VISIBLE);
        	
        	// Packet size
        	mtvStatic8.setText("Packet size: ");
        	mtvStatic8.setVisibility(View.VISIBLE);
        	metStatic5.setText("");
        	metStatic5.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic5.setVisibility(View.VISIBLE);
        	
        	// Packets number
        	mtvStatic10.setText("Packets number: ");
        	mtvStatic10.setVisibility(View.VISIBLE);
        	metStatic7.setText("");
        	metStatic7.setInputType(InputType.TYPE_CLASS_NUMBER);
        	metStatic7.setVisibility(View.VISIBLE);
        	break;
        
		case DetailedTestListActivity.TEST_UPDATE_TMS_LOCAL:
			mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to update the Bluetooth companion terminal " +
					"firmware using Local TMS agent tool in local mode. ");
			mtvStatic10.setVisibility(View.VISIBLE);
        	mtvStatic10.setText("File to download: ");
        	metStatic7.setText("");
        	metStatic7.setVisibility(View.VISIBLE);
        	break;
        	
        case DetailedTestListActivity.TEST_UPDATE_TMS_REMOTE:
			mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to update the Bluetooth companion terminal " +
					"firmware using Local TMS agent tool in remote mode. ");
			mtvStatic10.setVisibility(View.VISIBLE);
        	mtvStatic10.setText("File to download: ");
        	metStatic7.setText("");
        	metStatic7.setVisibility(View.VISIBLE);
        	break;	
        	
        case DetailedTestListActivity.TEST_UPDATE_REMOTE:
			mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to update the Bluetooth companion terminal firmware" +
					" using GPRS or WIFI and the Android socks server.");
        	break;	
        	
        case DetailedTestListActivity.TEST_UPDATE_PARAMETERS:
        	initTmsParameters();
        	break;	
        	
        case DetailedTestListActivity.TEST_IPA_PRINTER_PRINTTEXT:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to receive IPA Printer commands from the Telium device.");
        	break;
        	
        case DetailedTestListActivity.TEST_SIGNATURE_CAPTURE_EXTERNAL:
        	mtvDescription.setText("" +
					"Description: \nThe Android device shall be able to provide signature capture feature.");
        	break;
        default:
        	break;
        }
        setTitle(strTitle);
        
        
        mtfStrato = Typeface.createFromAsset(getAssets(), "Strato-linked.ttf");
        
        mbtnRun.setOnClickListener(btnRunListener);
    }
    
    @Override
	protected void onDestroy() {
    	Log.d(TAG, "TestActivity: onDestroy" );
    	if (mReleaseService == 1)
    	{
    	    if (mCallbackRegistered)
    	    {
                mPclService.unregisterCallback(mCallback);
    		    mCallbackRegistered = false;
    	    }
	    	if ((mNetworkTask != null) && (mNetworkTask.mmRunning))
	    	{
	    		Log.d(TAG, "NetworkTask running");
	    		if (mNetworkTask.mmTcpServerSocket != null) {
	    			Log.d(TAG, "Closing ServerSocket");
					try {
						mNetworkTask.mmTcpServerSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	    		}
	    		mNetworkTask.closeStreams();
	    		Log.d(TAG, "Cancel NetworkTask");
	    		mNetworkTask.cancel(true);
	    	}
	    	if (miScanState == 2)
	    	{
	    		runBcrScan();
	    	}
	    	
			releaseService();
    	}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		if (isCompanionConnected())
        {
        	mtvTitle.setText(R.string.str_connected);
        	mtvTitle.setBackgroundColor(Color.GREEN);
        	mtvTitle.setTextColor(Color.BLACK);
        }
        else
        {
        	mtvTitle.setText(R.string.str_not_connected);
        	mtvTitle.setBackgroundColor(Color.RED);
        	mtvTitle.setTextColor(Color.BLACK);
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, String.format("onActivityResult req=%d res=%d", requestCode, resultCode));
		if (requestCode == 1) {
			mLastSignature = null;
			if (resultCode == RESULT_OK) {
				mLastSignature = data.getParcelableExtra(EXTRA_SIGNATURE_BMP);
			}
			{
				mPclService.submitSignatureWithImage(mLastSignature);
			}
		}
	}

	private View.OnClickListener btnRunListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			hideKeyboard(mContext, v);
			mTestStartTime = System.currentTimeMillis();
			switch (mTest) {
			case DetailedTestListActivity.TEST_CONFIGURATION_GET_TERM_COMP:
				runGetCompInfos();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_GET_COMPANION_INFO:
				runCompanionInfo();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_INPUT_SIMUL:
				runInputSimul();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_GET_DATE_TIME:
				runGetDateTime();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_SET_DATE_TIME:
				runSetDateTime();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_SEND_MESSAGE:
				runSendMessage();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_RECEIVE_MESSAGE:
				runReceiveMessage();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_FLUSH_MESSAGE:
				runFlushMessage();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_SHORTCUT:
				runShortcut();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_RESET:
				runReset();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_POWER_OFF:
				runPowerOff();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_GET_FULL_SN:
				runGetFullSerialNumber();
				break;	
			case DetailedTestListActivity.TEST_CONFIGURATION_GET_SPMCI_VERSION:
				runGetSpmciVersion();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_LOCK_BACKLIGHT:
				runBacklightLock();
				break;
			case DetailedTestListActivity.TEST_CONFIGURATION_GET_BATTERY_LEVEL:
				runGetBatteryLevel();
				break;
			case DetailedTestListActivity.TEST_PRINTER_PRINT_TEXT:
				runPrintText();
				break;
			case DetailedTestListActivity.TEST_PRINTER_PRINT_BITMAP:
				runPrintBitmap();
				break;
			case DetailedTestListActivity.TEST_PRINTER_PRINT_STORE_LOGO:
				runStoreLogo();
				break;
			case DetailedTestListActivity.TEST_PRINTER_PRINT_PRINT_LOGO:
				runPrintLogo();
				break;
			case DetailedTestListActivity.TEST_PRINTER_STATUS:
				runPrinterStatus();
				break;
			case DetailedTestListActivity.TEST_PRINTER_OPEN:
				runPrinterOpen();
				break;
			case DetailedTestListActivity.TEST_PRINTER_CLOSE:
				runPrinterClose();
				break;
			case DetailedTestListActivity.TEST_PRINTER_ISO_FONTS:
				runPrinterIsoFonts();
				break;
	        case DetailedTestListActivity.TEST_PRINTER_CASH_DRAWER:
	        	runPrinterCashDrawer();
	        	break;
			case DetailedTestListActivity.TEST_TRANSACTION_DO:
				runDoTransaction();
				break;
			case DetailedTestListActivity.TEST_TRANSACTION_DO_EX:
				runDoTransactionEx();
				break;
			case DetailedTestListActivity.TEST_NETWORK_UPLOAD:
				runNetworkAndroidToTelium();
				break;
			case DetailedTestListActivity.TEST_NETWORK_DOWNLOAD:
				runNetworkTeliumToAndroid();
				break;
			case DetailedTestListActivity.TEST_UPDATE_TMS_LOCAL:
				runDoLocalTmsUpdate();
				break;
			case DetailedTestListActivity.TEST_UPDATE_TMS_REMOTE:
				runDoRemoteTmsUpdate();
				break;
			case DetailedTestListActivity.TEST_UPDATE_REMOTE:
				runDoRemoteUpdate();
				break;
			case DetailedTestListActivity.TEST_UPDATE_PARAMETERS:
				runSetTmsParameters();
				break;
			case DetailedTestListActivity.TEST_BCR_OPEN:
				runBcrOpen();
				break;
			case DetailedTestListActivity.TEST_BCR_CLOSE:
				runBcrClose();
				break;
			case DetailedTestListActivity.TEST_BCR_READ:
				runBcrRead();
				break;
			case DetailedTestListActivity.TEST_BCR_SETUP:
				runBcrSetup();
				break;
			case DetailedTestListActivity.TEST_BCR_RESET:
				runBcrReset();
				break;
			case DetailedTestListActivity.TEST_BCR_START_STOP_SCAN:
				runBcrScan();
				break;
			case DetailedTestListActivity.TEST_BCR_GET_VERSION:
				runBcrGetVersion();
				break;
			case DetailedTestListActivity.TEST_IPA_PRINTER_PRINTTEXT:
				runIpaPrinter();
				break;
			case DetailedTestListActivity.TEST_SIGNATURE_CAPTURE_EXTERNAL:
				runSignatureCapture();
				break;
				
			}
			
		}
    	
    };
 
    /*********************************************************************************/
    /*********************************************************************************/
    /*********************************************************************************/
    /*********************************************************************************/
    /*********************************************************************************/
      
	protected void runGetCompInfos() {
		mtvStatus.setText("");
		mtvResult.setText("");
		new GetTermCompTask().execute();
		
	}

	protected void runGetDateTime() {
		mtvStatus.setText("");
		mtvResult.setText("");
		new GetTimeTask().execute();
	}

	protected void runSetDateTime() {
		byte[] result = new byte[4];
		mtvStatus.setText("");
		mtvResult.setText("");
		new SetTimeTask(result).execute();
	}
	
	protected void runInputSimul() {
		Intent intent = new Intent(TestActivity.this, InputSimulActivity.class);
		startActivity(intent);
	}

	protected void runCompanionInfo() {
		mtvStatus.setText("");
		mtvResult.setText("");
		new GetTermInfoTask().execute();
	}

	protected void runSendMessage() {
		mtvStatus.setText("");
		mtvResult.setText("");
		String str = metStatic7.getText().toString();
		byte[] msg = str.getBytes();
		new SendMsgTask(msg).execute();
		}
	protected void runReceiveMessage() {
		mtvStatus.setText("");
		mtvResult.setText("");
		byte[] msg = new byte[1024];
		new RecvMsgTask(msg).execute();
	}
	protected void runFlushMessage() {
		mtvStatus.setText("");
		mtvResult.setText("");
		new FlushMsgTask().execute();
	}
	protected void runShortcut() {
		mtvStatus.setText("");
		mtvResult.setText("");
		String str = metStatic7.getText().toString();
		byte[] msg = str.getBytes();
		new M2OSShortcutTask(msg).execute();
	}
	protected void runReset() {
		mtvStatus.setText("");
		mtvResult.setText("");
		new ResetTerminalTask().execute(0);
	}

	protected void runPowerOff() {
		mtvStatus.setText("");
		mtvResult.setText("");
		new PowerOffTerminalTask().execute(0);
	}

	protected void runGetFullSerialNumber() {
		mtvStatus.setText("");
		mtvResult.setText("");
		new GetFullSerialNumberTask().execute();
	}
	
	protected void runGetSpmciVersion() {
		mtvStatus.setText("");
		mtvResult.setText("");
		new GetSpmciVersionTask().execute();
	}
	
	protected void runBacklightLock() {
		byte[] result = new byte[1];
		mtvStatus.setText("");
		mtvResult.setText("");
		int pos = msSpinner1.getSelectedItemPosition();
		int lock;
		if (pos == 0)
		{
			lock = 0;
		}
		else
		{
			lock = 3;
		}
		new BacklightLockTask(result).execute(lock);
	}
	
	protected void runGetBatteryLevel() {
		mtvStatus.setText("");
		mtvResult.setText("");
		new GetBatteryLevelTask().execute();
	}
	
    protected void runDoTransaction() {
    	TransactionIn transIn = new TransactionIn();
		TransactionOut transOut = new TransactionOut();

		transIn.setAmount(metStatic4.getText().toString());
		transIn.setCurrencyCode(metStatic3.getText().toString());
		transIn.setOperation(msSpinner1.getSelectedItem().toString().substring(0,1));
		transIn.setTermNum(metStatic2.getText().toString());
		transIn.setAuthorizationType(msSpinner2.getSelectedItem().toString());
		transIn.setCtrlCheque(msSpinner3.getSelectedItem().toString());
		transIn.setUserData1(metStatic5.getText().toString());
		Log.d(TAG, "Amount:" + transIn.getAmount() + " Currency:" + transIn.getCurrencyCode() + " Operation:" + transIn.getOperation());
		Log.d(TAG, "TermNum:" + transIn.getTermNum() + " AuthoType:" + transIn.getAuthorizationType() + " CtrlCheque:" + transIn.getCtrlCheque());
		Log.d(TAG, "UserData:" + transIn.getUserData1());
		mtvStatus.setText("TEST STARTED ...\n");
		mtvResult.setText("");
        mPclService.registerCallback(mCallback);
    	mCallbackRegistered = true;
		new DoTransactionTask(transIn, transOut).execute();
    }
    
    protected void runDoTransactionEx() {
    	int appNumber;
    	TransactionIn transIn = new TransactionIn();
		TransactionOut transOut = new TransactionOut();
		transIn.setAmount(metStatic4.getText().toString());
		transIn.setCurrencyCode(metStatic3.getText().toString());
		transIn.setOperation(msSpinner1.getSelectedItem().toString().substring(0,1));
		transIn.setTermNum(metStatic2.getText().toString());
		transIn.setAuthorizationType(msSpinner2.getSelectedItem().toString());
		transIn.setCtrlCheque(msSpinner3.getSelectedItem().toString());
		transIn.setUserData1(metStatic5.getText().toString());
		if (metStatic7.getText().length() == 0)
			appNumber = 0;
		else
			appNumber = Integer.parseInt(metStatic7.getText().toString());
		byte[] extDataIn = null;
		try {
			byte[] tmp = metStatic6.getText().toString().getBytes("ISO-8859-1");
			if (mcbHexa.isChecked())
			{
				extDataIn = new byte[tmp.length/2];
				int j=0;
				for (int i=0; i<tmp.length; i++)
				{
					if (tmp[i] >= 'a' && tmp[i] <= 'f')
						tmp[i] = (byte) (tmp[i] - 'a' + 10);
					else if (tmp[i] >= 'A' && tmp[i] <= 'F')
						tmp[i] = (byte) (tmp[i] - 'A' + 10);
					else if (tmp[i] >= '0' && tmp[i] <= '9')
						tmp[i] = (byte) (tmp[i] - '0');
				}
				for (int i=0; i<tmp.length; i+=2)
				{
					String str = String.format("%02x", tmp[i]*16 + tmp[i+1]);
					extDataIn[j++] = (byte) Integer.parseInt(str, 16);
				}
			}
			else
			{
				extDataIn = tmp;
			}
			

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			mtvStatus.setText("TEST ERROR: UnsupportedEncodingException");
			return;
		}
		byte[] extDataOut = new byte[5000];
		Log.d(TAG, "Amount:" + transIn.getAmount() + " Currency:" + transIn.getCurrencyCode() + " Operation:" + transIn.getOperation());
		Log.d(TAG, "TermNum:" + transIn.getTermNum() + " AuthoType:" + transIn.getAuthorizationType() + " CtrlCheque:" + transIn.getCtrlCheque());
		Log.d(TAG, "UserData:" + transIn.getUserData1());
		mtvStatus.setText("TEST STARTED ...\n");
		mtvResult.setText("");
		mPclService.registerCallback(mCallback);
    	mCallbackRegistered = true;
		new DoTransactionExTask(transIn, transOut, appNumber, extDataIn, extDataOut).execute();
    }
	
    protected void runPrintText() {
    	mtvStatus.setText("");
		mtvResult.setText("");
		byte[] result = new byte[1];
		new PrintTextTask(result).execute(metStatic7.getText().toString());
		
    }
    
    protected void runPrintBitmap() {
    	byte[] result = new byte[1];
		mtvStatus.setText("");
		mtvResult.setText("");
		int indexButtonChecked = rg.getCheckedRadioButtonId();
		String sBitmapName = new String((String) cbs[indexButtonChecked].getText());
		InputStream is;
        AssetManager assetManager = getAssets();
        try {
			is = assetManager.open("pics/" + sBitmapName);
		} catch (IOException e1) {
			e1.printStackTrace();
			mtvStatus.setText("ERROR: IOException");
			return;
		}
		
		if (sBitmapName.endsWith("bmp"))
		{
			// Open the input stream
	
			byte[] buffer = new byte[1024];
			int length = 0;
			ByteArrayOutputStream myOutputStream = new ByteArrayOutputStream();
	
			try {
				while ((length = is.read(buffer))>0){
				         myOutputStream.write(buffer, 0, length);
	
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				mtvStatus.setText("ERROR: IOException");
				return;
			}
			
			new PrintBitmapTask(result).execute(myOutputStream.toByteArray());
		}
		else
		{
			Bitmap bmp = null;
			bmp = BitmapFactory.decodeStream(is);
			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth() <= 384 ? bmp.getWidth() : 384, bmp.getHeight() <= 1024 ? bmp.getHeight() : 1024);
			new PrintBitmapObjectTask(result).execute(bmp);
		}
		
    }
    
    protected void runStoreLogo() {
    	byte[] result = new byte[1];
    	int indexButtonChecked = rg.getCheckedRadioButtonId();
		String sBitmapName = new String((String) cbs[indexButtonChecked].getText());
		InputStream is;
        AssetManager assetManager = getAssets();
        try {
			is = assetManager.open("pics/" + sBitmapName + ".bmp");
		} catch (IOException e1) {
			e1.printStackTrace();
			mtvStatus.setText("ERROR: IOException");
			return;
		}
		
		
		// Open the input stream

		byte[] buffer = new byte[1024];
		int length = 0;
		ByteArrayOutputStream myOutputStream = new ByteArrayOutputStream();

		try {
			while ((length = is.read(buffer))>0){
			         myOutputStream.write(buffer, 0, length);

			}
		} catch (IOException e1) {
			e1.printStackTrace();
			mtvStatus.setText("ERROR: IOException");
			return;
		}
		new StoreLogoTask(result).execute(myOutputStream.toByteArray());
		
    }
    
    protected void runPrintLogo() {
    	byte[] result = new byte[1];
		mtvStatus.setText("");
		mtvResult.setText("");
		new PrintLogoTask(result).execute();
    }
    
    protected void runPrinterStatus() {
    	
    	byte[] result = new byte[1];
    	mtvStatus.setText("");
		mtvResult.setText("");
		new PrinterStatusTask(result).execute();
    }
    
    protected void runPrinterOpen() {
    	
    	byte[] result = new byte[1];
    	mtvStatus.setText("");
		mtvResult.setText("");
		new PrinterOpenTask(result).execute();
    }

    protected void runPrinterClose() {
	
		byte[] result = new byte[1];
		mtvStatus.setText("");
		mtvResult.setText("");
		new PrinterCloseTask(result).execute();
	}
    
    protected void runPrinterIsoFonts() {
    	byte[] result = new byte[1];
    	int indexButtonChecked = rg.getCheckedRadioButtonId();
    	int font = Fonts.ISO8859_15.ordinal();
    	switch (indexButtonChecked)
    	{
    	case 0:
    		font = Fonts.ISO8859_1.ordinal();
    		break;
    	case 1:
    		font = Fonts.ISO8859_2.ordinal();
    		break;
    	case 2:
    		font = Fonts.ISO8859_3.ordinal();
    		break;
    	case 3:
    		font = Fonts.ISO8859_5.ordinal();
    		break;
    	case 4:
    		font = Fonts.ISO8859_6.ordinal();
    		break;
    	case 5:
    		font = Fonts.ISO8859_7.ordinal();
    		break;
    	case 6:
    		font = Fonts.ISO8859_15.ordinal();
    		break;
    		    		    		    		    		    		    		
    	}
		mtvStatus.setText("");
		mtvResult.setText("");
		new PrinterSetFontTask(result).execute(font);
    }

    protected void runPrinterCashDrawer() {
    	
    	byte[] result = new byte[1];
    	mtvStatus.setText("");
		mtvResult.setText("");
		new PrinterCashDrawerTask(result).execute();
    }
    
	protected void runNetworkAndroidToTelium() {
		boolean bBridgeOnly = false;
		int bridgeResult = -5;
		mtvStatus.setText("");
		mtvResult.setText("");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if ((metStatic4.getText().length() < 4) ||
			(Integer.parseInt(metStatic4.getText().toString()) < 1025) ||
			(Integer.parseInt(metStatic4.getText().toString()) > 65535))
		{
			builder.setTitle("Wrong Parameters")
		       .setMessage("Set Port between 1025 and 65535.")
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int which) {
				return;  
			} });
			builder.create().show();
			return;
		}
		
		if ((metStatic5.getText().length() == 0) && (metStatic7.getText().length() == 0))
		{
			// Only configure a bridge
			bBridgeOnly = true;
		}
		
		if (!bBridgeOnly)
		{
			if ((metStatic5.getText().length() == 0) ||
				(Integer.parseInt(metStatic5.getText().toString()) > 8192))
			{
				builder.setTitle("Wrong Parameters")
			       .setMessage("Set Packet size between 1 and 8192.")
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {
					return;  
				} });
				builder.create().show();
				return;
			}
			
			if ((metStatic7.getText().length() == 0) ||
				(Integer.parseInt(metStatic7.getText().toString()) > 10000))
			{
				builder.setTitle("Wrong Parameters")
			       .setMessage("Set Packets number between 1 and 10000.")
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {
					return;  
				} });
				builder.create().show();
				return;
			}
		}
		
		if( mPclService != null ) {
		    if(mcbLocalBridge.isChecked())
		    {
			    bridgeResult = mPclService.addDynamicBridgeLocal(Integer.parseInt(metStatic4.getText().toString()), 0);
		    }
		    else
		    {
		    	bridgeResult = mPclService.addDynamicBridge(Integer.parseInt(metStatic4.getText().toString()), 0);
		    }
        }
		
		if (!bBridgeOnly && (bridgeResult == 0 || bridgeResult == -2))
		{
			mNetworkTask = new NetworkTask(Integer.parseInt(metStatic4.getText().toString()),
									Integer.parseInt(metStatic5.getText().toString()),
					Integer.parseInt(metStatic7.getText().toString()), false);
			mNetworkTask.execute();
			mtvStatus.setText("TEST STARTED ...\n");
		}
		else
		{
			mtvStatus.setText("AddDynamicBridge");
			switch (bridgeResult)
			{
			case 0:
				mtvResult.setText("OK");
				break;
			case -1:
				mtvResult.setText("(-1) Too many bridges");
				break;
			case -2:
				mtvResult.setText("(-2) Bridge already exists");
				break;
			case -3:
				mtvResult.setText("(-3) Thread creation issue");
				break;
			case -4:
				mtvResult.setText("(-4) Bridge not initialized");
				break;		
			case -5:
				mtvResult.setText("(-5) PclService not initialized");
				break;
			}
		}

	}
    
	protected void runNetworkTeliumToAndroid() {
		boolean bBridgeOnly = false;
		int bridgeResult = -5;
		mtvStatus.setText("");
		mtvResult.setText("");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		if ((metStatic4.getText().length() < 4) ||
			(Integer.parseInt(metStatic4.getText().toString()) < 1025) ||
			(Integer.parseInt(metStatic4.getText().toString()) > 65535))
		{
			builder.setTitle("Wrong Parameters")
			       .setMessage("Set Port between 1025 and 65535.")
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {
					return;  
				} });
			builder.create().show();
			return;
		}
		
		if ((metStatic5.getText().length() == 0) && (metStatic7.getText().length() == 0))
		{
			// Only configure a bridge
			bBridgeOnly = true;
		}
		
		if (!bBridgeOnly)
		{
			if ((metStatic5.getText().length() == 0) ||
				(Integer.parseInt(metStatic5.getText().toString()) > 8192))
			{
				builder.setTitle("Wrong Parameters")
			       .setMessage("Set Packet size between 1 and 8192.")
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {
					return;  
				} });
				builder.create().show();
				return;
			}
			
			if ((metStatic7.getText().length() == 0) ||
				(Integer.parseInt(metStatic7.getText().toString()) > 10000))
			{
				builder.setTitle("Wrong Parameters")
			       .setMessage("Set Packets number between 1 and 10000.")
			       .setPositiveButton("OK", new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialog, int which) {
					return;  
				} });
				builder.create().show();
				return;
			}
		}
		
		if( mPclService != null ) {
			bridgeResult = mPclService.addDynamicBridge(Integer.parseInt(metStatic4.getText().toString()), 1);
		}
		
		if (!bBridgeOnly && (bridgeResult == 0 || bridgeResult == -2))
		{
			mNetworkTask = new NetworkTask(Integer.parseInt(metStatic4.getText().toString()),
					Integer.parseInt(metStatic5.getText().toString()),
					Integer.parseInt(metStatic7.getText().toString()), true);
			mNetworkTask.execute();
			mtvStatus.setText("TEST STARTED ...\n");
		}
		else
		{
			mtvStatus.setText("AddDynamicBridge");
			switch (bridgeResult)
			{
			case 0:
				mtvResult.setText("OK");
				break;
			case -1:
				mtvResult.setText("(-1) Too many bridges");
				break;
			case -2:
				mtvResult.setText("(-2) Bridge already exists");
				break;
			case -3:
				mtvResult.setText("(-3) Thread creation issue");
				break;
			case -4:
				mtvResult.setText("(-4) Bridge not initialized");
				break;		
			case -5:
				mtvResult.setText("(-5) PclService not initialized");
				break;
			}
		}
	}
    
    protected void runDoLocalTmsUpdate() {
    	{
			mPclService.addDynamicBridge(8000, 0);
		}
    	TmsAgent tmsagent = new TmsAgent();

    	String fileToDownload = Environment.getExternalStorageDirectory().toString() + "/temp/" + metStatic7.getText();
    	String fileReport = Environment.getExternalStorageDirectory().toString() + "/temp";
    	tmsagent.tms((String.format("-no_retail_proto -report_path %s -zip %s -com ip:127.0.0.1:8000",fileReport,fileToDownload)));

    }

    protected void runDoRemoteTmsUpdate() {
    	
    }
    
    protected void runDoRemoteUpdate() {
    	mtvStatus.setText("TEST STARTED ...\n");
    	mtvResult.setText("");
    	new DoUpdateTask().execute();
    }
    
    protected void runSetTmsParameters() {
    	mtvStatus.setText("");
    	new SetTmsParametersTask(metStatic3.getText().toString(),
	    			metStatic4.getText().toString(),
	    			metStatic5.getText().toString(),
	    			((TextView)(msSpinner3.getSelectedView())).getText().toString()).execute();
    }
    
    protected void runGetTmsParameters() {
    	mTestStartTime = System.currentTimeMillis();
    	mtvStatus.setText("");
    	new GetTmsParametersTask().execute();
    }
    
    protected void runBcrOpen() {
    	byte[] result = new byte[1];
    	int inactivityTo;
    	mtvStatus.setText("");
		mtvResult.setText("");
		if (metStatic2.getText().length() == 0)
		{
			inactivityTo = -1;
		}
		else
		{
			inactivityTo = Integer.parseInt(metStatic2.getText().toString());
		}
    	new OpenBarcodeTask(inactivityTo == -1 ? inactivityTo : inactivityTo * 6000, result).execute();
    }
	
    protected void runBcrClose() {
    	byte[] result = new byte[1];
    	mtvStatus.setText("");
		mtvResult.setText("");
    	new CloseBarcodeTask(result).execute();
    }
    
    protected void runBcrRead() {
    	mtvStatus.setText("TEST STARTED ...\n");
    	mBcrRead = true;
    }
    
	protected void runBcrSetup() {
    	
    }
    
    protected void runBcrReset() {
    	byte[] result = new byte[1];
    	mtvStatus.setText("");
		mtvResult.setText("");
    	new ResetBarcodeTask(result).execute();
    }
    
    protected void runBcrScan() {
    	byte[] result = new byte[1];
    	mtvStatus.setText("");
		mtvResult.setText("");
		mBcrRead = true;
    	new ScanBarcodeTask(result).execute();
    }
    
    protected void runBcrGetVersion() {
    	byte[] result = new byte[32];
    	mtvStatus.setText("");
		mtvResult.setText("");
    	new GetBarcodeVersionTask(result).execute();
    }
    protected void runIpaPrinter() {
        mPclService.registerCallback(mCallback);
    	mCallbackRegistered = true;
    }
    
    protected void runSignatureCapture() {
    	mPclService.registerCallback(mCallback);
    	mCallbackRegistered = true;
    }
    
    
	@Override
	public void onBarCodeReceived(String barCodeValue, int symbology) {
		if (mBcrRead)
		{
			mtvResult.append(barCodeValue + " (" + mPclService.bcrSymbologyToText(symbology) + ")\r\n");
		}
	}

	@Override
	public void onBarCodeClosed() {
		mtvResult.setText("Barcode reader closed");
		
	}

	@Override
	public void onStateChanged(String state) {
		if (state.equals("CONNECTED"))
		{
			mtvTitle.setText(R.string.str_connected);
			mtvTitle.setBackgroundColor(Color.GREEN);
			mtvTitle.setTextColor(Color.BLACK);
			if (mTest == DetailedTestListActivity.TEST_UPDATE_PARAMETERS)
			{
				runGetTmsParameters();
			}
		}
		else
		{
			mtvTitle.setText(R.string.str_not_connected);
			mtvTitle.setBackgroundColor(Color.RED);
			mtvTitle.setTextColor(Color.BLACK);
		}
	}

	@Override
	void onPclServiceConnected() {
		Log.d(TAG, "onPclServiceConnected");
		
		if (isCompanionConnected())
        {
        	mtvTitle.setText(R.string.str_connected);
        	mtvTitle.setBackgroundColor(Color.GREEN);
        	mtvTitle.setTextColor(Color.BLACK);
        	if (mTest == DetailedTestListActivity.TEST_UPDATE_PARAMETERS)
			{
				runGetTmsParameters();
			}
        }
        else
        {
        	mtvTitle.setText(R.string.str_not_connected);
        	mtvTitle.setBackgroundColor(Color.RED);
        	mtvTitle.setTextColor(Color.BLACK);
        }
	}
	
	
    /*********************************************************************************/
    /*********************************************************************************/
    /*********************************************************************************/
    /*********************************************************************************/

	/* PCL SERVICE TASK*/
	
	
	class DoTransactionTask extends AsyncTask<Void, Void, Boolean> {
		private TransactionIn transIn;
		private TransactionOut transOut;
		public DoTransactionTask(TransactionIn transIn, TransactionOut transOut) {
			this.transIn = transIn;
			this.transOut = transOut;
		}

		protected Boolean doInBackground(Void... tmp) {
			Boolean bRet = doTransaction(transIn, transOut);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			String strResult = "N/A";
			if (result == true) {
				if (transOut.getC3Error().equals(TransactionOut.ErrorCode.SUCCESS.toString()))
					strResult = "Transaction OK";
				else
					strResult = "Transaction KO";
				mtvResult.setText(strResult +
						"\nAmount:" + transOut.getAmount() +
						"\nCurrency:" + transOut.getCurrencyCode() +
						"\nC3Error:" + transOut.getC3Error() + 
			            "\nTermNum:" + transOut.getTerminalNumber() +
			            "\nUserData:" + transOut.getUserData() +
			            "\nFFU:" + transOut.getFFU());
				
			}
			else {
				mtvResult.setText(strResult);
			}
		}
	}
	
	class DoTransactionExTask extends AsyncTask<Void, Void, Boolean> {
		private TransactionIn transIn;
		private TransactionOut transOut;
		private byte[] extDataIn;
		private byte[] extDataOut;
		private long[] extDataOutSize;
		private int appNumber;
		public DoTransactionExTask(TransactionIn transIn, TransactionOut transOut, int appNumber, byte[] extDataIn, byte[] extDataOut) {
			this.transIn = transIn;
			this.transOut = transOut;
			this.extDataIn = extDataIn;
			this.extDataOut = extDataOut;
			this.appNumber = appNumber;
			this.extDataOutSize = new long[1];
			this.extDataOutSize[0] = extDataOut.length;
		}

		protected Boolean doInBackground(Void... tmp) {
			return doTransactionEx(transIn, transOut, appNumber, extDataIn, extDataIn.length, extDataOut, extDataOutSize);
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			String strResult = "N/A";
			if (result == true) {
				String strExtDataOut;
				if (mcbHexa.isChecked())
				{
					// Transform extDataOut in hex
					StringBuffer hex = new StringBuffer();
					String str;
					for(int i = 0; i < extDataOutSize[0]; i++){
						str = Integer.toHexString((int)extDataOut[i]);
						if (str.length() > 2)
							str = str.substring(str.length()-2, str.length());
						else if (str.length() == 1)
							str = "0" + str;
						hex.append(str);
					}
					strExtDataOut = hex.toString();
				  
				}
				else
				{
					strExtDataOut = new String(extDataOut);
				}
				
				if (transOut.getC3Error().equals(TransactionOut.ErrorCode.SUCCESS.toString()))
					strResult = "Transaction OK";
				else
					strResult = "Transaction KO";
					
				mtvResult.setText(strResult +
						"\nAmount:" + transOut.getAmount() +
						"\nCurrency:" + transOut.getCurrencyCode() +
						"\nC3Error:" + transOut.getC3Error() + 
	                    "\nTermNum:" + transOut.getTerminalNumber() + 
	                    "\nUserData:" + transOut.getUserData() +
	                    "\nFFU:" + transOut.getFFU() +
	                    "\nExtData:" + strExtDataOut);
	                      
				
			}
			else {
				mtvResult.setText(strResult);
			}
		}
	}
	
	class FlushMsgTask extends AsyncTask<Void, Void, Boolean> {

		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = flushMsg();
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
		}
	}
	
	class RecvMsgTask extends AsyncTask<Void, Void, Boolean> {
		byte[] m_msg;
		int[] m_bytereceived;
		RecvMsgTask(byte[] msg)
		{
			m_msg = msg.clone();
			m_bytereceived = new int[1];
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = recvMsg(m_msg, m_bytereceived);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			String str = new String(m_msg);
			if (result == true)
				mtvResult.setText(String.format("len=%d msg=%s", m_bytereceived[0], str));
			else
				mtvResult.setText("N/A");
		}
	}
	
	class SendMsgTask extends AsyncTask<Void, Void, Boolean> {
		byte[] m_msg;
		int[] m_bytesent;
		SendMsgTask(byte[] msg)
		{
			m_msg = msg.clone();
			m_bytesent = new int[1];
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = sendMsg(m_msg, m_bytesent);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			String strResult = "N/A";
			if (result == true)
			{
				if (m_bytesent[0] == m_msg.length)
					strResult = "OK";
				else
					strResult = "KO";
			}
			mtvResult.setText(strResult);
		}
	}
	
	class GetTermCompTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... tmp) {
			Boolean bRet = getComponentsInfo();
			return bRet;
		}

		protected void onPostExecute(Boolean result) {

			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				FileInputStream fIn = null;
				char[] buf = new char[2048];
				
				 try {
					fIn = openFileInput("Running.lst");
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					mtvStatus.setText("ERROR: FileNotFoundException");
					return;
				}
				if (fIn != null)
				{
					InputStreamReader isr = new InputStreamReader(fIn);
					
					try {
						isr.read(buf);
					} catch (IOException e) {
						e.printStackTrace();
						mtvStatus.setText("ERROR: IOException");
					}
					try {
						isr.close();
					} catch (IOException e) {
						e.printStackTrace();
						mtvStatus.setText("ERROR: IOException");
					}
					try {
						fIn.close();
					} catch (IOException e) {
						e.printStackTrace();
						mtvStatus.setText("ERROR: IOException");
					}
				}
				
				mtvResult.setText(buf, 0, (int) buf.length);
				mtvResult.setMovementMethod(LinkMovementMethod.getInstance());

			}
			else
			{
				mtvResult.setText("N/A");
			}
		}
	}
	
	class GetTermInfoTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... tmp) {
			Boolean bRet = getTermInfo();
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				strResult = String.format("SN: %08x PN:%08x", SN, PN);
			}
			mtvResult.setText(strResult);
		}
	}
	
	class GetTimeTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... tmp) {
			Boolean bRet = getTime();
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
				mtvResult.setText(String.format("%02d/%02d/%02d %02d:%02d.%02d", sysTime.wMonth, sysTime.wDay, sysTime.wYear, sysTime.wHour, sysTime.wMinute, sysTime.wSecond));
			else
				mtvResult.setText("N/A");
		}
	}
	
	class SetTimeTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		SetTimeTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Void... tmp) {
			Boolean bRet = setTime(mResult);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class M2OSShortcutTask extends AsyncTask<Void, Void, Boolean> {
		byte [] m_shortcut;
		M2OSShortcutTask(byte[] shortcut) {
			m_shortcut = shortcut.clone();
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = launchM2OSShortcut(m_shortcut);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
				mtvResult.setText("OK");
			else
				mtvResult.setText("N/A");
		}
	}
	
	class ResetTerminalTask extends AsyncTask<Integer, Void, Boolean> {
		protected Boolean doInBackground(Integer... info) {
			Boolean bRet = resetTerminal(info[0]);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
				mtvResult.setText("OK");
			else
				mtvResult.setText("N/A");
		}
	}

	class PowerOffTerminalTask extends AsyncTask<Integer, Void, Boolean> {
		protected Boolean doInBackground(Integer... info) {
			Boolean bRet = powerOffTerminal(info[0]);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
				mtvResult.setText("OK");
			else
				mtvResult.setText("N/A");
		}
	}
	
	class GetFullSerialNumberTask extends AsyncTask<Void, Void, Boolean> {
		byte[] fullSN;
		protected Boolean doInBackground(Void... tmp) {
			fullSN  = new byte[30];
			Boolean bRet = getFullSerialNumber(fullSN);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				strResult = new String(fullSN);
			}
			mtvResult.setText(strResult);
		}
	}
	
	class GetSpmciVersionTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] version;
		GetSpmciVersionTask() {
			version = new byte[4];
		}
		protected Boolean doInBackground(Void... tmp) {
			return mPclService.getSPMCIVersion(version);
		}

		protected void onPostExecute(Boolean result) {

			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				mtvResult.setText("\nSPMCI version: ");
				mtvResult.append(new String(version));
			}
			else
			{
				mtvResult.setText("N/A");
			}
		}
	}
	
	class BacklightLockTask extends AsyncTask<Integer, Void, Boolean> {
		private byte[] mResult;
		BacklightLockTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Integer... lock) {
			Boolean bRet = mPclService.setBacklightLock(lock[0], mResult);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {

			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				mtvResult.setText("OK");
			}
			else
			{
				mtvResult.setText("N/A");
			}
		}
	}
	
	class GetBatteryLevelTask extends AsyncTask<Void, Void, Boolean> {
		private int[] level;
		GetBatteryLevelTask() {
			level = new int[1];
		}
		protected Boolean doInBackground(Void... tmp) {
			Boolean bRet = mPclService.getBatteryLevel(level);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {

			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				mtvResult.setText("\nBattery level: " + level[0]);
			}
			else
			{
				mtvResult.setText("N/A");
			}
		}
	}
	
	class PrintBitmapTask extends AsyncTask<byte[], Void, Boolean> {
		private byte[] mResult;
		PrintBitmapTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(byte[]... bmpBuf) {
			Boolean bRet = false;
			byte[] tmp = new byte[1];
			bRet = openPrinter(tmp);
			if (bRet == true)
			{
				if (tmp[0] == 0)
				{
					bRet = printBitmap(bmpBuf[0], bmpBuf[0].length, mResult);
					closePrinter(tmp);
				}
			}
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class PrintTextTask extends AsyncTask<String, Void, Boolean> {
		private byte[] mResult;
		PrintTextTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(String... str) {
			Boolean bRet = false;
			byte[] tmp = new byte[1];
			bRet = openPrinter(tmp);
			Log.d(TAG, String.format("openPrinter bRet=%s mResult=%d", Boolean.toString(bRet), tmp[0]));
			if (bRet == true)
			{
				if (tmp[0] == 0)
				{
					bRet = printText(str[0], mResult);
					Log.d(TAG, String.format("printText bRet=%s mResult=%d", Boolean.toString(bRet), mResult[0]));
					closePrinter(tmp);
				}
			}
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class PrintLogoTask extends AsyncTask<byte[], Void, Boolean> {
		private byte[] mResult;
		PrintLogoTask(byte[] result) {
			mResult = result;
		}
		
		protected Boolean doInBackground(byte[]... bmpBuf) {
			Boolean bRet = false;
			byte[] tmp = new byte[1];
			bRet = openPrinter(tmp);
			if (bRet == true)
			{
				if (tmp[0] == 0)
				{
					bRet = printLogo(metStatic7.getText().toString(), mResult);
					closePrinter(tmp);
				}
			}
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class StoreLogoTask extends AsyncTask<byte[], Void, Boolean> {
		private byte[] mResult;
		StoreLogoTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(byte[]... bmpBuf) {
			Boolean bRet = false;
			bRet = storeLogo(metStatic5.getText().toString(), 1, bmpBuf[0], bmpBuf[0].length, mResult);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class PrinterStatusTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		PrinterStatusTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			byte[] tmp = new byte[1];
			bRet = openPrinter(tmp);
			if (bRet == true)
			{
				if (tmp[0] == 0)
				{
					bRet = getPrinterStatus(mResult);
					closePrinter(tmp);
				}
			}
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				if (mResult[0] == 0)
				{
					strResult = "OK";
				}
				else
				{
					strResult = "KO (" + mResult[0]+ ") Reason(s):\n";
					if ((mResult[0] & 0x10) == 0x10)
						strResult += "- PRINTER ERROR\n";
					if ((mResult[0] & 0x20) == 0x20)
						strResult += "- PAPER OUT\n";
					if ((mResult[0] & 0x40) == 0x40)
						strResult += "- BT PRINTER NOT CONNECTED\n";
					if ((mResult[0] & 0x80) == 0x80)
						strResult += "- PRINTER BATTERY LOW\n";
					if ((mResult[0] & 0x01) == 0x01)
						strResult += "- OTHER ERROR\n";
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class PrinterOpenTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		PrinterOpenTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			bRet = openPrinter(mResult);
			
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				if (mResult[0] == 0)
				{
					strResult = "OK";
				}
				else
				{
					strResult = "KO (" + mResult[0]+ ") Reason(s):\n";
					if ((mResult[0] & 0x10) == 0x10)
						strResult += "- PRINTER ERROR\n";
					if ((mResult[0] & 0x20) == 0x20)
						strResult += "- PAPER OUT\n";
					if ((mResult[0] & 0x40) == 0x40)
						strResult += "- BT PRINTER NOT CONNECTED\n";
					if ((mResult[0] & 0x80) == 0x80)
						strResult += "- PRINTER BATTERY LOW\n";
					if ((mResult[0] & 0x01) == 0x01)
						strResult += "- OTHER ERROR\n";
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class PrinterCloseTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		PrinterCloseTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			bRet = closePrinter(mResult);
			
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				if (mResult[0] == 0)
				{
					strResult = "OK";
				}
				else
				{
					strResult = "KO (" + mResult[0]+ ") Reason(s):\n";
					if ((mResult[0] & 0x10) == 0x10)
						strResult += "- PRINTER ERROR\n";
					if ((mResult[0] & 0x20) == 0x20)
						strResult += "- PAPER OUT\n";
					if ((mResult[0] & 0x40) == 0x40)
						strResult += "- BT PRINTER NOT CONNECTED\n";
					if ((mResult[0] & 0x80) == 0x80)
						strResult += "- PRINTER BATTERY LOW\n";
					if ((mResult[0] & 0x01) == 0x01)
						strResult += "- OTHER ERROR\n";
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class PrintBitmapObjectTask extends AsyncTask<Bitmap, Void, Boolean> {
		private byte[] mResult;
		PrintBitmapObjectTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Bitmap... bmp) {
			Boolean bRet = false;
			byte[] tmp = new byte[1];
			bRet = openPrinter(tmp);
			if (bRet == true)
			{
				if (tmp[0] == 0)
				{
					bRet = printBitmapObject(bmp[0], mResult);
					closePrinter(tmp);
				}
			}
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}

	class PrinterSetFontTask extends AsyncTask<Integer, Void, Boolean> {
		private byte[] mResult;
		PrinterSetFontTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Integer... font) {
			Boolean bRet = false;
			StringBuilder sb = new StringBuilder();
		    
			if (font[0] == Fonts.ISO8859_1.ordinal())
			{
				sb.append("English: Welcome\n");
			}
			else if (font[0] == Fonts.ISO8859_2.ordinal())
		    {
			    // Czech (ISO8859-2)
			    sb.append("Czech: Nechť již hříšné saxofony ďáblů rozzvučí síň úděsnými tóny waltzu, tanga a quickstepu.\n");
			    // Polish (ISO8859-2)
			    sb.append("Polish: Pożądany\n");
		    }
		    else if (font[0] == Fonts.ISO8859_3.ordinal())
		    {
			    // Turkish (ISO8859-3)
			    sb.append("Turkish: Günaydin\n");
		    }
		    else if (font[0] == Fonts.ISO8859_5.ordinal())
		    {
			    // Russian (ISO8859-5)
			    sb.append("Russian: ДОБРО ПОЖАЛОВАТЬ\n");
		    }
		    else if (font[0] == Fonts.ISO8859_6.ordinal())
		    {	
			    // Arabic (ISO8859-6)
			    //sb.append("Arabic: ﺍﻟﺴﻼﻡ ﻋﻠﻴﻜﻢ\n");
		    	sb.append("Arabic: ");
			    byte[] array = new byte[13];
			    array[0] = (byte) 0xC7;
			    array[1] = (byte) 0xE4;
			    array[2] = (byte) 0xD3;
			    array[3] = (byte) 0xE4;
			    array[4] = (byte) 0xC7;
			    array[5] = (byte) 0xE5;
			    array[6] = (byte) 0x20;
			    array[7] = (byte) 0xD9;
			    array[8] = (byte) 0xE4;
			    array[9] = (byte) 0xEA;
			    array[10] = (byte) 0xE3;
			    array[11] = (byte) 0xE5;
			    array[12] = (byte) '\n';
			    try {
					sb.append(new String(array, "ISO-8859-6"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
		    }
		    else if (font[0] == Fonts.ISO8859_7.ordinal())
		    {
		    	// Greek (ISO8859-7)
		    	sb.append("Greek: καλημέρα\n");
		    }
		    else if (font[0] == Fonts.ISO8859_15.ordinal())
		    {
		    	sb.append("French: Bienvenue\n");
		    }
			
			bRet = printerSetFont(font[0], mResult);
			
			byte[] tmp = new byte[1];
			bRet = openPrinter(tmp);
			Log.d(TAG, String.format("openPrinter bRet=%s mResult=%d", Boolean.toString(bRet), tmp[0]));
			if (bRet == true)
			{
				if (tmp[0] == 0)
				{
					bRet = printText(sb.toString(), mResult);
					closePrinter(tmp);
				}
			}
			
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				if (mResult[0] == 0)
				{
					strResult = "OK";
				}
				else
				{
					strResult = "KO (" + mResult[0]+ ") Reason(s):\n";
					if ((mResult[0] & 0x10) == 0x10)
						strResult += "- PRINTER ERROR\n";
					if ((mResult[0] & 0x20) == 0x20)
						strResult += "- PAPER OUT\n";
					if ((mResult[0] & 0x40) == 0x40)
						strResult += "- BT PRINTER NOT CONNECTED\n";
					if ((mResult[0] & 0x80) == 0x80)
						strResult += "- PRINTER BATTERY LOW\n";
					if ((mResult[0] & 0x01) == 0x01)
						strResult += "- OTHER ERROR\n";
				}
			}
			mtvResult.setText(strResult);
		}
	}

	class PrinterCashDrawerTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		PrinterCashDrawerTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			bRet = openCashDrawer(mResult);
			
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class OpenBarcodeTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		private int mInactivityTo;
		OpenBarcodeTask(int inactivityTo, byte[] result) {
			mResult = result;
			mInactivityTo = inactivityTo;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			bRet = openBarcode(mInactivityTo, mResult);
			Log.d(TAG, String.format("openBarcode bRet=%s mResult=%d", Boolean.toString(bRet), mResult[0]));
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO (" + mResult[0] + ")";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class CloseBarcodeTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		CloseBarcodeTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			bRet = closeBarcode(mResult);
			Log.d(TAG, String.format("closeBarcode bRet=%s mResult=%d", Boolean.toString(bRet), mResult[0]));
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO (" + mResult[0] + ")";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class ResetBarcodeTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		ResetBarcodeTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			bRet = resetBarcode(mResult);
			Log.d(TAG, String.format("resetBarcode bRet=%s mResult=%d", Boolean.toString(bRet), mResult[0]));
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					break;
				default:
					strResult = "KO (" + mResult[0] + ")";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class ScanBarcodeTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		ScanBarcodeTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			bRet = scanBarcode(mResult);
			Log.d(TAG, String.format("scanBarcode bRet=%s mResult=%d", Boolean.toString(bRet), mResult[0]));
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					strResult = "OK";
					if (miScanState == 1)
					{
						mbtnRun.setText("Stop Scan");
						miScanState = 2;
					}
					else
					{
						mbtnRun.setText("Start Scan");
						miScanState = 1;
					}
					break;
				default:
					strResult = "KO (" + mResult[0] + ")";
					break;
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class GetBarcodeVersionTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mVersion;
		GetBarcodeVersionTask(byte[] version) {
			mVersion = version;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			bRet = getBarcodeVersion(mVersion);
			Log.d(TAG, String.format("getBarcodeVersion bRet=%s mVersion=%s", Boolean.toString(bRet), new String(mVersion)));
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult = "N/A";
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			if (result == true)
			{
				strResult = new String(mVersion);
			}
			mtvResult.setText(strResult);
		}
	}
	
	class DoUpdateTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] resp;
		public DoUpdateTask() {
			resp = new byte[4];
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = doUpdate(resp);
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			String strResult = "N/A";
			if (result == true)
			{
				if (resp[0] == 0)
					strResult = "OK";
				else
					strResult = "KO";
			}
			mtvResult.setText(strResult);		
		}
	}
	
	class SetTmsParametersTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] resp;
		private String mIpAddress;
		private String mPort;
		private String mIdentifier;
		private String mSslProfile;
		public SetTmsParametersTask(String ip_addr, String port, String identifier, String ssl_profile) {
			resp = new byte[4];
			mIpAddress = ip_addr;
			mPort = port;
			mIdentifier = identifier;
			mSslProfile = ssl_profile;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet;
			if (mSslProfile.equalsIgnoreCase("No change"))
			{
				bRet = setTmsParameters(mIpAddress, mPort, mIdentifier, null, resp);
			}
			else if (mSslProfile.equalsIgnoreCase("No SSL"))
			{
				bRet = setTmsParameters(mIpAddress, mPort, mIdentifier, "", resp);
			}
			else
			{
				bRet = setTmsParameters(mIpAddress, mPort, mIdentifier, mSslProfile, resp);
			}
						
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			String strResult = "N/A";
			if (result == true)
			{
				if (resp[0] == 0)
				{
					strResult = "OK";
				}
				else
				{
					strResult = "KO";
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class GetTmsParametersTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] resp;
		private String[] mIpAddress;
		private String[] mPort;
		private String[] mIdentifier;
		private String[] mSslProfile;
		private String[] mCurrentSslProfile;
		public GetTmsParametersTask() {
			resp = new byte[1];
			mIpAddress = new String[1];
			mPort = new String[1];
			mIdentifier = new String[1];
			mSslProfile = new String[20];
			mCurrentSslProfile = new String[1];
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet;
			bRet = getTmsParameters(mIpAddress, mPort, mIdentifier, mSslProfile, mCurrentSslProfile, resp);
						
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvStatus.setText(Boolean.toString(result));
			mTestEndTime = System.currentTimeMillis();
			mtvStatus.append("\nDuration:" + String.format("%d.%03ds", (mTestEndTime-mTestStartTime)/1000, (mTestEndTime-mTestStartTime)%1000));
			String strResult = "N/A";
			int selected = -1;
			if (result == true)
			{
				if (resp[0] == 0)
				{
					ArrayAdapter<String> spinnerArrayAdapter;
					strResult = "OK";
					metStatic3.setText(mIpAddress[0]);
					metStatic4.setText(mPort[0]);
					metStatic5.setText(mIdentifier[0]);
					spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item);
					spinnerArrayAdapter.add("No change");
					spinnerArrayAdapter.add("No SSL");
					Log.d(TAG, "current:" + mCurrentSslProfile[0]);
					for (int i=0; i<20; i++)
					{
						if ((mSslProfile[i] != null) && (mSslProfile[i].length() != 0))
						{
							spinnerArrayAdapter.add(mSslProfile[i]);
							if (mSslProfile[i].equalsIgnoreCase(mCurrentSslProfile[0]))
							{
								selected = i+2;
							}
						}
					}
		        	msSpinner3.setAdapter(spinnerArrayAdapter);
		        	
		        	if (selected != -1)
		        	{
						msSpinner3.setSelection(selected);		
					}
		        	else
		        	{
		        		msSpinner3.setSelection(1);	
		        	}
				}
				else
				{
					strResult = "KO";
				}
			}
			mtvResult.setText(strResult);
		}
	}
	
	class NetworkTask extends AsyncTask<Void, Integer, Boolean> {
		private InputStream mmTcpInStream;
		private OutputStream mmTcpOutStream;
		private Socket mmTcpSocket;
		public ServerSocket mmTcpServerSocket;
		public boolean mmRunning;
		private int mPort;
		private int mTailleBuf;
		private int mLoopsNb;
		boolean ismAndroidServer;
		private long time_elapsed = 0;
		String strResult;
		private int mCount;

		NetworkTask(int port, int tailleBuf, int loopsNb, boolean isAndroidServer) {
			mmTcpSocket = null;
			mmTcpServerSocket = null;
			mmTcpInStream = null;
			mmTcpOutStream = null;
			mPort = port;
			mTailleBuf = tailleBuf;
			mLoopsNb = loopsNb;
			ismAndroidServer = isAndroidServer;
			mCount = 0;
		}

		protected Boolean doInBackground(Void... dummy) {
			Log.d(TAG, "TcpThread: Started");
			byte[] buffer = new byte[mTailleBuf];  // buffer store for the stream
			int total;
			int bytes;

			for (int i=0; i<mTailleBuf; i++)
			{
				buffer[i] = (byte) i;
			}

			mmRunning = true;

			try {
				if(ismAndroidServer)
				{
					Log.d(TAG, "TcpThread: Waiting for connection...");
					mmTcpServerSocket = new ServerSocket(mPort);
				}
				else
				{
					Log.d(TAG, "TcpThread: Connecting...");
					mmTcpSocket = new Socket("127.0.0.1", mPort);
				}

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
				Log.e(TAG, "TcpThread: UnknownHostException");
				return Boolean.FALSE;
			} catch (IOException e1) {
				e1.printStackTrace();
				Log.e(TAG, "TcpThread: IOException");
				return Boolean.FALSE;
			}
			
			if(ismAndroidServer)
			{
				try 
				{
					mmTcpSocket = mmTcpServerSocket.accept();
				} catch (IOException e2) {
					e2.printStackTrace();
					try {
						mmTcpServerSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if (mmTcpSocket != null) {

				Log.d(TAG, "TcpThread: Connected");
				try {
					mmTcpInStream = mmTcpSocket.getInputStream();
					mmTcpOutStream = mmTcpSocket.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace();
					Log.e(TAG, "TcpThread: IOException");
					try {
						mmTcpSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return Boolean.FALSE;
				}


				if (mLoopsNb > 0) {
					long start_time = System.currentTimeMillis();
					Log.d(TAG, String.format("TcpThread: Start sending %d", buffer.length));
					mCount = 0;
					while (mmRunning)
					{
						if (isCancelled())
						{
							Log.d(TAG, String.format("TcpThread: Cancelled"));
							closeStreams();
							break;
						}
						if ( mmTcpOutStream != null ) {
							try {
								//Log.d(TAG, "TcpThread: Write");
								mmTcpOutStream.write(buffer, 0, mTailleBuf);
							} catch (IOException e1) {
								e1.printStackTrace();
								Log.e(TAG, "TcpThread: Write IOException");
								closeStreams();
								break;
							}
						}

						if ( mmTcpInStream != null ) {
							total = 0;
							while (total < mTailleBuf)
							{
								try {
									bytes = mmTcpInStream.read(buffer);
									if ( bytes == -1 ) {	//end of the stream
										Log.e(TAG, "TcpThread: End of stream");
										closeStreams();
										break;
									}
									else {
										total += bytes;
									}
								} catch (IOException e) {
									Log.e(TAG, "TcpThread: IOException(read)");
									e.printStackTrace();
									closeStreams();
									break;
								}
							}
							if (total == mTailleBuf)
								mCount++;
						}
						else
						{
							break;
						}
						
						if (mCount%10 == 0)
							publishProgress(mCount);
						if (mCount >= mLoopsNb)
							break;
					}
					Log.d(TAG, String.format("TcpThread: Stop sending (count=%d)", mCount));
					long stop_time = System.currentTimeMillis();
					time_elapsed = stop_time - start_time;
					Log.d(TAG, String.format("START = %d | STOP = %d | TIME = %d",start_time,stop_time,time_elapsed));
					//mtvStatus.setText("OK\nEND OF TEST\n\n" );
					Log.d(TAG,String.format("Bandwith = %d bits/second", (8*((1000* mTailleBuf * mCount *2)/time_elapsed))));
				}
			}
			
			if(ismAndroidServer)
			{
				// wait for other side to close connection
				if ( mmTcpInStream != null ) {
					
					while (true)
					{
						try {
							bytes = mmTcpInStream.read(buffer);
							if ( bytes == -1 ) {	//end of the stream
								Log.e(TAG, "TcpThread: End of stream");
								closeStreams();
								break;
							}
						} catch (IOException e) {
							Log.e(TAG, "TcpThread: IOException(read)");
							e.printStackTrace();
							closeStreams();
							break;
						}
					}
				}
				
				try
				{
					mmTcpServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			else
			{
				closeStreams();
			}
			Log.d(TAG, "TcpThread: Ended");
			return Boolean.TRUE;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			mtvStatus.setText(String.format("TEST IN PROGRESS...\nPackets: %d", values[0]));
			super.onProgressUpdate(values);
		}

		protected void onPostExecute(Boolean result) {
			if (result == true && mCount == mLoopsNb)
			{
				strResult = "OK";
				if (time_elapsed != 0)
					mtvResult.setText(String.format("Transfer rate = %d kbits/second", (8*mTailleBuf*mCount*2)/time_elapsed));
			}
			else
			{
				strResult = "KO";
			}

			mtvStatus.setText(strResult + "\nEND OF TEST");
		}

		private void closeStreams() {
			if (mmTcpInStream != null)
			{
				try {
					mmTcpInStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				mmTcpInStream = null;
			}
			if (mmTcpOutStream != null)
			{
				synchronized (mmTcpOutStream)
				{
					try {
						mmTcpOutStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mmTcpOutStream = null;
				}
			}
			if (mmTcpSocket != null)
			{
				try {
					mmTcpSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mmTcpSocket = null;
			}
		}
	}

	
    /*********************************************************************************/
    /*********************************************************************************/
    /*********************************************************************************/
    /*********************************************************************************/

	/* PCLService functions */
	
	private boolean openPrinter(byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			{
				ret = mPclService.openPrinter(result);
			}
		}
		else {
			Log.d(TAG, "openPrinter: mPclService is null");
		}
		return ret;
	}

	private boolean closePrinter(byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			{
				ret = mPclService.closePrinter(result);
			}
		}
		return ret;
	}

	private boolean printText(String strText, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			{
				ret = mPclService.printText(strText, result);
				Log.d(TAG, String.format("printText result=%d", result[0]));
			}
		}
		return ret;
	}

	private boolean printBitmap( byte[] bmpBuf, int bmpSize, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			{
				ret = mPclService.printBitmap(bmpBuf, bmpSize, result);
			}
		}
		return ret;
	}

    private boolean printBitmapObject( Bitmap bmp, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.printBitmapObject(bmp, result);
		}
		return ret;
	}
	
    private boolean printerSetFont(Integer font, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.setPrinterFont(font, result);
		}
		return ret;
	}
	
	private boolean openCashDrawer(byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			{
				ret = mPclService.openCashDrawer(result);
			}
		}
		else {
			Log.d(TAG, "openCashDrawer: mPclService is null");
		}
		return ret;
	}    
	
	private boolean openBarcode(int inactivityTo, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			if (inactivityTo == -1)
			{
				ret = mPclService.openBarcode(result);
			}
			else
			{
				ret = mPclService.openBarcodeWithInactivityTo(inactivityTo, result);
			}
		}
		else {
			Log.d(TAG, "openBarcode: mPclService is null");
		}
		return ret;
	}

	private boolean closeBarcode(byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			{
				ret = mPclService.closeBarcode(result);
			}
		}
		return ret;
	}

	private boolean resetBarcode(byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrSoftReset(result);
		}
		return ret;
	}
	
	private boolean scanBarcode(byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			if (miScanState == 1)
			{
				ret = mPclService.bcrStartScan(result);
			}
			else
			{
				ret = mPclService.bcrStopScan(result);
			}
		}
		return ret;
	}
	
	private boolean getBarcodeVersion(byte[] version) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrGetFirmwareVersion(version);
		}
		return ret;
	}
	
	
	private void initTmsParameters() {
		RelativeLayout.LayoutParams lp;
		mtvDescription.setText("" +
				"Description: \nThe Android device shall be able to get/set TMS parameters of the Bluetooth companion.");
		mtvStatic3.setText("IP addr or host: ");
    	mtvStatic3.setVisibility(View.VISIBLE);
    	lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	lp.addRule(RelativeLayout.RIGHT_OF, mtvStatic3.getId());
    	lp.addRule(RelativeLayout.ALIGN_TOP, mtvStatic3.getId());
    	metStatic3.setText("");
    	metStatic3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    	metStatic3.setLayoutParams(lp);
    	metStatic3.setVisibility(View.VISIBLE);
    	
    	mtvStatic4.setText("TCP Port: ");
    	mtvStatic4.setVisibility(View.VISIBLE);
    	lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	lp.addRule(RelativeLayout.RIGHT_OF, mtvStatic4.getId());
    	lp.addRule(RelativeLayout.ALIGN_TOP, mtvStatic4.getId());
    	metStatic4.setText("");
    	metStatic4.setInputType(InputType.TYPE_CLASS_NUMBER);
    	metStatic4.setWidth(LayoutParams.MATCH_PARENT);    	
    	metStatic4.setLayoutParams(lp);
    	metStatic4.setVisibility(View.VISIBLE);
    	
    	mtvStatic8.setText("Identifier: ");
    	mtvStatic8.setVisibility(View.VISIBLE);
    	lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	lp.addRule(RelativeLayout.RIGHT_OF, mtvStatic8.getId());
    	lp.addRule(RelativeLayout.ALIGN_TOP, mtvStatic8.getId());
    	metStatic5.setText("");
    	metStatic5.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    	metStatic5.setWidth(LayoutParams.MATCH_PARENT);    	
    	metStatic5.setLayoutParams(lp);
    	metStatic5.setVisibility(View.VISIBLE);
    	
    	mtvStatic10.setText("SSL profiles: ");
    	mtvStatic10.setVisibility(View.VISIBLE);
    	
    	lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	lp.addRule(RelativeLayout.RIGHT_OF, mtvStatic10.getId());
    	lp.addRule(RelativeLayout.ALIGN_TOP, mtvStatic10.getId());
    	msSpinner3.setLayoutParams(lp);
    	msSpinner3.setVisibility(View.VISIBLE);
	}
	
	private static void hideKeyboard(Context context, View view) {
	    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}
