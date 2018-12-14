package com.ingenico.spicesshop;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MainActivity extends CommonActivity implements OnTouchListener{
    ImageView img;
    protected void onCreate(Bundle savedInstanceState) 
    {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);

               img = (ImageView) findViewById(R.id.imageView1);
               img.setOnTouchListener(this);
               initService();
    }
    
       public boolean onTouch(View v, MotionEvent event) 
   {
       switch (event.getAction())
       {
           case MotionEvent.ACTION_DOWN:
           {       
                 // Here u can write code which is executed after the user touch on the screen 
                    break; 
           }
           case MotionEvent.ACTION_UP:
           {          
        	    Intent intent = new Intent(MainActivity.this,AccueilActivity.class);
        	    startActivity(intent);
                break;
           }
           case MotionEvent.ACTION_MOVE:
           {  
              // Here u can write code which is executed when user move the finger on the screen   
               break;
           }
       }
       return true;
   }

	@Override
	public void onBarCodeReceived(String barCodeValue, int symbology) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onPclServiceConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBarCodeClosed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStateChanged(String state) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		releaseService();
	}
	
}