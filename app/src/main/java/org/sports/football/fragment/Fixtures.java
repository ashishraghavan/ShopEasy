package org.sports.football.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.sports.football.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fixtures extends Fragment {


    public Fixtures() {
        // Required empty public constructor
    }

    public static Fixtures newInstance() {
        return new Fixtures();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fixtures, container, false);
    }


}
