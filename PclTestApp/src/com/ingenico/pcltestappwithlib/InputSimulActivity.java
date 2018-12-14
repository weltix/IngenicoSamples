package com.ingenico.pcltestappwithlib;

import com.ingenico.pclservice.PclService;
import com.ingenico.pclservice.PclService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class InputSimulActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = "PCLINPUTSIMUL";
	private TextView mTextViewScreen;
	
	private PclService service;
    private PclServiceConnection mServiceConnection;
    private int mReleaseService;
    boolean mBound = false;
    
    class PclServiceConnection implements ServiceConnection
    {
        public void onServiceConnected(ComponentName className, 
			IBinder boundService )
        {
          //service = IPclService.Stub.asInterface((IBinder)boundService);
        	LocalBinder binder = (LocalBinder) boundService;
    		service = (PclService) binder.getService();
    		mBound = true;
		  Log.d(TAG, "onServiceConnected" );
        }

        public void onServiceDisconnected(ComponentName className)
        {
          service = null;
          mBound = false;
		  Log.d(TAG, "onServiceDisconnected" );
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputsimul);
		mReleaseService = 1;
		final PclServiceConnection data = (PclServiceConnection) getLastCustomNonConfigurationInstance();
        if (data == null) {
        	initService();
        }
        else
        {
        	mServiceConnection = data;
        }
		findViewById(R.id.button_F1).setOnClickListener(this);
		findViewById(R.id.button_F2).setOnClickListener(this);
		findViewById(R.id.button_F3).setOnClickListener(this);
		findViewById(R.id.button_F4).setOnClickListener(this);
		findViewById(R.id.button_UP).setOnClickListener(this);
		findViewById(R.id.button_DOWN).setOnClickListener(this);
		findViewById(R.id.button_OK).setOnClickListener(this);
		findViewById(R.id.button_Num1).setOnClickListener(this);
		findViewById(R.id.button_Num2).setOnClickListener(this);
		findViewById(R.id.button_Num3).setOnClickListener(this);
		findViewById(R.id.button_Num4).setOnClickListener(this);
		findViewById(R.id.button_Num5).setOnClickListener(this);
		findViewById(R.id.button_Num6).setOnClickListener(this);
		findViewById(R.id.button_Num7).setOnClickListener(this);
		findViewById(R.id.button_Num8).setOnClickListener(this);
		findViewById(R.id.button_Num9).setOnClickListener(this);
		findViewById(R.id.button_Num0).setOnClickListener(this);
		findViewById(R.id.button_F).setOnClickListener(this);
		findViewById(R.id.button_Dot).setOnClickListener(this);
		findViewById(R.id.button_Cancel).setOnClickListener(this);
		findViewById(R.id.button_Corr).setOnClickListener(this);
		findViewById(R.id.button_Valid).setOnClickListener(this);
		findViewById(R.id.button_test).setOnClickListener(this);
		mTextViewScreen = (TextView)findViewById(R.id.textView_screen);
		
	}
	
	@Override
	public Object onRetainCustomNonConfigurationInstance() {
    	mReleaseService = 0;
    	return mServiceConnection;
	}

	@Override
    protected void onDestroy() {
		Log.d(TAG, "onDestroy" );
        super.onDestroy();
        if (mReleaseService == 1)
        	releaseService();
    }

	@Override
	public void onClick(View v) {
		byte[] key = new byte[6];
		key[1] = 0;
		switch (v.getId()) {
		case R.id.button_F1:
			key[0] = 0x19;
			break;
		case R.id.button_F2:
			key[0] = 0x20;
			break;
		case R.id.button_F3:
			key[0] = 0x21;
			break;
		case R.id.button_F4:
			key[0] = 0x22;
			break;
		case R.id.button_OK:
			key[0] = 0x25;
			break;
		case R.id.button_UP:
			key[0] = 0x23;
			break;
		case R.id.button_DOWN:
			key[0] = 0x24;
			break;
		case R.id.button_Num1:
			key[0] = 0x31;
			break;
		case R.id.button_Num2:
			key[0] = 0x32;
			break;
		case R.id.button_Num3:
			key[0] = 0x33;
			break;
		case R.id.button_Num4:
			key[0] = 0x34;
			break;
		case R.id.button_Num5:
			key[0] = 0x35;
			break;
		case R.id.button_Num6:
			key[0] = 0x36;
			break;
		case R.id.button_Num7:
			key[0] = 0x37;
			break;
		case R.id.button_Num8:
			key[0] = 0x38;
			break;
		case R.id.button_Num9:
			key[0] = 0x39;
			break;
		case R.id.button_Num0:
			key[0] = 0x30;
			break;
		case R.id.button_F:
			key[0] = 0x28;
			break;
		case R.id.button_Dot:
			key[0] = '.';
			break;
		case R.id.button_Cancel:
			key[0] = 0x17;
			break;
		case R.id.button_Corr:
			key[0] = 0x18;
			break;
		case R.id.button_Valid:
			key[0] = 0x16;
			break;
		case R.id.button_test:
			key[0] = 0x34;
			key[1] = 0x39;
			key[2] = 0x32;
			key[3] = 0x36;
			key[4] = 0x16;
			key[5] = 0;
			break;
		default:
			key[0] = 0;
			break;
		}
		
		new InputSimulTask(key).execute();
	}
	
	class InputSimulTask extends AsyncTask<Void, Void, Boolean> {
		byte[] m_keys;
		InputSimulTask(byte[] keys)
		{
			m_keys = keys.clone();
		}
        protected Boolean doInBackground(Void... dummy) {
        	Boolean bRet = inputSimul(m_keys);
            return bRet;
        }

        protected void onPostExecute(Boolean result) {
            mTextViewScreen.setText(Boolean.toString(result));
        }
    }

	private boolean inputSimul(byte[] key) {
        boolean ret = false;
        if( service == null )
        	mTextViewScreen.setText( "Service not available" );
    	  else {
    		{
    		  ret = service.inputSimul(key);
    		}
    	  }
    	  return ret;
      	
    }
	
	private void initService() {
		Log.d(TAG, "initService" );
		mServiceConnection = new PclServiceConnection();
		/*
		Intent i = new Intent();
		i.setClassName( "com.ingenico.pclservice", "com.ingenico.pclservice.PclService" );
		getApplicationContext().bindService( i, mServiceConnection, Context.BIND_AUTO_CREATE);
		*/
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
}
