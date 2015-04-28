package com.example.vellaj48.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link createVariables.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link createVariables#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createVariables extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_create_variables,container,false);
    }
    public String getTextVariable()
    {
        EditText editText = (EditText)getView().findViewById(R.id.editText8);
        String expressionToSend = editText.getText().toString();
        return expressionToSend;
    }
    public void setEquationVariable(String s)
    {
        EditText editText = (EditText)getView().findViewById(R.id.editText8);
        String expressionToSet = s;
        editText.setText(expressionToSet);
    }
}
