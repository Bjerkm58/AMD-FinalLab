package com.example.vellaj48.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class EquationFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_equation,container,false);
    }
    public String getText()
    {
        EditText editText = (EditText)getView().findViewById(R.id.equation);
        String expressionToSend = editText.getText().toString();
        return expressionToSend;
    }

    public void setEquation(String s)
    {
        EditText editText = (EditText)getView().findViewById(R.id.equation);
        String expressionToSet = s;
        editText.setText(expressionToSet);
    }

}
