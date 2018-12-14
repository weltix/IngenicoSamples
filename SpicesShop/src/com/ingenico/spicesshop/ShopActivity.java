package com.ingenico.spicesshop;

import com.ingenico.spicesshop.R;
import com.ingenico.spicesshop.struct_spice;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class ShopActivity extends CommonActivity implements OnClickListener,OnGestureListener{
	
	private ViewFlipper flipper;
	private Button btnNext, btnPrevious;
	private ImageView btnAddToCartSpice, btnCartView, btnActiveCodebar;
	private EditText editTextQuantity;
	private TextView textSpicePrice;
	private GestureDetector gestureDetector = null;	
	private TextView textTotalPrice;
	
	public struct_spice badiane;
	public struct_spice paprika;
	public struct_spice pavotbleu;
	public struct_spice cannelle;
	public struct_spice curcuma;
	public struct_spice reglisse;
	public struct_spice nigelle;
	public struct_spice sumac;
	
	public int idquantity;
	//public SpicesShop appContext;
	
    public SpicesTypes getCurrentArticle(){return currentSpice;}
	SpicesTypes currentSpice = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flipper);

    	currentSpice = getCurrentSpice();
        
        flipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        flipper.setDisplayedChild((int)(currentSpice.Value() - 1));
        
        editTextQuantity = (EditText) findViewById(R.id.spice_quantity);
        editTextQuantity.setInputType(InputType.TYPE_NULL);
        
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.requestFocus();
        btnNext.setOnClickListener(this);
        
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(this);
        
        textSpicePrice = (TextView) findViewById(R.id.SpicePrice);
        textTotalPrice = (TextView) findViewById(R.id.textViewTotal);
        
        btnCartView = (ImageView) findViewById(R.id.btnViewCart);
        btnCartView.setOnClickListener(this);
        
        btnAddToCartSpice = (ImageView) findViewById(R.id.btnAddToCartSpice);
        btnAddToCartSpice.setOnClickListener(this);
        
        btnActiveCodebar = (ImageView) findViewById(R.id.btnActiveCodebar);
        btnActiveCodebar.setOnClickListener(this);

        editTextQuantity.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				editTextQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
				editTextQuantity.onTouchEvent(event); // call native handler
			return true; // consume touch even
            }
        }
        );
            
        gestureDetector = new GestureDetector(this, this);

		showSpice(currentSpice);
        
    }
    
    @Override
	public void onBarCodeReceived(String barCodeValue, int symbology)
	{
		SpicesTypes type = getSpiceCodeList().get(Integer.valueOf(barCodeValue));
		
		if(type != null)
		{
			if(type.Value() != currentSpice.Value())
				showSpice(type);
			else
			{
				addToCart(type, 1);
				updateSpiceInfos(type);
				Toast.makeText(getBaseContext(), String.format("1 %s %s", SpicesNames[currentSpice.Value()-1], getString(R.string.str_added)), Toast.LENGTH_SHORT).show();
			}
		}
	}
    
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

    public SpicesTypes getCurrentSpice()
    {
    	byte spiceID = (byte)getIntent().getByteExtra("com.ingenico.spicesshop.ARTICLE", (byte) 99);
    	return getSpiceCodeList().get(Integer.valueOf(spiceID + 1));
    }

    public void updateSpiceInfos(SpicesTypes spiceID)
    {
    	if(spiceID.Value() < SpicesTypes.MAX_SpicesTypes.Value())
    	{
    		textSpicePrice.setText(String.format("%.2f \u20ac", price[(spiceID.Value() - 1)]));
    		textTotalPrice.setText(String.format("Total:\r\n%.2f \u20ac", appContext.getTotalPrice()));
    	}
    }
    public void showSpice(SpicesTypes spiceID)
    {
    	if(spiceID.Value() < SpicesTypes.MAX_SpicesTypes.Value())
    	{
    		
    		while(spiceID.Value() > currentSpice.Value())
    			nextView();
    		while(spiceID.Value() < currentSpice.Value())
    			previousView();
    		
    		updateSpiceInfos(spiceID);
    	}
    }

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btnNext:
			//Button Next Style
			Animation animationNext = AnimationUtils.loadAnimation(this, R.anim.btn_style_next);
			btnNext.startAnimation(animationNext);
			
			nextView();
			break;
			
		case R.id.btnPrevious:
			//Button Previous Style
			Animation animationPrevious = AnimationUtils.loadAnimation(this, R.anim.btn_style_previous);
			btnPrevious.startAnimation(animationPrevious);
			previousView();
			break;
			
		case R.id.buttonSettings:
			//Button ViewCart
    	    Intent intent2 = new Intent(ShopActivity.this,ConnectionActivity.class);
    	    startActivity(intent2);
			break;

		case R.id.btnViewCart:
			//Button ViewCart
    	    Intent intent = new Intent(ShopActivity.this,CartActivity.class);
    	    startActivity(intent);
			break;
		case R.id.btnAddToCartSpice:
            String strQuantity = editTextQuantity.getText().toString();
			if (strQuantity.length() > 0)				
			{
                int quantity = Integer.parseInt(strQuantity);
                if (quantity != 0)
                {
                    addToCart(currentSpice, quantity);
				    updateSpiceInfos(currentSpice);
				    Toast.makeText(getBaseContext(), String.format("%d %s %s", quantity, SpicesNames[currentSpice.Value()-1], getString(R.string.str_added)), Toast.LENGTH_SHORT).show();
                }
						
				editTextQuantity.setText("");
			}
			break;
			
		case R.id.btnActiveCodebar:
			reopenBarCode();
			break;
		default:
			break;
		}
		
	}
	
	 
	@Override
     public boolean onTouchEvent(MotionEvent event) {
             return gestureDetector.onTouchEvent(event);
     }
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	int SWIPE_MIN_VELOCITY = 100;
	int SWIPE_MIN_DISTANCE = 100;
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
		//Get Position
		float ev1X = e1.getX();
		float ev2X = e2.getX();
		
		//Get distance of X (e1) to X (e2)
		final float xdistance = Math.abs(ev1X - ev2X);
		//Get velocity of cursor
        final float xvelocity = Math.abs(velocityX);
        
        if( (xvelocity > SWIPE_MIN_VELOCITY) && (xdistance > SWIPE_MIN_DISTANCE) )
        {
			if(ev1X > ev2X)//Switch Left
			{
				nextView();
			}
			else//Switch Right
			{
				previousView();
			}
        }
        
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	//Next, Previous Views
	private void previousView() {
		
			//Previous View
		if (currentSpice == SpicesTypes.SpicesTypes_Badian)
			currentSpice = SpicesTypes.SpicesTypes_Sumac;
		else
			currentSpice = getSpiceCodeList().get(currentSpice.Value() - 1);
			
		updateSpiceInfos(currentSpice);
		flipper.setInAnimation(this, R.anim.slide_right_in);
		flipper.setOutAnimation(this, R.anim.slide_right_out);
		flipper.showPrevious();
		
	}
	private void nextView() {
		
			//Next View
		if (currentSpice == SpicesTypes.SpicesTypes_Sumac)
			currentSpice = SpicesTypes.SpicesTypes_Badian;
		else
			currentSpice = getSpiceCodeList().get(currentSpice.Value() + 1);
			
	    updateSpiceInfos(currentSpice);	
		flipper.setInAnimation(this, R.anim.slide_left_in);
		flipper.setOutAnimation(this, R.anim.slide_left_out);
		flipper.showNext();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
       //SpicesShop appContext = ((SpicesShop)getApplicationContext());
        //textTotalPrice = (TextView) findViewById(R.id.textViewTotal);
        textTotalPrice.setText(String.format("Total:\r\n%.2f \u20ac", appContext.getTotalPrice()));
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
	
}