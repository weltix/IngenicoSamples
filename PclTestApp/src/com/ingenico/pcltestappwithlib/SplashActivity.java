package com.ingenico.pcltestappwithlib;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

public class SplashActivity extends CommonActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    /**
     * The thread to process splash screen events
     */
    private Thread mSplashThread;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash);
        initService();

        // The thread to wait for splash screen events
        mSplashThread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        // Wait given period of time or exit on touch
                        this.wait(SPLASH_DISPLAY_LENGTH);
                    	Log.d(TAG, "SplashActivity start");
                    }
                }
                catch(InterruptedException ex){
                }

                finish();

                // Run next activity
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
                //stop();
            }
        };

        mSplashThread.start();
    }

	@Override
	protected void onDestroy() {
		releaseService();
		super.onDestroy();
	}
	
	/**
     * Processes splash screen touch events
     */
    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(mSplashThread){
            	mSplashThread.notifyAll();
            }
        }
        return true;
    }

	@Override
	public void onBarCodeReceived(String barCodeValue, int symbology) {
		
	}

	@Override
	public void onBarCodeClosed() {
		
	}

	@Override
	public void onStateChanged(String state) {
		synchronized(mSplashThread){
            mSplashThread.notifyAll();
        }
	}

	@Override
	void onPclServiceConnected() {
		Log.d(TAG, "onPclServiceConnected");
		mPclService.addDynamicBridgeLocal(6000, 0);
	}
}