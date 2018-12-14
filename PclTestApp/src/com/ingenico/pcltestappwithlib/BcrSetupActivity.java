package com.ingenico.pcltestappwithlib;

import java.math.BigInteger;

import com.ingenico.pclservice.PclService;
import com.ingenico.pclservice.Symbologies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class BcrSetupActivity extends CommonActivity {

	Button mButtonApply;
	Button mButtonSave;
	Button mButtonSymbologies;
	Spinner mSpinnerReaderMode;
	Spinner mSpinnerGoodScanBeep;
	Spinner mSpinnerEnableTrigger;
	Spinner mSpinnerImagerMode;
	Spinner mSpinnerIlluminationMode;
	Spinner mSpinnerLightingMode;
	EditText mEditTextBeepFrequency;
	EditText mEditTextBeepDuration;
	EditText mEditTextVersion;
	
	static class PclObject {
		PclServiceConnection serviceConnection;
		PclService service;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bcr_setup);
		mReleaseService = 1;
		mButtonApply = (Button)findViewById(R.id.btnApply);
		mButtonSave = (Button)findViewById(R.id.btnApplyFlash);
		mButtonSymbologies = (Button)findViewById(R.id.btnSymbologies);
		mSpinnerReaderMode = (Spinner)findViewById(R.id.spReaderMode);
		mSpinnerGoodScanBeep = (Spinner)findViewById(R.id.spGoodScanBeep);
		mSpinnerEnableTrigger = (Spinner)findViewById(R.id.spEnableTrigger);
		mSpinnerImagerMode = (Spinner)findViewById(R.id.spImagerMode);
		mSpinnerIlluminationMode = (Spinner)findViewById(R.id.spIlluminationMode);
		mSpinnerLightingMode = (Spinner)findViewById(R.id.spLightingMode);
		mEditTextBeepFrequency = (EditText)findViewById(R.id.etBeepFrequency);
		mEditTextBeepDuration = (EditText)findViewById(R.id.etBeepLength);
		mEditTextVersion = (EditText)findViewById(R.id.etVersion);
		
		mButtonApply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				byte[] result = new byte[1];
				if ((mEditTextBeepFrequency.getText().length() == 0) ||
						(mEditTextBeepDuration.getText().length() == 0))
				{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.warning_params), Toast.LENGTH_LONG).show();
				}
				else
				{
					new ConfigureBcrTask(mSpinnerReaderMode.getSelectedItemPosition(),
							mSpinnerGoodScanBeep.getSelectedItemPosition(),
							mSpinnerEnableTrigger.getSelectedItemPosition(),
							mSpinnerImagerMode.getSelectedItemPosition(),
							Integer.parseInt(mEditTextBeepFrequency.getText().toString()),
							Integer.parseInt(mEditTextBeepDuration.getText().toString()),
							mSpinnerLightingMode.getSelectedItemPosition(),
							false,
							"",
							result).execute();
				}
				
			}
		});
		
		mButtonSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				byte[] result = new byte[1];
				if ((mEditTextBeepFrequency.getText().length() == 0) ||
						(mEditTextBeepDuration.getText().length() == 0))
				{
					Toast.makeText(getApplicationContext(), "Enter a value for beep frequency and beep duration", Toast.LENGTH_LONG).show();
				}
				else
				{
					new ConfigureBcrTask(mSpinnerReaderMode.getSelectedItemPosition(),
							mSpinnerGoodScanBeep.getSelectedItemPosition(),
							mSpinnerEnableTrigger.getSelectedItemPosition(),
							mSpinnerImagerMode.getSelectedItemPosition(),
							Integer.parseInt(mEditTextBeepFrequency.getText().toString()),
							Integer.parseInt(mEditTextBeepDuration.getText().toString()),
							mSpinnerLightingMode.getSelectedItemPosition(),
							true,
							mEditTextVersion.getText().toString(),
							result).execute();
				}
			}
		});
		
		mButtonSymbologies.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(BcrSetupActivity.this, SymbologiesActivity.class);
				startActivity(i);
			}
		});
		
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
			mBound = true;
		}
		
		
	}
	
	@Override
	protected void onStart() {
		SharedPreferences settings = getSharedPreferences("BCR_SETUP", 0);
		int bcrReaderMode = settings.getInt("READER_MODE", 0);
		int bcrScanBeep = settings.getInt("SCAN_BEEP", 1);
		boolean bcrEnableTrigger = settings.getBoolean("ENABLE_TRIGGER", true);
		int bcrImagerMode = settings.getInt("IMAGER_MODE", 0);
		int bcrIlluminationMode = settings.getInt("ILLUMINATION_MODE", 0);
		int bcrBeepFrequency = settings.getInt("BEEP_FREQUENCY", 1000);
		int bcrBeepDuration = settings.getInt("BEEP_DURATION", 100);
		int bcrLightingMode = settings.getInt("LIGHTING_MODE", 0);
		mSpinnerReaderMode.setSelection(bcrReaderMode);
		mSpinnerGoodScanBeep.setSelection(bcrScanBeep);
		mSpinnerEnableTrigger.setSelection(bcrEnableTrigger?1:0);
		mSpinnerImagerMode.setSelection(bcrImagerMode);
		mSpinnerIlluminationMode.setSelection(bcrIlluminationMode);
		mEditTextBeepFrequency.setText(Integer.toString(bcrBeepFrequency));
		mEditTextBeepDuration.setText(Integer.toString(bcrBeepDuration));
		mSpinnerLightingMode.setSelection(bcrLightingMode);
		//new GetBarcodeSettingsVersionTask().execute();
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		mReleaseService = 0;
		PclObject obj = new PclObject();
		obj.service = mPclService;
		obj.serviceConnection = mServiceConnection;
		return obj;
	}
	
	@Override
	protected void onDestroy() {
    	Log.d(TAG, "BcrSetupActivity: onDestroy" );
    	if (mReleaseService == 1)
    	{	
			releaseService();
    	}
		super.onDestroy();
	}

	@Override
	public void onBarCodeReceived(String barCodeValue, int symbology) {
		
	}

	@Override
	public void onBarCodeClosed() {
		
	}

	@Override
	public void onStateChanged(String state) {
		
	}

	@Override
	void onPclServiceConnected() {
		new GetBarcodeSettingsVersionTask().execute();
		
	}
	
	private boolean bcrSetReaderMode(int mode, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrSetReaderMode(mode, result);
		}
		else {
			Log.d(TAG, "bcrSetReaderMode: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrGoodScanBeep(int beepMode, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrSetGoodScanBeep(beepMode, result);
		}
		else {
			Log.d(TAG, "bcrSetReaderMode: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrEnableTrigger(int enable, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrEnableTrigger(enable, result);
		}
		else {
			Log.d(TAG, "bcrEnableTrigger: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrSetImagerMode(int imagerMode, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrSetImagerMode(imagerMode, result);
		}
		else {
			Log.d(TAG, "bcrSetImagerMode: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrSetBeep(int frequency, int duration, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrSetBeep(frequency, duration, result);
		}
		else {
			Log.d(TAG, "bcrSetBeep: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrSetLightingMode(int lightingMode, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrSetLightingMode(lightingMode, result);
		}
		else {
			Log.d(TAG, "bcrSetLightingMode: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrEnableSymbologies(int[] arraySymbologies, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrEnableSymbologies(arraySymbologies, arraySymbologies.length, result);
		}
		else {
			Log.d(TAG, "bcrEnableSymbologies: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrDisableSymbologies(int[] arraySymbologies, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrDisableSymbologies(arraySymbologies, arraySymbologies.length, result);
		}
		else {
			Log.d(TAG, "bcrDisableSymbologies: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrSetNonVolatileMode(int mode, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrSetNonVolatileMode(mode, result);
		}
		else {
			Log.d(TAG, "bcrSetNonVolatileMode: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrGetSettingsVersion(byte[] version) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrGetSettingsVersion(version);
		}
		else {
			Log.d(TAG, "bcrGetSettingsVersion: mPclService is null");
		}
		return ret;
	}
	
	private boolean bcrSetSettingsVersion(byte[] version, byte[] result) {
		boolean ret = false;
		if( mPclService != null ) {
			ret = mPclService.bcrSetSettingsVersion(version, result);
		}
		else {
			Log.d(TAG, "bcrSetSettingsVersion: mPclService is null");
		}
		return ret;
	}
	
	class GetBarcodeSettingsVersionTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mVersion;
		GetBarcodeSettingsVersionTask() {
			mVersion = new byte[2];
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			bRet = bcrGetSettingsVersion(mVersion);
			Log.d(TAG, String.format("bcrGetSettingsVersion bRet=%s mVersion=%02x.%02x", Boolean.toString(bRet), mVersion[0], mVersion[1]));
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			String strResult;
			if (result == true)
			{
				strResult = bytesToHex(mVersion);
				mEditTextVersion.setText(strResult);
			}
		}
		
		private String bytesToHex(byte[] bytes) {
			final char[] hexArray = "0123456789ABCDEF".toCharArray();
		    char[] hexChars = new char[bytes.length * 2];
		    for ( int j = 0; j < bytes.length; j++ ) {
		        int v = bytes[j] & 0xFF;
		        hexChars[j * 2] = hexArray[v >>> 4];
		        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		    }
		    return new String(hexChars);
		}
	}
	
	class ConfigureBcrTask extends AsyncTask<Void, Void, Boolean> {
		private byte[] mResult;
		private int mReaderMode;
		private int mBeepMode;
		private int mEnableTrigger;
		private int mImagerMode;
		private int mBeepFrequency;
		private int mBeepDuration;
		private int mLightingMode;
		private boolean mPermanent;
		private String mVersion;
		ConfigureBcrTask(int readerMode, int beepMode, int enableTrigger, int imagerMode,
				int beepFrequency, int beepDuration, int lightingMode, boolean permanent, String version, byte[] result) {
			mResult = result;
			mReaderMode = readerMode;
			mBeepMode = beepMode;
			mEnableTrigger = enableTrigger;
			mImagerMode = imagerMode;
			mBeepFrequency = beepFrequency;
			mBeepDuration = beepDuration;
			mLightingMode = lightingMode;
			mPermanent = permanent;
			mVersion = version;
		}
		protected Boolean doInBackground(Void... dummy) {
			Boolean bRet = false;
			SharedPreferences settings = getSharedPreferences("SYMBOLOGIES", 0);
			int itemCount = getResources().getStringArray(R.array.symbo_array).length;
			int[] allSymbologies = new int[1];
			allSymbologies[0] = 0;
			int[] arraySymbologies;
			
			int j = 0;
			for (int i=0; i<itemCount; i++)
			{
				if (settings.getBoolean("SYMBO_"+i, false) == true)
				{
					j++;
				}
			}
			if (j == Symbologies.supportedSymbologiesCount())
			{
				arraySymbologies = new int[1];
				arraySymbologies[0] = Symbologies.ICBarCode_AllSymbologies.toInt(); // All Symbologies
			}
			else
			{
				arraySymbologies = new int[j];
				j = 0;
				for (int i=0; i<itemCount; i++)
				{
					if (settings.getBoolean("SYMBO_"+i, false) == true)
					{
						arraySymbologies[j] = convertListItemPositionToSymbology(i);
						j++;
					}
				}
			}
			

			if (mPermanent)
			{
				bRet = bcrSetNonVolatileMode(1, mResult);
			}
			bRet = bcrSetReaderMode(mReaderMode, mResult);
			bRet = bcrGoodScanBeep(mBeepMode, mResult);
			bRet = bcrEnableTrigger(mEnableTrigger, mResult);
			bRet = bcrSetImagerMode(mImagerMode, mResult);
			bRet = bcrSetBeep(mBeepFrequency, mBeepDuration, mResult);
			bRet = bcrSetLightingMode(mLightingMode, mResult);
			bRet = bcrDisableSymbologies(allSymbologies, mResult);
			bRet = bcrEnableSymbologies(arraySymbologies, mResult);
			if (!mVersion.isEmpty())
			{
				BigInteger i = new BigInteger(mVersion, 16);
				byte[] array = i.toByteArray();
				bRet = bcrSetSettingsVersion(array, mResult);
			}
			if (mPermanent)
			{
				bRet = bcrSetNonVolatileMode(0, mResult);
			}
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			saveParameters();
		}
	}
	
	private int convertListItemPositionToSymbology(int position)
    {
    	switch (position)
    	{
    	case 0:
    	case 1:
    	case 2:
    	case 3:
    	case 4:
    	case 5:
    	case 6:
    	case 7:
    	case 8:
    	case 9:
    	case 10:
    	case 11:
    	case 12:
    		return position + 1;
    	case 13:
    	case 14:
    	case 15:
    		return position + 2;
    	case 16:
    		return position + 3;
    	case 17:
    	case 18:
    	case 19:
    		return position + 4;
    	case 20:
    	case 21:
    	case 22:
    		return position + 5;
    	case 23:
    	case 24:
    	case 25:
    		return position + 6;
    	case 26:
    	case 27:
    	case 28:
    	case 29:
    	case 30:
    	case 31:
    	case 32:
    	case 33:
    	case 34:
    	case 35:
    	case 36:
    		return position + 7;
    	case 37:
    		return 0x4A;
    	default:
    		return -1;
    	}
    }
	
	private void saveParameters() {
		int beepFreq;
		int beepDuration;
		Editor editor;
		SharedPreferences settings = getSharedPreferences("BCR_SETUP", 0);
		editor = settings.edit();
		editor.putInt("READER_MODE", mSpinnerReaderMode.getSelectedItemPosition());
		editor.putInt("SCAN_BEEP", mSpinnerGoodScanBeep.getSelectedItemPosition());
		editor.putBoolean("ENABLE_TRIGGER", (mSpinnerEnableTrigger.getSelectedItemPosition() == 0)?false:true);
		editor.putInt("IMAGER_MODE", mSpinnerImagerMode.getSelectedItemPosition());
		editor.putInt("ILLUMINATION_MODE", mSpinnerIlluminationMode.getSelectedItemPosition());
		if (mEditTextBeepFrequency.getText().length() > 0)
		{
			beepFreq = Integer.parseInt(mEditTextBeepFrequency.getText().toString());
		}
		else
		{
			beepFreq = 1000;
			
		}
		editor.putInt("BEEP_FREQUENCY", beepFreq);
		if (mEditTextBeepDuration.getText().length() > 0)
		{
			beepDuration = Integer.parseInt(mEditTextBeepDuration.getText().toString());
		}
		else
		{
			beepDuration = 100;
		}
		editor.putInt("BEEP_DURATION", beepDuration);
		editor.putInt("LIGHTING_MODE", mSpinnerLightingMode.getSelectedItemPosition());
        editor.commit();
	}
}
