package com.ingenico.spicesshop;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.ingenico.pclservice.PclService;
import com.ingenico.pclservice.TransactionIn;
import com.ingenico.pclservice.TransactionOut;
import com.ingenico.pclservice.PclService.LocalBinder;
import com.ingenico.pclutilities.SslObject;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public abstract class CommonActivity extends Activity implements ServiceConnection, CommonActivityInterface {
    public static final String TAG = "SPICE";
    //private IPclService service = null;
    private static Boolean m_BarCodeActivated = false;
    private static Boolean m_PrinterActivated = false;
    private BarCodeReceiver m_BarCodeReceiver = null;
    protected SpicesShop appContext;
    public static final float[] price = {1.0f, 2.0f, 1.5f, 1.2f, 3.0f, 1.0f, 2.3f, 3.4f};
    public static String[] SpicesNames;
    private StateReceiver m_StateReceiver = null;

    protected PclServiceConnection mServiceConnection;
    protected int mReleaseService;
    protected boolean mBound = false;
    protected PclService mPclService = null;
    private boolean mServiceStarted;

    int SN, PN;

    class PclServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder boundService) {    //mPclService = IPclService.Stub.asInterface((IBinder)boundService);
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) boundService;
            mPclService = (PclService) binder.getService();
            Log.d(TAG, "onServiceConnected");
            onPclServiceConnected();
        }

        public void onServiceDisconnected(ComponentName className) {
            mPclService = null;
            Log.d(TAG, "onServiceDisconnected");
        }
    }

    ;

    abstract void onPclServiceConnected();

    public enum SpicesTypes {
        SpicesTypes_Badian(1),
        SpicesTypes_Paprika(2),
        SpicesTypes_PavoBleu(3),
        SpicesTypes_Cannelle(4),
        SpicesTypes_Curcuma(5),
        SpicesTypes_Reglisse(6),
        SpicesTypes_Nigelle(7),
        SpicesTypes_Sumac(8),
        MAX_SpicesTypes(9);

        private int m_Value;

        public int Value() {
            return m_Value;
        }

        SpicesTypes(int val) {
            this.m_Value = val;
        }
    }

    private Hashtable<Integer, SpicesTypes> spicesCodeList = null;

    public Hashtable<Integer, SpicesTypes> getSpiceCodeList() {
        return spicesCodeList;
    }

    public CommonActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = ((SpicesShop) getApplicationContext());
        spicesCodeList = new Hashtable<Integer, SpicesTypes>();

        spicesCodeList.put(1, SpicesTypes.SpicesTypes_Badian);
        spicesCodeList.put(2, SpicesTypes.SpicesTypes_Paprika);
        spicesCodeList.put(3, SpicesTypes.SpicesTypes_PavoBleu);
        spicesCodeList.put(4, SpicesTypes.SpicesTypes_Cannelle);
        spicesCodeList.put(5, SpicesTypes.SpicesTypes_Curcuma);
        spicesCodeList.put(6, SpicesTypes.SpicesTypes_Reglisse);
        spicesCodeList.put(7, SpicesTypes.SpicesTypes_Nigelle);
        spicesCodeList.put(8, SpicesTypes.SpicesTypes_Sumac);

        SpicesNames = new String[8];
        SpicesNames[0] = getString(R.string.str_title_badiane);
        SpicesNames[1] = getString(R.string.str_title_paprika);
        SpicesNames[2] = getString(R.string.str_title_pavotbleu);
        SpicesNames[3] = getString(R.string.str_title_canelle);
        SpicesNames[4] = getString(R.string.str_title_curcuma);
        SpicesNames[5] = getString(R.string.str_title_reglisse);
        SpicesNames[6] = getString(R.string.str_title_nigelle);
        SpicesNames[7] = getString(R.string.str_title_sumac);

        //initService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //releaseService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //initService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //releaseBarCodeReceiver();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        //openBarCode();
        initBarCodeReceiver();
        initStateReceiver();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        releaseBarCodeReceiver();
        releaseStateReceiver();
        //closeBarCode();
    }

    protected void initService() {
		 /* Intent i = new Intent();
		  i.setClassName( "com.ingenico.pclservice", "com.ingenico.pclservice.PclService" );
		  getApplicationContext().bindService( i, this, Context.BIND_AUTO_CREATE);*/
        Log.d(TAG, String.format("initService mBound=%d", mBound ? 1 : 0));
        if (!mBound) {
            SslObject sslObject = new SslObject("serverb.p12", "coucou");
            SharedPreferences settings = getSharedPreferences("PCLSERVICE", MODE_PRIVATE);
            boolean enableLog = settings.getBoolean("ENABLE_LOG", false);
            mServiceConnection = new PclServiceConnection();
            Intent intent = new Intent(this, PclService.class);
            intent.putExtra("PACKAGE_NAME", "com.ingenico.spicesshop");
            intent.putExtra("FILE_NAME", "pairing_addr.txt");
            intent.putExtra("ENABLE_LOG", enableLog);
            intent.putExtra("SSL_OBJECT", sslObject);
            mBound = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    protected void releaseService() {
        //getApplicationContext().unbindService( this );
        Log.d(TAG, String.format("releaseService mBound=%d", mBound ? 1 : 0));
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }


    public boolean isCompanionConnected() {
        boolean bRet = false;
        if (mPclService != null) {
            byte result[] = new byte[1];
            {
                if (mPclService.serverStatus(result) == true) {
                    if (result[0] == 0x10)
                        bRet = true;
                }
            }
        }
        return bRet;
    }

    boolean getTermInfo() {
        boolean ret = false;
        byte[] serialNbr = new byte[4];
        byte[] productNbr = new byte[4];
        if (mPclService != null) {
            {
                ret = mPclService.getTerminalInfo(serialNbr, productNbr);
                ByteBuffer bbSN = ByteBuffer.wrap(serialNbr);
                ByteBuffer bbPN = ByteBuffer.wrap(productNbr);
                bbSN.order(ByteOrder.LITTLE_ENDIAN);
                bbPN.order(ByteOrder.LITTLE_ENDIAN);
                SN = bbSN.getInt();
                PN = bbPN.getInt();
            }
        }
        return ret;

    }

    /**
     * BarCode
     */
    private void initBarCodeReceiver() {
        if (m_BarCodeReceiver == null) {
            m_BarCodeReceiver = new BarCodeReceiver(this);
            IntentFilter intentfilter = new IntentFilter("com.ingenico.pclservice.action.BARCODE_EVENT");
            intentfilter.addAction("com.ingenico.pclservice.action.BARCODE_CLOSED");
            registerReceiver(m_BarCodeReceiver, intentfilter);
        }
    }

    private void releaseBarCodeReceiver() {
        if (m_BarCodeReceiver != null) {
            unregisterReceiver(m_BarCodeReceiver);
            m_BarCodeReceiver = null;
        }
    }

    public boolean openBarCode() {
        Log.d(TAG, "openBarCode");
        if ((mPclService != null) && !m_BarCodeActivated)
            m_BarCodeActivated = setBarCodeActivation(true);

        return m_BarCodeActivated;
    }

    public boolean closeBarCode() {
        Log.d(TAG, "closeBarCode");
        if ((mPclService != null) && m_BarCodeActivated)
            m_BarCodeActivated = !setBarCodeActivation(false);

        return m_BarCodeActivated;
    }

    public void reopenBarCode() {
        if (m_BarCodeActivated)
            closeBarCode();
        openBarCode();
    }

    private boolean setBarCodeActivation(boolean activateBarCode) {
        boolean result = false;
        byte array[] = null;

        if (mPclService != null) {
            array = new byte[1];
            if (activateBarCode) {
                result = mPclService.openBarcode(array);
                if (result == true) {
                    if (array[0] != 0)
                        result = false;
                }
            } else {
                mPclService.closeBarcode(array);
                result = true;
            }

        }

        return result;
    }

    /**
     * Printer
     */
    public boolean openPrinter() {
        if ((mPclService != null) && !m_PrinterActivated)
            m_PrinterActivated = setPrinterActivation(true);

        return m_PrinterActivated;
    }

    public boolean closePrinter() {
        if (m_PrinterActivated)
            m_PrinterActivated = !setPrinterActivation(false);

        return m_PrinterActivated;
    }

    private boolean setPrinterActivation(boolean activatePrinter) {
        boolean result = false;
        byte array[] = null;

        if (mPclService != null) {
            array = new byte[1];
            if (activatePrinter)
                result = mPclService.openPrinter(array);
            else
                result = mPclService.closePrinter(array);
        }

        return result;
    }

    public boolean printText(String strText) {
        boolean Result = false;

        if (openPrinter()) {
            byte[] PrintResult = new byte[1];
            Result = mPclService.printText(strText, PrintResult);
            Log.d(TAG, String.format("TO PRINT : %s", strText));
            Log.d(TAG, String.format("printText result=%d", PrintResult[0]));

            closePrinter();
        }

        return Result;
    }

    public boolean printBitmap(byte[] bmpBuf, int bmpSize) {
        boolean result = false;

        if (bmpBuf != null) {
            if (openPrinter()) {
                byte[] printResult = new byte[1];
                result = mPclService.printBitmap(bmpBuf, bmpSize, printResult);
                Log.d(TAG, String.format("printBitMap result=%d", printResult[0]));

                closePrinter();
            }
        }

        return result;
    }

    public boolean doTransaction(TransactionIn transIn, TransactionOut transOut) {
        boolean Result = false;

        if (mPclService != null) {
            Log.d(TAG, String.format("doTransaction"));
            Result = mPclService.doTransaction(transIn, transOut);
        }

        return Result;
    }

    private class BarCodeReceiver extends BroadcastReceiver {
        private CommonActivity ViewOwner = null;

        @SuppressLint("UseValueOf")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.ingenico.pclservice.action.BARCODE_CLOSED")) {
                ViewOwner.onBarCodeClosed();
            } else {
                byte abyte0[] = intent.getByteArrayExtra("barcode");
                String BarCodeStr = new String(abyte0);
                int symbology = intent.getIntExtra("barcode_symbology", -2);


                ViewOwner.onBarCodeReceived(BarCodeStr, symbology);
            }
        }

        BarCodeReceiver(CommonActivity receiver) {
            super();
            ViewOwner = receiver;
        }
    }

    public void addToCart(SpicesTypes type, int quantity) {
        if ((type.Value() < SpicesTypes.MAX_SpicesTypes.Value()) && (quantity > 0)) {
            byte spiceID = (byte) (type.Value() - 1);
            struct_spice spice = new struct_spice(SpicesNames[spiceID], price[spiceID], quantity);
            appContext.addSpice(spice);
        }
    }

    private void initStateReceiver() {
        if (m_StateReceiver == null) {
            m_StateReceiver = new StateReceiver(this);
            IntentFilter intentfilter = new IntentFilter("com.ingenico.pclservice.intent.action.STATE_CHANGED");
            registerReceiver(m_StateReceiver, intentfilter);
        }
    }

    private void releaseStateReceiver() {
        if (m_StateReceiver != null) {
            unregisterReceiver(m_StateReceiver);
            m_StateReceiver = null;
        }
    }

    private class StateReceiver extends BroadcastReceiver {
        private CommonActivity ViewOwner = null;

        @SuppressLint("UseValueOf")
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra("state");
            Log.d(TAG, String.format("receiver: State %s", state));
            ViewOwner.onStateChanged(state);
        }

        StateReceiver(CommonActivity receiver) {
            super();
            ViewOwner = receiver;
        }
    }
}
