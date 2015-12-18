package org.sports.football.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.collect.Lists;

import org.apache.http.message.BasicHeader;
import org.sports.football.R;
import org.sports.football.activity.LeagueDetail;
import org.sports.football.model.League;
import org.sports.football.network.Response;
import org.sports.football.network.ServiceCall;
import org.sports.football.util.Constants;
import org.sports.football.util.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeagueFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeagueFragment extends ListFragment {

    private ArrayAdapter<League> leagueAdapter;
    private final List<League> leagueList = Lists.newArrayList();
    private static final String TAG = LeagueFragment.class.getSimpleName();

    public static LeagueFragment newInstance() {
        return new LeagueFragment();
    }

    public LeagueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_league, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Intent leagueDetail = new Intent(getActivity(), LeagueDetail.class);
        leagueDetail.putExtra(Constants.SERIALIZABLE_LEAGUE,leagueAdapter.getItem(position));
        startActivity(leagueDetail);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        leagueAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,leagueList);
        getListView().setAdapter(leagueAdapter);
        final List<BasicHeader> basicHeaderList = Lists.newArrayList();
        basicHeaderList.add(new BasicHeader("X-Auth-Token", Constants.FOOTBALL_DATA_KEY));
        final ServiceCall serviceCall = new ServiceCall.ServiceCallBuilder()
                .overrideCache(false)
                .shouldFollowRedirects(true)
                .shouldLog(true)
                .setMethod(ServiceCall.EMethodType.GET)
                .setHeaderElements(basicHeaderList)
                .setUrl("http://api.football-data.org/v1/soccerseasons?season=2015")
                .build();
        new AsyncGetLeagues().execute(serviceCall);
    }

    class AsyncGetLeagues extends AsyncTask<ServiceCall,Void,List<League>> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        @SuppressWarnings("unchecked")
        protected List<League> doInBackground(ServiceCall... params) {

            final ServiceCall serviceCall = params[0];
            try {
                final Response response = serviceCall.executeRequest();
                final List<Map<String,Object>> leagueList = response.getResponseAsType(List.class);
                if ( leagueList == null || leagueList.isEmpty() ) {
                    return Collections.EMPTY_LIST;
                }
                List<League> leagues = Lists.newArrayList();
                for ( Map<String,Object> league : leagueList ) {
                    leagues.add(Utils.getSafeMapper().convertValue(league,League.class));
                }

                return leagues;
            } catch (Exception e) {
                Log.e(TAG,"Failed to retrieve leagues with message "+e.getMessage(),e);
                return Collections.EMPTY_LIST;
            }
        }

        @Override
        protected void onPostExecute(List<League> leagues) {
            super.onPostExecute(leagues);
            LeagueFragment.this.leagueList.addAll(leagues);
            leagueAdapter.notifyDataSetChanged();
        }
    }
}
