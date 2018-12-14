package com.ingenico.pcltestappwithlib;

import com.ingenico.pcltestappwithlib.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class CaptureSignature extends Activity {
	private static final String TAG = "PCLSIGNCAP";
	private static final int SIGN_CAPTURE_OK = RESULT_OK;
	private static final int SIGN_CAPTURE_KO = RESULT_CANCELED;
	private static final String EXTRA_SIGNATURE_BMP = "signature";
    LinearLayout mLinearLayout;
    SignatureView mSignature;
    Button mClear, mGetSign, mCancel;
    View mView;
    
	private int mResult = SIGN_CAPTURE_KO;
	private Path mPath;
	private boolean mBtnSaveEnabled;

    
    static byte[] headbmp = {0x42,0x4D,(byte)0x66,(byte)0x24,0x00,0x00,0x00,0x00,
		 0x00,0x00,0x3E,0x00,0x00,0x00,0x28,0x00,
		 0x00,0x00,(byte)0x90,0x01,0x00,0x00,(byte)0xB2,0x00,
		 0x00,0x00,0x01,0x00,0x01,0x00,0x00,0x00,
		 0x00,0x00,(byte)0x28,(byte)0x24,0x00,0x00,(byte)0xC4,0x0E,
		 0x00,0x00,(byte)0xC4,0x0E,0x00,0x00,0x00,0x00,
		 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
		 0x00,0x00,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0X00
		}; 

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signature);
        
        Intent intent = getIntent();
        intent.getIntExtra("POS_X", (char) 0);
        intent.getIntExtra("POS_Y", (char) 0);
        int width  = intent.getIntExtra("WIDTH", (char) 0);
        int height  = intent.getIntExtra("HEIGHT", (char) 0);
        intent.getIntExtra("TIMEOUT", (int) 0);
        SystemClock.uptimeMillis();
		mPath = new Path();
		mBtnSaveEnabled = false;
        /*
        mReleaseService = true;
        final PclObject data = (PclObject) getLastNonConfigurationInstance();
		if (data == null || data.service == null || data.serviceConnection == null) {
			Log.d(TAG, "onCreate: Init service" );
			initService();
			mStartTime = SystemClock.uptimeMillis();
			mPath = new Path();
			mBtnSaveEnabled = false;
		}
		else
		{
			Log.d(TAG, "onCreate: Service already initialized" );
			mServiceConnection = data.serviceConnection;
			mPclService = data.service;
			mStartTime = data.startTime;
			mPath = data.path;
			mBtnSaveEnabled = data.btnSaveEnabled;
		}
        */
		/*
        timeoutCheck = new Timer();
        timeoutCheck.schedule(new TimerTask() {
        	@Override
        	public void run() {
        		runOnUiThread(new Runnable() {
        			public void run() {
        				checkTimeout();
        			}
        		});
        	}
        }, 1000, 100); // updates each 100 msecs
        */

        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new SignatureView(this, null, mPath);
        mSignature.setBackgroundColor(Color.WHITE);
        //mContent.addView(mSignature, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        mLinearLayout.addView(mSignature,width,height);
        mClear = (Button)findViewById(R.id.clear);
        mGetSign = (Button)findViewById(R.id.getsign);
        mGetSign.setEnabled(mBtnSaveEnabled);
        mCancel = (Button)findViewById(R.id.cancel);
        mView = mLinearLayout;

        mClear.setOnClickListener(new OnClickListener() 
        {        
            public void onClick(View v) 
            {
            	Log.v(TAG, "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
                mBtnSaveEnabled = false;
            }
        });

        mGetSign.setOnClickListener(new OnClickListener() 
        {        
            public void onClick(View v) 
            {
                Log.v(TAG, "Panel Saved");
                mView.setDrawingCacheEnabled(true);
                Bitmap bmp = mSignature.save(mView);
                Intent i = new Intent();
                i.putExtra(EXTRA_SIGNATURE_BMP, bmp);
                mResult = SIGN_CAPTURE_OK;
                setResult(mResult, i);
                finish();
            }
        });

        mCancel.setOnClickListener(new OnClickListener() 
        {        
            public void onClick(View v) 
            {
                Log.v(TAG, "Panel Canceled");
                mResult = SIGN_CAPTURE_KO;
                setResult(mResult);
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		boolean finish = intent.getBooleanExtra("FINISH", false);
		if (finish)
			finish();
	}
	
    public class SignatureView extends View 
    {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint mPaint = new Paint();
        private Path mPath;

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public SignatureView(Context context, AttributeSet attrs) 
        {
            super(context, attrs);
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeWidth(STROKE_WIDTH);
            mPath = new Path();
        }
        
        public SignatureView(Context context, AttributeSet attrs, Path path) 
        {
            super(context, attrs);
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeWidth(STROKE_WIDTH);
            mPath = path;
        }

        public Bitmap save(View v) 
        {
        	Log.v(TAG, "Width: " + v.getWidth());
            Log.v(TAG, "Height: " + v.getHeight());
            Bitmap bmp = Bitmap.createBitmap (mLinearLayout.getWidth(), mLinearLayout.getHeight(), Bitmap.Config.RGB_565);
            
            Canvas canvas = new Canvas(bmp);
            v.draw(canvas); 
            return bmp;
        
            
            /*
    		try
    		{
    	        String bmpstring = "PCL_signature.bmp";
    	        FileOutputStream fos = openFileOutput(bmpstring, Context.MODE_WORLD_READABLE);
    	        Log.d(TAG, String.format("File path: %s", getFileStreamPath(bmpstring)));
    			DataOutputStream dos = new DataOutputStream(fos);
    			dos.write(bmpdatabit,0, bmpdatabitlen+HEADSIZE);
    			dos.flush();
    			dos.close();
    			mResult = SIGN_CAPTURE_OK;
    		}
    		catch(Exception e)
    		{
    			System.out.println("IOException : " + e);
    			mResult = SIGN_CAPTURE_KO;
    		}
    		*/
        }

        public void clear() 
        {
            mPath.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) 
        {
            canvas.drawPath(mPath, mPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) 
        {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);
            mBtnSaveEnabled = true;

            switch (event.getAction()) 
            {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:

                resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) 
                {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    mPath.lineTo(historicalX, historicalY);
                }
                mPath.lineTo(eventX, eventY);
                break;

            default:
                debug("Ignored touch event: " + event.toString());
                return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string){
        }

        private void expandDirtyRect(float historicalX, float historicalY) 
        {
            if (historicalX < dirtyRect.left) 
            {
                dirtyRect.left = historicalX;
            } 
            else if (historicalX > dirtyRect.right) 
            {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) 
            {
                dirtyRect.top = historicalY;
            } 
            else if (historicalY > dirtyRect.bottom) 
            {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) 
        {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
/*  
    static class PclObject {
		PclServiceConnection serviceConnection;
		IPclService service;
		long startTime;
		Path path;
		boolean btnSaveEnabled;
	}
    
    class PclServiceConnection implements ServiceConnection
	{
		public void onServiceConnected(ComponentName className, 
				IBinder boundService )
		{
			mPclService = IPclService.Stub.asInterface((IBinder)boundService);
			Log.d(TAG, "onServiceConnected" );
		}

		public void onServiceDisconnected(ComponentName className)
		{
			mPclService = null;
			Log.d(TAG, "onServiceDisconnected" );
		}
	};
	
	private void initService() {
		Log.d(TAG, "initService" );
		mServiceConnection = new PclServiceConnection();
		Intent i = new Intent();
		i.setClassName( "com.ingenico.pclservice", "com.ingenico.pclservice.PclService" );
		getApplicationContext().bindService( i, mServiceConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	private void releaseService() {
		if (mServiceConnection != null) {
			Log.d(TAG, "releaseService" );
			getApplicationContext().unbindService( mServiceConnection );	  
			mServiceConnection = null;
			mIsBound = false;
		}
	}
*/

}
