package com.ingenico.pcltestappwithlib;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SymbologiesActivity extends ListActivity {

	private CheckBox mCheckBoxSelectAll;
	int mArraySymbologies[];
	
	String[] symbologies = new String[] {
		""	
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.symbologies);
		String[] symbologies = getResources().getStringArray(R.array.symbo_array);
		mCheckBoxSelectAll = (CheckBox)findViewById(R.id.cbSelectAll);
		
		/** Defining array adapter to store items for the listview **/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, symbologies);
 
        /** Setting the arrayadapter for this listview  **/
        getListView().setAdapter(adapter);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
	            int checkedItemCount = getCheckedItemCount();
	 
	            if(getListView().getCount()==checkedItemCount)
	            	mCheckBoxSelectAll.setChecked(true);
	            else
	            	mCheckBoxSelectAll.setChecked(false);
	        }
		});
		
		mCheckBoxSelectAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CheckBox chk = (CheckBox) v;
                int itemCount = getListView().getCount();
                for(int i=0 ; i < itemCount ; i++){
                    getListView().setItemChecked(i, chk.isChecked());
                }
			}
		});
	}

	@Override
	protected void onStart() {
		SharedPreferences settings = getSharedPreferences("SYMBOLOGIES", 0);
		int itemCount = getResources().getStringArray(R.array.symbo_array).length;
		
		for (int i=0; i<itemCount; i++)
		{
			if (settings.getBoolean("SYMBO_"+i, false) == true)
			{
				getListView().setItemChecked(i, true);
			}
		}
		int checkedItemCount = getCheckedItemCount();
		 
        if(getListView().getCount()==checkedItemCount)
        	mCheckBoxSelectAll.setChecked(true);
        else
        	mCheckBoxSelectAll.setChecked(false);
		super.onStart();
	}

	@Override
	protected void onStop() {
		Editor editor;
		SharedPreferences settings = getSharedPreferences("SYMBOLOGIES", 0);
		SparseBooleanArray positions = getListView().getCheckedItemPositions();
		int itemCount = getListView().getCount();
		editor = settings.edit();
		 
        for(int i=0;i<itemCount;i++){
            if(positions.get(i))
            {
                editor.putBoolean("SYMBO_"+i, true);
            }
            else
            {
            	editor.putBoolean("SYMBO_"+i, false);
            }
        }
        editor.putInt("SYMBO_COUNT", itemCount);
        editor.commit();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	
	/**
    *
    * Returns the number of checked items
    */
    private int getCheckedItemCount(){
        int cnt = 0;
        SparseBooleanArray positions = getListView().getCheckedItemPositions();
        int itemCount = getListView().getCount();
 
        for(int i=0;i<itemCount;i++){
            if(positions.get(i))
                cnt++;
        }
        return cnt;
    }
    
    

}