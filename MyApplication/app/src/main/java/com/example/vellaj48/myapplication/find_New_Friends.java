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
 * {@link find_New_Friends.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link find_New_Friends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class find_New_Friends extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_find__new__friends,container,false);
        // see getFriendsEquation
    }

}
