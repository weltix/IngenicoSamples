package com.ingenico.spicesshop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ingenico.pclservice.TransactionIn;
import com.ingenico.pclservice.TransactionOut;
import com.ingenico.spicesshop.R;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class CartActivity extends CommonActivity implements OnClickListener{

    private ImageView butActiveCodebar, buyAll, deleteEntryButton;
    private LinearLayout ll;
    private ScrollView sv;
    private CheckBox[] cbs;
    private Context activityContext;
    
    
    
	   /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContext = this;
		setContentView(R.layout.car);
				
		buyAll = (ImageView) findViewById(R.id.buttonBuyAll);
    	RelativeLayout rlmain = (RelativeLayout) findViewById(R.id.CarLayoutId);
		sv = new ScrollView(this);
    	ll = new LinearLayout(this);
    	cbs = new CheckBox[8];
    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
    			RelativeLayout.LayoutParams.WRAP_CONTENT);
    	lp.addRule(RelativeLayout.BELOW, R.id.textView1);
    	lp.addRule(RelativeLayout.ALIGN_LEFT, R.id.textView1);
    	ll.setOrientation(LinearLayout.VERTICAL);
    	ll.setLayoutParams(lp);
    	sv.setLayoutParams(lp);
    	rlmain.addView(sv, lp);
    	sv.addView(ll);
        
        ImageView doTransactionButton = (ImageView) findViewById(R.id.buttonBuyAll);
        doTransactionButton.setOnClickListener(this);
        deleteEntryButton = (ImageView) findViewById(R.id.buttonBin);
        deleteEntryButton.setOnClickListener(this);
        
        butActiveCodebar = (ImageView) findViewById(R.id.butActiveCodebar);
        butActiveCodebar.setOnClickListener(this);
        
        initService();
    }
    
    
    private void refreshCart(){
        TextView textTotalPrice = (TextView) findViewById(R.id.textViewTotalCart);
        //textTotalPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)24);
        textTotalPrice.setText(String.format("Total:\r\n%.2f \u20ac", appContext.getTotalPrice()));
        ll.removeAllViews();
        for(int liste = 0; liste < SpicesShop.monPanier.size(); liste++) {
        	cbs[liste]  = new CheckBox(this);
            cbs[liste].setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float)14);
            cbs[liste].setText(String.format("%d x %s at %.2f\u20ac",SpicesShop.monPanier.get(liste).quantity,SpicesShop.monPanier.get(liste).name,SpicesShop.monPanier.get(liste).price));
            ll.addView(cbs[liste]);
        }
        if (SpicesShop.monPanier.size() > 0)
        	deleteEntryButton.setVisibility(View.VISIBLE);
        else
        	deleteEntryButton.setVisibility(View.INVISIBLE);
    }
	
    
	class PrintTextTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... text) {
        	
        	Log.d(TAG, String.format("PrintTextTask !"));
        	Boolean bRet = printText(text[0]);
            
            return bRet;
        }

        protected void onPostExecute(Boolean result) {
        	refreshCart();
        }
    }
		
		
    class DoTransactionTask extends AsyncTask<Void, Void, Boolean> {
    	private TransactionIn transIn;
		private TransactionOut transOut;
    	public DoTransactionTask(TransactionIn transIn, TransactionOut transOut) {
    		Log.d(TAG, String.format("DoTransactionTask CONSTRUCT"));
    		this.transIn = transIn;
    		this.transOut = transOut;
    	}
    	
        protected Boolean doInBackground(Void... tmp) {
        	Boolean bRet = doTransaction(transIn, transOut);
            return bRet;
        }

        protected void onPostExecute(Boolean result) {
        	Log.d(TAG, String.format("DoTransactionTask onPostExecute"));
	        if (transOut.getC3Error().equals("0000"))
	        {	
	        	// prepare the alert box
	            AlertDialog.Builder alertbox = new AlertDialog.Builder(activityContext);
	 
	            // set the message to display
	            alertbox.setMessage(getString(R.string.str_print));
	 
	            // set a positive/yes button and create a listener
	            alertbox.setPositiveButton(getString(R.string.str_yes), new DialogInterface.OnClickListener() {
	 
	                // do something when the button is clicked
	                public void onClick(DialogInterface arg0, int arg1) {
	                	// Read the contents of our asset
	                	InputStream is;
	                    AssetManager assetManager = getAssets();
	                    try {
							is = assetManager.open("logo_spice.bmp");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							return;
						}
	                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	                    byte buf[] = new byte[1024];
	                    int len;
	                    try {
	                    	while ((len = is.read(buf)) != -1) {
	                    		outputStream.write(buf,0,len);
	                    	}
	                    	outputStream.close();
	                    	is.close();
	                    } catch (IOException e) {}
	                    byte[] bMapArray =outputStream.toByteArray();
	                	
	                    new PrintBitmapTask().execute(bMapArray);

	                }
	            });
	 
	            // set a negative/no button and create a listener
	            alertbox.setNegativeButton(getString(R.string.str_no), new DialogInterface.OnClickListener() {
	 
	                // do something when the button is clicked
	                public void onClick(DialogInterface arg0, int arg1) {
	                	Toast.makeText(getBaseContext(),getString(R.string.str_thanks), Toast.LENGTH_LONG).show();
	                	SpicesShop.monPanier.clear();
	                	refreshCart();
	                }
	            });
	 
	            // display box
	            alertbox.show();
	        }
	        else
	        {
	        	Toast.makeText(getBaseContext(),getString(R.string.str_failed), Toast.LENGTH_LONG).show();	
	        }
	        buyAll.setClickable(true);
        }
    }
    
    class PrintBitmapTask extends AsyncTask<byte[], Void, Boolean> {
        protected Boolean doInBackground(byte[]... bmpBuf) {
        	Log.d(TAG, String.format("PrintBitmapTask !!!!!!!!!!!!!!!!!!!!!!!!!"));
            Boolean bRet = printBitmap(bmpBuf[0], bmpBuf[0].length);
            return bRet;
        }

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
        	String str_cartList = "\u001B" + "@" + "\n\n";
            Log.d(TAG, String.format("CART_LIST_BEFORE = %s",str_cartList));
            float PricebySpice;
            for(int liste = 0; liste < SpicesShop.monPanier.size(); liste++) {
            	PricebySpice = (SpicesShop.monPanier.get(liste).quantity) * (SpicesShop.monPanier.get(liste).price);
            	str_cartList = str_cartList + "\u000F"+ SpicesShop.monPanier.get(liste).name + " \n" +
            					"      " + String.format("%4s", String.format("%d",SpicesShop.monPanier.get(liste).quantity)) + " x " +
            					String.format("%7s", String.format("%.2f",SpicesShop.monPanier.get(liste).price)) + "\u0012" + 
            					String.format("%8s", String.format("%.2f",PricebySpice)) +"\n\n";
            	
            }
            Log.d(TAG, String.format("CART_LIST_AFTER = %s",str_cartList));
            str_cartList = str_cartList + "________________________\n\n" + "\u001b" + "E" +
            				"TOTAL (EUR)" + String.format("%10s", String.format("%.2f",appContext.getTotalPrice())) + "\u001b" + "F" + "\n\n\n" +
            		        "Thanks for your visit !" +"\u001B" + "@" +"\n\n\n\n\n";
            new PrintTextTask().execute(str_cartList);
        	Toast.makeText(getBaseContext(),getString(R.string.str_thanks), Toast.LENGTH_LONG).show();
        	SpicesShop.monPanier.clear();
        	refreshCart();
		}

    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
				
		switch(v.getId())
		{
		case R.id.buttonBuyAll:
			
	    	TransactionIn transIn = new TransactionIn();
	    	TransactionOut transOut = new TransactionOut();
	    	appContext = ((SpicesShop)getApplicationContext());
			
	    	if (appContext.getTotalPrice() != 0  )
	    	{
	    		buyAll.setClickable(false);
	    		String amount = Float.toString(100*appContext.getTotalPrice());
	    		
	    		amount = amount.substring(0, amount.indexOf('.'));
		    	transIn.setAmount(amount);
		    	transIn.setCurrencyCode("978");
		    	transIn.setOperation("C");
		    	transIn.setTermNum("58");
		        new DoTransactionTask(transIn, transOut).execute();
	    	}
	    	else
	    	{
	    		Toast.makeText(getBaseContext(),getString(R.string.str_empty), Toast.LENGTH_LONG).show();
	    	}
	    	break;
	        
		case R.id.buttonBin:
			//appContext = ((SpicesShop)getApplicationContext());
			for(int liste = SpicesShop.monPanier.size()-1; liste >= 0 ; liste--) {
	        	if (cbs[liste].isChecked())
	        	{
	        		SpicesShop.monPanier.remove(liste);
	        	}
	        }
			refreshCart();
			break;
			
		case R.id.butActiveCodebar:
			reopenBarCode();
			break;
		}
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//initService();
		refreshCart();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseService();
	}

	public void onBarCodeReceived(String barCodeValue, int symbology)
	{
		SpicesTypes type = getSpiceCodeList().get(barCodeValue);
		
		if(type != null)
		{
			addToCart(type, 1);
			refreshCart();
			Toast.makeText(getBaseContext(), String.format("1 %s %s", SpicesNames[type.Value()-1], getString(R.string.str_added)), Toast.LENGTH_SHORT).show();
		}	
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
