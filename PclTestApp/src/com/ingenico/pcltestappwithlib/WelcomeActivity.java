package com.ingenico.pcltestappwithlib;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.ingenico.pclservice.PclService;
import com.ingenico.pclutilities.PclUtilities;
import com.ingenico.pclutilities.PclUtilities.BluetoothCompanion;
import com.ingenico.pclutilities.PclUtilities.IpTerminal;
import com.ingenico.pclutilities.PclUtilities.SerialPortCompanion;
import com.ingenico.pclutilities.PclUtilities.UsbCompanion;
import com.ingenico.pclutilities.SslObject;

public class WelcomeActivity extends CommonActivity implements OnClickListener, OnCheckedChangeListener {

	private static final String TAG = "PCLTESTAPP";
	private RadioGroup mRadioGroup;
	private PclUtilities mPclUtil;
	private boolean mServiceStarted;
	private boolean sslActivated = false;
	private int terminalCounter = 0;
	private Set<IpTerminal> terminals = new HashSet<IpTerminal>();

    TextView mtvState;
	TextView mtvSerialNumber;
	CharSequence mCurrentDevice;
	TextView mtvAddonVersion;
	CheckBox mcbEnableLog;
	CheckBox mcbActivateSsl;
	
	UsbManager mUsbManager = null;
	PendingIntent mPermissionIntent = null;
	private boolean mPermissionRequested = false;
	private boolean mPermissionGranted = false;
	private static final String ACTION_USB_PERMISSION = "com.ingenico.pcltestappwithlib.USB_PERMISSION";
	
	private boolean mRestart;
	
	static class PclObject {
		PclServiceConnection serviceConnection;
		PclService service;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TextView tvAppVersion;
		TextView tvBuildDate;
		super.onCreate(savedInstanceState);
		Log.d(TAG, "WelcomeActivity start");
		setContentView(R.layout.welcome);
		final CharSequence data = (CharSequence) getLastNonConfigurationInstance();
		
		mCurrentDevice = data;
		Log.d(TAG, "mCurrentDevice = " + mCurrentDevice);
		
		findViewById(R.id.button_unitary_test).setOnClickListener(this);
		findViewById(R.id.button_loop_test).setOnClickListener(this);
		findViewById(R.id.button_unitary_test).setEnabled(false);
		findViewById(R.id.button_loop_test).setEnabled(true);
		mRadioGroup=(RadioGroup)findViewById(R.id.optionRadioGroup);
		mtvState = (TextView)findViewById(R.id.tvState);
		mtvSerialNumber = (TextView)findViewById(R.id.tvSerialNumber);
		tvAppVersion = (TextView)findViewById(R.id.tvAppVersion);
		try {
			tvAppVersion.setText(getString(R.string.app_version) + getPackageManager().getPackageInfo(getPackageName(), 0 ).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		tvBuildDate = (TextView)findViewById(R.id.tvBuildDate);

		tvBuildDate.setText(getString(R.string.build_date) + BuildConfig.BUILD_TIME);

		mtvAddonVersion = (TextView)findViewById(R.id.tvAddonVersion);
		
		mcbEnableLog = (CheckBox)findViewById(R.id.cbEnableLog);
		SharedPreferences settings = getSharedPreferences("PCLSERVICE", MODE_PRIVATE);
    	boolean enableLog = settings.getBoolean("ENABLE_LOG", true);
		mcbEnableLog.setChecked(enableLog);
		mcbEnableLog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences settings = getSharedPreferences("PCLSERVICE", MODE_PRIVATE);
				Editor editor = settings.edit();
				boolean isChecked = mcbEnableLog.isChecked();
				editor.putBoolean("ENABLE_LOG", isChecked);
				editor.commit();
				if (mPclService != null) {
					mPclService.enableDebugLog(isChecked);
				}
			}
		});
		
		sslActivated = settings.getBoolean("ENABLE_SSL", false);
		mcbActivateSsl = (CheckBox)findViewById(R.id.cbActivateSsl);
    	mcbActivateSsl.setChecked(sslActivated);
    	mcbActivateSsl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences settings = getSharedPreferences("PCLSERVICE", MODE_PRIVATE);
				Editor editor = settings.edit();
				sslActivated = mcbActivateSsl.isChecked();
				editor.putBoolean("ENABLE_SSL", sslActivated);
				editor.commit();
				
