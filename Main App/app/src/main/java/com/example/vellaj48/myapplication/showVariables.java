package com.example.vellaj48.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link showVariables.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link showVariables#newInstance} factory method to
 * create an instance of this fragment.
 */
public class showVariables extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_show_variables,container,false);
    }
}
