package com.viavr.calculatorprojectwork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


public class MainActivity extends AppCompatActivity {

    private EditText m_InputOutput;
    private Set<String> m_OperatorStrings = new HashSet<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_InputOutput = findViewById(R.id.editTextNumber);
        m_InputOutput.requestFocus();
        m_InputOutput.setShowSoftInputOnFocus(false);

        SetSuperScriptButton(findViewById(R.id.btnSquare), "X2", "2");

        m_OperatorStrings.add("+");
        m_OperatorStrings.add("-");
        m_OperatorStrings.add("*");
        m_OperatorStrings.add("/");
    }


//region ----- OnButtonPressed functions -----
    public void OnAddPressed(View view) {
        if(!CanAddOperator()) return;
        AddInput("+");
    }

    public void OnSubtractPressed(View view) {
        if(!CanAddOperator()) return;
        AddInput("-");
    }

    public void OnMultiplyPressed(View view) {
        if(!CanAddOperator()) return;
        AddInput("*");
    }

    public void OnDividePressed(View view) {
        if(!CanAddOperator()) return;
        AddInput("/");
    }

    public  void OnNumericPressed(View view){
        Button b = (Button)view;
        String str = b.getText().toString();
        AddInput(str);
    }

    public void OnCalculatePressed(View view){
        Calculate();
    }

    public void OnClearPressed(View view){
        ClearInputOutput();
    }

    public void OnBackspacePressed(View view){
        RemoveCharacterAtCursor();
    }

    public void OnSquaredPressed(View view){
        SquareInput();
    }

    public void OnOneDividedByPressed(View view){
        OneDividedByX();
    }

//endregion

//region ----- Main logic functions -----
    private void Calculate(){
        String inputString = String.valueOf(m_InputOutput.getText());
        Expression expression = new ExpressionBuilder(inputString).build();
        try {
            double result = expression.evaluate();
            SetOutput(result);
        }catch (ArithmeticException ex){

        }
        SetSelectionToEnd();
    }

    private void SquareInput(){
        Double inputResult = GetInputOrEvaluate();
        SetOutput(inputResult*inputResult);
        SetSelectionToEnd();
    }

    private void OneDividedByX(){
        Double inputResult = GetInputOrEvaluate();
        SetOutput(1/inputResult);
        SetSelectionToEnd();
    }

    // Used when 1/x or x^2 is used. Checks if there's an equation, evaluates it if found and returns the value for the necessary equation.
    private double GetInputOrEvaluate(){
        double result = 0;
        String inputString = String.valueOf(m_InputOutput.getText());

        if(InputContainsOperator()){
            try {
                Expression expression = new ExpressionBuilder(inputString).build();
                result = expression.evaluate();
            }catch (ArithmeticException ex){

            }
        }
        else
        {
            result = Double.valueOf(inputString);
        }
        return result;
    }

    private void AddInput(String _input){
        String in = String.valueOf(m_InputOutput.getText());
        in += _input;
        m_InputOutput.setText(in);
        SetSelectionToEnd();
    }

    // Sets formatted output (basically removes unnecessary .0)
    private void SetOutput(Double _output){
        NumberFormat format = new DecimalFormat("0.#");
        m_InputOutput.setText(format.format(_output));
    }

    private void ClearInputOutput(){
        m_InputOutput.setText("");
    }

    // Logic for backspace functionality
    private void RemoveCharacterAtCursor() {
        int cursorPosition = m_InputOutput.getSelectionStart();
        if (cursorPosition > 0) {
            m_InputOutput.setText(m_InputOutput.getText().delete(cursorPosition - 1, cursorPosition));
            m_InputOutput.setSelection(cursorPosition-1);
        }
    }

    // Checks if there is any numeric input (operator can't be first) and if there isn't already an operator added
    private boolean CanAddOperator(){
        return String.valueOf(m_InputOutput.getText()).length() > 0 && !IsLastSymbolOperator();
    }

    private boolean IsLastSymbolOperator(){
        String str = String.valueOf(m_InputOutput.getText());

        if(str.length() <= 0) return false;

        String last = str.substring((str.length() - 1));
        return m_OperatorStrings.contains(last);
    }

    private boolean InputContainsOperator(){
        for(String s : m_OperatorStrings){
            if(m_InputOutput.getText().toString().contains(s)) return true;
        }
        return false;
    }

    private void SetSelectionToEnd(){
        Selection.setSelection(m_InputOutput.getText(), m_InputOutput.getText().length());
    }

    private void SetSuperScriptButton(Button _button, String _text, String _charToSuper){
        String text = _text;
        SuperscriptSpan superscriptSpan = new SuperscriptSpan();
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(
                superscriptSpan,
                text.indexOf(_charToSuper),
                text.indexOf(_charToSuper) + String.valueOf(_charToSuper).length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        _button.setText(builder);
    }

//endregion
}