				// Update IP terminal
				for(IpTerminal terminal: terminals){
					if (terminal.isActivated())
					{
						if(mcbActivateSsl.isChecked()){
							terminal.setSsl(1);
						}
						else{
							terminal.setSsl(0);
						}
						// Stop and restart service
						releaseService();
						stopPclService();
						mPclUtil.activateIpTerminal(terminal);
						startPclService();
						Log.d(TAG, "onSSLChanged => INIT");
						initService();
						break;
					}
				}
			}
		});
					
		mPclUtil = new PclUtilities(this, "com.ingenico.pcltestappwithlib", "pairing_addr.txt");
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
			if (mUsbManager != null) {
				mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
				IntentFilter usbFilter = new IntentFilter(ACTION_USB_PERMISSION);
				usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
				usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
				registerReceiver(mUsbReceiver, usbFilter);
			}
		}
	}
	
	@Override
	protected void onResume() {
		refreshTerminalsList("onResume");
		
		if (mRadioGroup.getCheckedRadioButtonId() == -1)
		{
			// Do not restart PclService when no companion is activated
			mRestart = false;
		}
		else
		{
			mRestart = true;
		}
		
		// Start PclService even if no companion is selected - to test TPCL-750
		mRadioGroup.setOnCheckedChangeListener(this);
		findViewById(R.id.button_unitary_test).setEnabled(true);
		findViewById(R.id.button_loop_test).setEnabled(true);
			
		startPclService();
		initService();
		
		mReleaseService = 1;
		
		if (isCompanionConnected())
        {
			new GetTermInfoTask().execute();
        	mtvState.setText(R.string.str_connected);
        	mtvState.setBackgroundColor(Color.GREEN);
        	mtvState.setTextColor(Color.BLACK);
        }
        else
        {
        	mtvState.setText(R.string.str_not_connected);
        	mtvState.setBackgroundColor(Color.RED);
        	mtvState.setTextColor(Color.BLACK);
        }
		
		if( mPclService != null ) {
			mtvAddonVersion.setText(getString(R.string.addon_version) + mPclService.getAddonVersion());
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "WelcomeActivity: onDestroy" );
		super.onDestroy();
		releaseService();
		if (mReleaseService == 1)
    	{
			stopPclService();
    	}
		if (mUsbReceiver != null) {
			unregisterReceiver(mUsbReceiver);
		}
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		mReleaseService = 0;
		CharSequence cs;
		int id = mRadioGroup.getCheckedRadioButtonId();
		if (id == -1)
			cs = "";
		else
			cs = ((RadioButton)(mRadioGroup.getChildAt(id))).getText();
		return cs;
	}
		
	@Override
	public void onClick(View v) {
		Intent i;
		
		switch (v.getId()) {
		case R.id.button_unitary_test:
			i = new Intent(WelcomeActivity.this, TestListActivity.class);
			startActivity(i);
			break;
		case R.id.button_loop_test:
			i = new Intent(WelcomeActivity.this, PclLoopTestActivity.class);
			startActivity(i);
			break;
		}
		
	}

	class GetTermInfoTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... tmp) {
			Boolean bRet = getTermInfo();
			return bRet;
		}

		protected void onPostExecute(Boolean result) {
			if (result == true)
			{
				mtvSerialNumber.setText(String.format("SN: %08x / PN: %08x", SN, PN));
			}
		}
	}

	class RestartServiceTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... tmp) {
			releaseService();
			stopPclService();
			startPclService();
			initService();
			return true;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		findViewById(R.id.button_unitary_test).setEnabled(true);
		findViewById(R.id.button_loop_test).setEnabled(true);
		Log.d(TAG, String.format("onCheckedChanged id=%d", checkedId));
		if (checkedId != -1) {
			RadioButton rb = (RadioButton) group.getChildAt(checkedId);
			if (rb != null) {
				Log.d(TAG, String.format("onCheckedChanged id=%d text=%s tmpName=%s", checkedId, rb.getText(), mCurrentDevice));
				if (!rb.getText().equals(mCurrentDevice)) {
					Log.d(TAG, String.format("current:%s saved:%s", rb.getText(), mCurrentDevice));

					mCurrentDevice = rb.getText();
					if (mCurrentDevice.toString().equals(getString(R.string.use_direct_connect))) {
						IpTerminal terminal = mPclUtil.new IpTerminal("", "", "255.255.255.255", 0);

						if (mcbActivateSsl.isChecked()) {
							terminal.setSsl(1);
						} else {
							terminal.setSsl(0);
						}

						mPclUtil.activateIpTerminal(terminal);
					} else if (rb.getTag() != null && rb.getTag().toString().equalsIgnoreCase("ip")) {
						String[] terminalNameTab = mCurrentDevice.toString().split(" - ");
						if (terminalNameTab.length > 0) {

							for (IpTerminal terminal : terminals) {

								if (terminal.getName().equalsIgnoreCase(terminalNameTab[1].trim())) {
									if (mcbActivateSsl.isChecked()) {
										terminal.setSsl(1);
									}
									mPclUtil.activateIpTerminal(terminal);
								} else {
									terminal.setActivated(false);
								}
							}

						}
					} else if (mCurrentDevice.charAt(2) == ':') {
						mPclUtil.ActivateCompanion(((String) mCurrentDevice).substring(0, 17));
					}
					else if (((String) mCurrentDevice).startsWith("/dev/")) {
						String[] terminalNameTab = mCurrentDevice.toString().split(" ");
						if (terminalNameTab.length == 2) {
							Log.d(TAG, "Activate SerialPortCompanion" + mCurrentDevice);
							mPclUtil.activateSerialPortCompanion(terminalNameTab[0], terminalNameTab[1]);
						}
					} else {
						mPclUtil.activateUsbCompanion((String) mCurrentDevice);
					}

					// Restart the service
					if (mRestart) {
						Log.d(TAG,"Restart PCL Service");
						RestartServiceTask restartTask = new RestartServiceTask();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							restartTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							restartTask.execute();
						}
					}
				} else {
					Log.d(TAG, "onCheckedChanged Allready selected");
				}
			}
			mRestart = true;
		}
		Log.d(TAG, "onCheckedChanged EXIT");
	}
	
	@Override
	public void onStateChanged(String state) {
		if (state.equals("CONNECTED"))
		{
			new GetTermInfoTask().execute();
			mtvState.setText(R.string.str_connected);
			mtvState.setBackgroundColor(Color.GREEN);
			mtvState.setTextColor(Color.BLACK);
		}
		else
		{
			mtvState.setText(R.string.str_not_connected);
			mtvState.setBackgroundColor(Color.RED);
			mtvState.setTextColor(Color.BLACK);
			mtvSerialNumber.setText("");
		}
	}

	@Override
	void onPclServiceConnected() {
		Log.d(TAG, "onPclServiceConnected");
		mPclService.addDynamicBridgeLocal(6000, 0);
		
		if (isCompanionConnected())
        {
			new GetTermInfoTask().execute();
			if (mtvState != null) {
	        	mtvState.setText(R.string.str_connected);
	        	mtvState.setBackgroundColor(Color.GREEN);
	        	mtvState.setTextColor(Color.BLACK);
			}
        }
        else
        {
        	if (mtvState != null) {
	        	mtvState.setText(R.string.str_not_connected);
	        	mtvState.setBackgroundColor(Color.RED);
	        	mtvState.setTextColor(Color.BLACK);
        	}
        }
		
		if( mPclService != null ) {
			if (mtvAddonVersion != null) {
				mtvAddonVersion.setText(getString(R.string.addon_version) + mPclService.getAddonVersion());
			}
		}
	}

	private void startPclService() {
		
		if (!mServiceStarted)
		{
			SslObject sslObject = new SslObject("serverb.p12", "coucou");
			
			SharedPreferences settings = getSharedPreferences("PCLSERVICE", MODE_PRIVATE);
	    	boolean enableLog = settings.getBoolean("ENABLE_LOG", true);
			Intent i = new Intent(this, PclService.class);
			i.putExtra("PACKAGE_NAME", "com.ingenico.pcltestappwithlib");
			i.putExtra("FILE_NAME", "pairing_addr.txt");
			i.putExtra("ENABLE_LOG", enableLog);
			i.putExtra("SSL_OBJECT", sslObject);
			if (getApplicationContext().startService(i) != null)
				mServiceStarted = true;
		}
	}
    
    private void stopPclService() {
		if (mServiceStarted)
		{
			Intent i = new Intent(this, PclService.class);
			if (getApplicationContext().stopService(i))
				mServiceStarted = false;
		}
	}

	@Override
	public void onBarCodeReceived(String barCodeValue, int symbology) {
		// Do nothing		
	}

	@Override
	public void onBarCodeClosed() {
		// Do nothing		
	}
	
	void refreshTerminalsList(String string) {
		synchronized (mRadioGroup) {
			Log.d(TAG, "refreshTerminalsList: " + string);
			mRadioGroup.removeAllViewsInLayout();
			boolean bFound = false;

			terminalCounter = 0;

			// Add button for IP direct connect
			{
				RadioButton radioButton = new RadioButton(this);
				radioButton.setText(getString(R.string.use_direct_connect));
				radioButton.setId(terminalCounter);
				if (mCurrentDevice != null && mCurrentDevice.toString().equals(getString(R.string.use_direct_connect)))
				{
					bFound = true;
					radioButton.setChecked(true);
				}
				mRadioGroup.addView(radioButton);
				terminalCounter++;
			}

			Set<BluetoothCompanion> btComps = mPclUtil.GetPairedCompanions();
			if (btComps != null && (btComps.size() > 0)) {
				// Loop through paired devices
				for (BluetoothCompanion comp : btComps) {
					Log.d(TAG, comp.getBluetoothDevice().getAddress());
					RadioButton radioButton = new RadioButton(this);
					radioButton.setText(comp.getBluetoothDevice().getAddress() + " - " + comp.getBluetoothDevice().getName());
					radioButton.setId(terminalCounter);
					if (comp.isActivated()) {
						bFound = true;
						radioButton.setChecked(true);
						mCurrentDevice = comp.getBluetoothDevice().getAddress() + " - " + comp.getBluetoothDevice().getName();
					}
					else {
						radioButton.setChecked(false);
					}
					mRadioGroup.addView(radioButton);
					terminalCounter++;
				}
			}

			Set<UsbDevice> usbDevs = mPclUtil.getUsbDevices();
			if (usbDevs != null && (usbDevs.size() > 0)) {
				for (UsbDevice dev : usbDevs) {
					Log.d(TAG, "refreshTerminalsList:" + dev.toString());
					if (!mUsbManager.hasPermission(dev) && !mPermissionRequested && !mPermissionGranted) {
						Log.d(TAG, "refreshTerminalsList: requestPermission");
						mPermissionRequested = true;
						mUsbManager.requestPermission(dev, mPermissionIntent);
					} else {
						UsbCompanion comp = mPclUtil.getUsbCompanion(dev);
						if (comp != null) {
							RadioButton radioButton = new RadioButton(this);
							radioButton.setText(comp.getName());
							radioButton.setId(terminalCounter);
							if (comp.isActivated()) {
								bFound = true;
								radioButton.setChecked(true);
								mCurrentDevice = comp.getName();
							} else {
								radioButton.setChecked(false);
							}
							mRadioGroup.addView(radioButton);
							terminalCounter++;
						} else {
							Log.d(TAG, "refreshTerminalsList: getUsbCompanion returns null");
						}
					}
				}
			}

			Set<String> serialDevices = mPclUtil.getSerialPortDevices();
			if (serialDevices != null && (serialDevices.size() > 0)) {
				for (String fileDev : serialDevices) {
					// Our serial port terminal
					if (fileDev.equals("/dev/ttyS3")) {
						String tmpStr = "";
						if (mCurrentDevice == null)
							tmpStr = mPclUtil.getActivatedCompanion();
						else
							tmpStr = mCurrentDevice.toString();
						// Test if already selected companion
						if (tmpStr.startsWith("/dev/ttyS3")) {							
							RadioButton radioButton = new RadioButton(this);
							radioButton.setText(tmpStr);
							radioButton.setId(terminalCounter);
							bFound = true;
							radioButton.setChecked(true);
							mRadioGroup.addView(radioButton);
							terminalCounter++;
						} else {
							SerialPortCompanion comp = mPclUtil.getSerialPortCompanion(fileDev);
							if (comp != null) {

								RadioButton radioButton = new RadioButton(this);
								radioButton.setText(comp.toString());
								radioButton.setId(terminalCounter);
								if (comp.isActivated()) {
									bFound = true;
									radioButton.setChecked(true);
									mCurrentDevice = comp.toString();
								} else {
									radioButton.setChecked(false);
								}
								mRadioGroup.addView(radioButton);
								terminalCounter++;
								comp.getSerialPortDevice().close();
							}
						}
						break;
					}
				}
			} else {
				Log.d(TAG, "refreshTerminalsList: getSerialPortCompanions returns null or empty list");
			}

			terminals.clear();

			if (!bFound) {
				String act = mPclUtil.getActivatedCompanion();
				if ((act != null) && !act.isEmpty() && (act.charAt(2) != ':') && (!act.contains("_"))) {
					RadioButton radioButton = new RadioButton(this);
					radioButton.setText(act);
					radioButton.setId(terminalCounter);
					radioButton.setChecked(true);
					mCurrentDevice = act;
					mRadioGroup.addView(radioButton);
					terminalCounter++;
				} else if ((act != null) && !act.isEmpty() && act.contains("_")) {
					String[] terminalIp = act.split("_");
					if (!terminalIp[1].equals("255.255.255.255")) {
						RadioButton radioButton = new RadioButton(this);
						radioButton.setText(terminalIp[2] + " - " + terminalIp[0]);
						radioButton.setId(terminalCounter);
						radioButton.setTag("ip");
						radioButton.setChecked(true);
						sslActivated = terminalIp[3].equals("1");
						mcbActivateSsl.setChecked(sslActivated);
						mCurrentDevice = act;
						mRadioGroup.addView(radioButton);
						IpTerminal term = mPclUtil.new IpTerminal(terminalIp[0], terminalIp[2], terminalIp[1], sslActivated ? 1 : 0);
						term.activate();
						terminals.add(term);
						terminalCounter++;
					} else {
						((RadioButton) mRadioGroup.getChildAt(0)).setChecked(true);
						mCurrentDevice = getString(R.string.use_direct_connect);
					}
				}
			}

			GetIpTerminalsTask task = new GetIpTerminalsTask(this);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				task.execute();
			}

			Log.d(TAG, "END refreshTerminalsList: " + string);
		}
	}

	private class GetIpTerminalsTask extends AsyncTask<Object, IpTerminal, String> {

		Context context;
		
        public GetIpTerminalsTask(Context context) {
			super();
			this.context = context;
		}

		@Override
        protected String doInBackground(Object... params) {
        	
			boolean bExist = false;
        	for(int i = 0; i < 4; i++){
        		Set<IpTerminal> terminalsRetrived = mPclUtil.getIPTerminals();
        		
        		for(IpTerminal term1: terminalsRetrived)
        		{
        			bExist = false;
        			for (IpTerminal term2: terminals)
        			{
	        			if(term1.getMac().equals(term2.getMac()))
	        			{
	        				bExist = true;
	        				break;				
	        			}
        			}
        			if (!bExist)
            		{
            			terminals.add(term1);
        				publishProgress(term1);
            		}
        		}
        		
        	}
        	
            return null;
        }

        @Override
        protected void onProgressUpdate(IpTerminal... values) {

        	String activated = "";
        	IpTerminal term = values[0];
        	
        	if(mCurrentDevice != null){
        		String[] activatedtab = mCurrentDevice.toString().split("_");
        		if(activatedtab.length > 2)
        			activated = activatedtab[2] + " - " + activatedtab[0]; 
        	}
        	Log.d(TAG, "GetIpTerminals: activated=" + activated);
        	
        	
        	Log.d(TAG, "GetIpTerminals: term=" + term.getMac() + " - " + term.getName());
        	if(!activated.equals(term.getMac() + " - " + term.getName())){
				RadioButton radioButton = new RadioButton(context);
				radioButton.setText(term.getMac() + " - " + term.getName());
				radioButton.setId(terminalCounter);
				radioButton.setTag("ip");
				if(term.isActivated()){
					radioButton.setChecked(true);
					mCurrentDevice = term.getMac() + " - " + term.getName();
					String[] terminalIp = mPclUtil.getActivatedCompanion().split("_");
					sslActivated = (terminalIp[3] == "1" ? true : false);
					mcbActivateSsl.setChecked(sslActivated);
				}
				mRadioGroup.addView(radioButton);
				terminalCounter++;
        	}
        }
    }
	
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

	    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
	        	synchronized (this) {
		        	UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		        	if (device != null) {			        	
		        		if (PclUtilities.isIngenicoUsbDevice(device)) {
				        	refreshTerminalsList("deviceAttached");
			        	}
		        	}
	        	}
	        }
	        else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
	        	synchronized (this) {
		        	UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
		        	if (device != null) {
			        	if (PclUtilities.isIngenicoUsbDevice(device)) {
				        	refreshTerminalsList("deviceDetached");
			        	}
		        	}
	        	}
	        }
	        else if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (PclUtilities.isIngenicoUsbDevice(device)) {
						if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
							Log.i(TAG, "Permission granted for device\n" + device);
							refreshTerminalsList("deviceGranted");
							mPermissionGranted = true;
						} 
						else {
							Log.w(TAG, "Permission refused for device\n" + device);
						}
						mPermissionRequested = false;
					}

				}
			}
	    }
	};
}
