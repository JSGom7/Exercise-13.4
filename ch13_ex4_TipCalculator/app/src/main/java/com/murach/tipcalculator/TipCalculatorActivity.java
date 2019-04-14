package com.murach.tipcalculator;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class TipCalculatorActivity extends Activity 
implements OnEditorActionListener, OnClickListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;   
    private Button   percentUpButton;
    private Button   percentDownButton;
    private Button saveButton;
    private TextView tipTextView;
    private TextView totalTextView;
    
    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;
    
    // set up preferences
    private SharedPreferences prefs;
    private TipDatabase tipDatabase;

    private long id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);

        tipDatabase = new TipDatabase(this);

        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        saveButton = (Button) findViewById(R.id.button_save);

        // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);

        // get default SharedPreferences object
        prefs = PreferenceManager.getDefaultSharedPreferences(this);        
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = prefs.edit();        
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();

        // get the instance variables
        billAmountString = prefs.getString("billAmountString", "");
        tipPercent = prefs.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);
        
        // calculate and display
        calculateAndDisplay();

        //Displaying rows from tips_table
        ArrayList<Tip> tips = tipDatabase.getTips();
        for(Tip tip : tips){
            Log.d("tip_table", "ID: " + Long.toString(tip.getId()) + " " +
                    "Bill date: " + tip.getDateStringFormatted() + " " +
                    "Bill amount: " + tip.getBillAmountFormatted() + " " +
                    "Tip percent: " + tip.getTipPercentFormatted() + "\n");
        }
        id = tips.size();
    }
    
    public void calculateAndDisplay() {        

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount; 
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }
        
        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;
        
        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));
        
        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
    		actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }        
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.percentDownButton:
            tipPercent = tipPercent - .01f;
            calculateAndDisplay();
            break;
        case R.id.percentUpButton:
            tipPercent = tipPercent + .01f;
            calculateAndDisplay();
            break;
        case R.id.button_save:
            Tip tip = new Tip(id++, 0,
                    Float.parseFloat(billAmountEditText.getText().toString()),
                    tipPercent);
            tipDatabase.insertTip(tip);
            billAmountEditText.setText("");
            tipPercent = 0;
            calculateAndDisplay();
        }
    }


}