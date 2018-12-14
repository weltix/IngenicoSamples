package com.ingenico.spicesshop;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.ingenico.pclservice.PclService;
import com.ingenico.pclutilities.PclUtilities;
import com.ingenico.pclutilities.SslObject;
import com.ingenico.pclutilities.PclUtilities.BluetoothCompanion;
import com.ingenico.pclutilities.PclUtilities.IpTerminal;
import com.ingenico.pclutilities.PclUtilities.SerialPortCompanion;
import com.ingenico.pclutilities.PclUtilities.UsbCompanion;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
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
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class ConnectionActivity extends CommonActivity implements OnClickListener, OnCheckedChangeListener {

	private static final String TAG = "SPICESSHOP";
	private RadioGroup mRadioGroup;

	private int terminalCounter = 0;
	private boolean sslActivated = false;
	private PclUtilities mPclUtil;
	private Set<IpTerminal> terminals = new HashSet<IpTerminal>();
	
	CharSequence mCurrentDevice;
	TextView mtvSerialNumber;
	CheckBox mcbEnableLog;
	CheckBox mcbActivateSsl;
	TextView mtvAddonVersion;
	TextView mtvState;
	
	private boolean mPermissionRequested = false;
	private boolean mPermissionGranted = false;
	UsbManager mUsbManager = null;
	PendingIntent mPermissionIntent = null;
	
	private static final String ACTION_USB_PERMISSION = "com.ingenico.spicesshop.USB_PERMISSION";
	
	static class PclObject {
		PclServiceConnection serviceConnection;
		PclService service;
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Log.d(TAG, String.format("onCheckedChanged id=%d", checkedId));
		if (checkedId != -1)
		{
			RadioButton rb = (RadioButton)group.getChildAt(checkedId);
			if (rb != null) {
				Log.d(TAG, String.format("onCheckedChanged id=%d text=%s", checkedId, rb.getText()));
				if (!rb.getText().equals(mCurrentDevice))
				{
					releaseService();
					
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
					} else if(rb.getTag() != null && rb.getTag().toString().equalsIgnoreCase("ip")){
						String[] terminalNameTab = mCurrentDevice.toString().split(" - ");
						if(terminalNameTab.length > 0){
							
							for(IpTerminal terminal: terminals){

								if(terminal.getName().equalsIgnoreCase(terminalNameTab[1].trim())){
									if(mcbActivateSsl.isChecked()){
										terminal.setSsl(1);
									}
									mPclUtil.activateIpTerminal(terminal);
								}
							}
							
						}
					} 
					else if (mCurrentDevice.charAt(2) == ':') {
						mPclUtil.ActivateCompanion(((String) mCurrentDevice).substring(0, 17));
					} else if (((String) mCurrentDevice).startsWith("/dev/")) {
						String[] terminalNameTab = mCurrentDevice.toString().split(" ");
						if (terminalNameTab.length == 2) {
							mPclUtil.activateSerialPortCompanion(terminalNameTab[0], terminalNameTab[1]);
						}
					} else {
						mPclUtil.activateUsbCompanion((String) mCurrentDevice);
					}

					Log.d(TAG, "onCheckedChanged => INIT");
					initService();
				}
			}
		}
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TextView tvAppVersion;
		TextView tvBuildDate;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		final CharSequence data = (CharSequence) getLastNonConfigurationInstance();
		mCurrentDevice = data;
		
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
    	boolean enableLog = settings.getBoolean("ENABLE_LOG", false);
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
			}
		});
		
		mPclUtil = new PclUtilities(this, "com.ingenico.spicesshop", "pairing_addr.txt");
		
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
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshTerminalsList("onResume");
		if (mRadioGroup.getCheckedRadioButtonId() == -1)
		{
			mRadioGroup.setOnCheckedChangeListener(this);
		}
		else
		{
			mRadioGroup.setOnCheckedChangeListener(this);

			initService();
		}
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
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		releaseService();
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

	void refreshTerminalsList(String string) {
		
		synchronized(mRadioGroup) {
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
				if (!mUsbManager.hasPermission(dev) && !mPermissionRequested && !mPermissionGranted) {
					Log.d(TAG, "refreshTerminalsList: requestPermission");
					mPermissionRequested = true;
					mUsbManager.requestPermission(dev, mPermissionIntent);
				}
				else
				{
					UsbCompanion comp = mPclUtil.getUsbCompanion(dev);
					if (comp != null) {
						RadioButton radioButton = new RadioButton(this);
						radioButton.setText(comp.getName());
						radioButton.setId(terminalCounter);
						if (comp.isActivated()) {
							bFound = true;
							radioButton.setChecked(true);
							mCurrentDevice = comp.getName();
						}
						else {
							radioButton.setChecked(false);
						}
						mRadioGroup.addView(radioButton);
						terminalCounter++;
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
		}
		
		new GetIpTerminals(this).execute();
		
		if (!bFound) {
			String act = mPclUtil.getActivatedCompanion();
			if ((act != null) && !act.isEmpty() && (act.charAt(2) != ':') && (!act.contains("_"))) {
				RadioButton radioButton = new RadioButton(this);
				radioButton.setText(act);
				radioButton.setId(terminalCounter);
				radioButton.setChecked(true);
				mCurrentDevice = act;
				mRadioGroup.addView(radioButton);
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
			Log.d(TAG, "END refreshTerminalsList: " + string);
		}
	}
		
	private class GetIpTerminals extends AsyncTask<Void, Void, String> {

		Context context;
		
        public GetIpTerminals(Context context) {
			super();
			this.context = context;
		}

		@Override
        protected String doInBackground(Void... params) {
        	
			terminals.clear();
			
        	for(int i = 0; i < 4; i++){
        		Set<IpTerminal> terminalsRetrived = mPclUtil.getIPTerminals();
        		
        		for(IpTerminal term: terminalsRetrived){
        			if(!terminals.contains(term))
        				terminals.add(term);
        		}
        	}
        	if(terminals.size() > 0)
        		publishProgress();
        	
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        	String activated = "";
        	
        	if(mCurrentDevice != null){
        		String[] activatedtab = mCurrentDevice.toString().split("_");
        		if(activatedtab.length > 2)
        			activated = activatedtab[2] + " - " + activatedtab[0]; 
        	}
        	
        	for(IpTerminal term: terminals){
        		
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
    }

	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		// TODO Auto-generated method stub
		
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
	        	mtvSerialNumber.setText("");
        	}
        }
		
		if( mPclService != null ) {
			if (mtvAddonVersion != null) {
				mtvAddonVersion.setText(getString(R.string.addon_version) + mPclService.getAddonVersion());
			}
		}
		
	}

	@Override
	public void onBarCodeClosed() {
		// TODO Auto-generated method stub
		
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
	public void onBarCodeReceived(String barCodeValue, int symbology) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "WelcomeActivity: onDestroy" );
		super.onDestroy();
		releaseService();
		/*if (mReleaseService == 1)
    	{
			stopPclService();
    	}*/
		if (mUsbReceiver != null) {
			unregisterReceiver(mUsbReceiver);
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
