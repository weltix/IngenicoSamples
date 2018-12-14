package com.ingenico.pcltestappwithlib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.ingenico.pclservice.IPclService;
import com.ingenico.pclservice.PclService;
import com.ingenico.pclservice.TransactionIn;
import com.ingenico.pclservice.TransactionOut;
import com.ingenico.pclservice.PclService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class PclLoopTestActivity extends FragmentActivity implements OnClickListener {

	private static final String TAG = "PCLLOOPTEST";
	private IPclService mPclService;
	private PclServiceConnection mServiceConnection;
	private int mReleaseService;
	private TextView mtvResult;
	private EditText metNbrLoops;
	private EditText metDelay;
	private int mNbrLoops;
	private int mLoopCounter;
	private int mOkCounter;
	private int mKoCounter;
	private int mDelay;
	private AsyncTask mCurrentTask = null;
	private String mCurrentTest;
	boolean mBound = false;
	
	static class PclObject {
		PclServiceConnection serviceConnection;
		IPclService service;
	}

	class PclServiceConnection implements ServiceConnection
	{
		public void onServiceConnected(ComponentName className, 
				IBinder boundService )
		{
			//mPclService = IPclService.Stub.asInterface((IBinder)boundService);
			LocalBinder binder = (LocalBinder) boundService;
			mPclService = (IPclService) binder.getService();
    		mBound = true;
			Log.d(TAG, "onServiceConnected" );
		}

		public void onServiceDisconnected(ComponentName className)
		{
			mPclService = null;
			mBound = false;
			Log.d(TAG, "onServiceDisconnected" );
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.looptest);
		findViewById(R.id.button_loop_do_transaction).setOnClickListener(this);
		findViewById(R.id.button_loop_print_text).setOnClickListener(this);
		findViewById(R.id.button_loop_printbmp).setOnClickListener(this);
		findViewById(R.id.button_loop_stop).setOnClickListener(this);
		mtvResult = (TextView)findViewById(R.id.textView_loop_result);
		metNbrLoops = (EditText)findViewById(R.id.editText_loops_number);
		metDelay = (EditText)findViewById(R.id.editText_delay);
		mReleaseService = 1;
		final PclObject data = (PclObject) getLastCustomNonConfigurationInstance();
		if (data == null || data.service == null || data.serviceConnection == null) {
			Log.d(TAG, "onCreate: Init service" );
			initService();
		}
		else
		{
			Log.d(TAG, "onCreate: Service already initialized" );
			mServiceConnection = data.serviceConnection;
			mPclService = data.service;
		}
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		mReleaseService = 0;
		PclObject obj = new PclObject();
		obj.service = mPclService;
		obj.serviceConnection = mServiceConnection;
		return obj;
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy" );
		super.onDestroy();
		if (mReleaseService == 1)
		{
			releaseService();
			if (mCurrentTask != null)
			{
				mCurrentTask.cancel(true);
			}
		}
	}
	
	private void initService() {
		Log.d(TAG, "initService" );
		mServiceConnection = new PclServiceConnection();
		//Intent i = new Intent();
		//i.setClassName( "com.ingenico.pclservice", "com.ingenico.pclservice.PclService" );
		//getApplicationContext().bindService( i, mServiceConnection, Context.BIND_AUTO_CREATE);
		Intent intent = new Intent(this, PclService.class);
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void releaseService() {
		if (mServiceConnection != null && mBound) {
			Log.d(TAG, "releaseService" );
			unbindService( mServiceConnection );	  
			mServiceConnection = null;
			mBound = false;
		}
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.button_loop_do_transaction:
			onClickLoopDoTransaction();
			break;
		case R.id.button_loop_print_text:
			onClickLoopPrintText();
			break;
		case R.id.button_loop_printbmp:
			onClickLoopPrintBitmap();
			break;
		case R.id.button_loop_stop:
			onClickLoopStop();
			break;
		}
		
	}

	private void onClickLoopDoTransaction() {
		mCurrentTest = "Do Transaction";
		mtvResult.setText(mCurrentTest);
		TransactionIn transIn = new TransactionIn();
		TransactionOut transOut = new TransactionOut();
		String amount = "995";
		if (metNbrLoops.getText().length() == 0)
		{
			mNbrLoops = 1;
		}
		else
		{
			mNbrLoops = Integer.parseInt(metNbrLoops.getText().toString());
		}
		if (metDelay.getText().length() == 0)
		{
			mDelay = 0;
		}
		else
		{
			mDelay = Integer.parseInt(metDelay.getText().toString());
		}

		transIn.setAmount(amount);
		transIn.setCurrencyCode("978");
		transIn.setOperation("C");
		transIn.setTermNum("58");
		mLoopCounter = 0;
		mOkCounter = 0;
		mKoCounter = 0;
		
		mCurrentTask = new DoTransactionTask(transIn, transOut).execute();

	}

	class DoTransactionTask extends AsyncTask<Void, Void, Boolean> {
		private TransactionIn transIn;
		private TransactionOut transOut;
		public DoTransactionTask(TransactionIn transIn, TransactionOut transOut) {
			this.transIn = transIn;
			this.transOut = transOut;
		}

		protected Boolean doInBackground(Void... tmp) {
			if ((mLoopCounter > 0) && (mLoopCounter < mNbrLoops))
			{
				try {
					Thread.sleep(mDelay*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Boolean bRet = doTransaction(transIn, transOut);
			mLoopCounter++;
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvResult.setText(String.format("%s: %d/%d ", mCurrentTest, mLoopCounter, mNbrLoops));
			if (result == true) {
				if (transOut.getC3Error().equals("0000"))
				{
					mOkCounter++;
				}
				else
				{
					mKoCounter++;
				}
			}
			else
			{
				mKoCounter++;
			}
			mtvResult.append(String.format("OK:%d KO:%d", mOkCounter, mKoCounter));
			if (mLoopCounter < mNbrLoops)
			{
				mCurrentTask = new DoTransactionTask(transIn, transOut).execute();
			}
			else
			{
				mCurrentTask = null;
			}
		}
	}
	
	private boolean doTransaction(TransactionIn transIn, TransactionOut transOut) {
		boolean ret = false;

		if( mPclService != null ) {
			
			ret = mPclService.doTransaction(transIn, transOut);
			
		}
		return ret;

	}
	
	private void onClickLoopPrintBitmap() {
		byte[] result = new byte[1];
		mCurrentTest = "Print Bitmap";
		mtvResult.setText(mCurrentTest);
		
		File bmp = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/logo_313x128_256colors.bmp");
		int len = (int) bmp.length();
		FileInputStream in = null;
		try {
			in = new FileInputStream(bmp);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedInputStream buf = new BufferedInputStream(in);
		byte[] bMapArray = null;
		bMapArray = new byte[len];
		try {
			buf.read(bMapArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mLoopCounter = 0;
		mOkCounter = 0;
		mKoCounter = 0;
		if (metNbrLoops.getText().length() == 0)
		{
			mNbrLoops = 1;
		}
		else
		{
			mNbrLoops = Integer.parseInt(metNbrLoops.getText().toString());
		}
		if (metDelay.getText().length() == 0)
		{
			mDelay = 0;
		}
		else
		{
			mDelay = Integer.parseInt(metDelay.getText().toString());
		}
		
		mCurrentTask = new PrintBitmapTask(result).execute(bMapArray);

	}
	
	class PrintBitmapTask extends AsyncTask<byte[], Void, Boolean> {
		private byte[] mResult;
		private byte[] mbMapArray;
		PrintBitmapTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(byte[]... bmpBuf) {
			Boolean bRet = false;
			byte[] tmp = new byte[1];
			mbMapArray = bmpBuf[0];
			if ((mLoopCounter > 0) && (mLoopCounter < mNbrLoops))
			{
				try {
					Thread.sleep(mDelay*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			bRet = openPrinter(tmp);
			if (bRet == true)
			{
				if (tmp[0] == 0)
				{
					bRet = printBitmap(bmpBuf[0], bmpBuf[0].length, mResult);
					closePrinter(tmp);
				}
			}
			mLoopCounter++;
			
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvResult.setText(String.format("%s: %d/%d ", mCurrentTest, mLoopCounter, mNbrLoops));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					mOkCounter++;
					break;
				default:
					mKoCounter++;
					break;
				}
			}
			else
			{
				mKoCounter++;
			}
			mtvResult.append(String.format("OK:%d KO:%d", mOkCounter, mKoCounter));
			if (mLoopCounter < mNbrLoops)
			{
				mCurrentTask = new PrintBitmapTask(mResult).execute(mbMapArray);
			}
			else
			{
				mCurrentTask = null;
			}
		}
	}
	
	private boolean openPrinter(byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			
			ret = mPclService.openPrinter(result);
			
		}
		else {
			Log.d(TAG, "openPrinter: mPclService is null");
		}
		return ret;
	}

	private boolean closePrinter(byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.closePrinter(result);
			
		}
		return ret;
	}
	
	private boolean printBitmap( byte[] bmpBuf, int bmpSize, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.printBitmap(bmpBuf, bmpSize, result);
			
		}
		return ret;
	}
	
	private void onClickLoopPrintText() {
		byte[] result = new byte[1];
		mCurrentTest = "Print Text";
		mtvResult.setText(mCurrentTest);
		mLoopCounter = 0;
		mOkCounter = 0;
		mKoCounter = 0;
		if (metNbrLoops.getText().length() == 0)
		{
			mNbrLoops = 1;
		}
		else
		{
			mNbrLoops = Integer.parseInt(metNbrLoops.getText().toString());
		}
		if (metDelay.getText().length() == 0)
		{
			mDelay = 0;
		}
		else
		{
			mDelay = Integer.parseInt(metDelay.getText().toString());
		}
		
		mCurrentTask = new PrintTextTask(result).execute("Print from Android ");

	}

	class PrintTextTask extends AsyncTask<String, Void, Boolean> {
		private byte[] mResult;
		private String mStr;
		PrintTextTask(byte[] result) {
			mResult = result;
		}
		protected Boolean doInBackground(String... str) {
			Boolean bRet = false;
			byte[] tmp = new byte[1];
			mStr = str[0];
			if ((mLoopCounter > 0) && (mLoopCounter < mNbrLoops))
			{
				try {
					Thread.sleep(mDelay*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			bRet = openPrinter(tmp);
			Log.d(TAG, String.format("openPrinter bRet=%s mResult=%d", Boolean.toString(bRet), tmp[0]));
			if (bRet == true)
			{
				if (tmp[0] == 0)
				{
					bRet = printText(str[0] + mLoopCounter, mResult);
					Log.d(TAG, String.format("printText bRet=%s mResult=%d", Boolean.toString(bRet), mResult[0]));
					closePrinter(tmp);
				}
			}
			mLoopCounter++;
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			mtvResult.setText(String.format("%s: %d/%d ", mCurrentTest, mLoopCounter, mNbrLoops));
			if (result == true)
			{
				switch (mResult[0]) {
				case 0:
					mOkCounter++;
					break;
				default:
					mKoCounter++;
					break;
				}
			}
			else
			{
				mKoCounter++;
			}
			mtvResult.append(String.format("OK:%d KO:%d", mOkCounter, mKoCounter));
			if (mLoopCounter < mNbrLoops)
			{
				mCurrentTask = new PrintTextTask(mResult).execute(mStr);
			}
			else
			{
				mCurrentTask = null;
			}
		}
	}
	
	private boolean printText(String strText, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			
				ret = mPclService.printText(strText, result);
				Log.d(TAG, String.format("printText result=%d", result[0]));
			
		}
		return ret;
	}
	
	private void onClickLoopStop() {
		if (mCurrentTask != null)
		{
			mCurrentTask.cancel(true);
			mtvResult.setText(String.format("%s: %d/%d OK:%d KO:%d", mCurrentTest, mLoopCounter, mNbrLoops, mOkCounter, mKoCounter));
		}
	}
}
