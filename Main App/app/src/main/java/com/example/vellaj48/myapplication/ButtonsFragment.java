package com.example.vellaj48.myapplication;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ButtonsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ButtonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonsFragment extends Fragment {


    public boolean decimalUsed = false;
    public boolean numbersRequired = false;  // used only for after decimal used
    public boolean operatorRequired = false;  // used only for after right parentheses ")"
    public boolean operatorUsed = true;
    public int leftParenthesesUsed = 0;
    public int decimalWasUsed = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_buttons,container,false);
    }



}