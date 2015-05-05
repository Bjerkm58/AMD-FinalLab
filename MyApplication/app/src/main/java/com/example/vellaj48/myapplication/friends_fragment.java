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
 * {@link friends_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link friends_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class friends_fragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_friends_fragment,container,false);

        // see getFriendsEquation
    }
}
