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
public class LeagueTable extends Fragment {


    public LeagueTable() {
        // Required empty public constructor
    }

    public static LeagueTable newInstance() {
        return new LeagueTable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_league_table, container, false);
    }


}
