package com.example.vellaj48.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class equationSteps extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_equation_steps,container,false);
    }

    public void setEquationSteps(String s)
    {
        EditText editText = (EditText)getView().findViewById(R.id.textView2);
        editText.setText(s);
    }
}
