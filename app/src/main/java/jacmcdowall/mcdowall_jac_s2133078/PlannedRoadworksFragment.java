// Jac Stephen McDowall
// S2133078
// Glasgow Caledonian University

package jacmcdowall.mcdowall_jac_s2133078;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlannedRoadworksFragment extends Fragment {
    Fragment plannedRoadworksDetailsFragment;
    Bundle plannedRoadworksDetailsBundle;

    ListView plannedRoadworksList;
    View view;

    SearchView searchView;

    boolean shouldRefreshOnResume = false;

    ArrayList<Item> parsedArray = new ArrayList<Item>();

    public PlannedRoadworksFragment() {
        // Required constructor.
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.planned_roadworks_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        plannedRoadworksList = view.findViewById(R.id.plannedRoadworksList);

        XmlPullParserHandler xmlPullParserHandler = new XmlPullParserHandler("https://trafficscotland.org/rss/feeds/plannedroadworks.aspx");
        Thread parserThread = new Thread(xmlPullParserHandler);
        parserThread.start();

        try {
            parserThread.join(0);
            parsedArray = xmlPullParserHandler.getItemArrayList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<String> titleArray = new ArrayList<String>();

        for (int i = 0; i < parsedArray.size(); i++) {
            titleArray.add(parsedArray.get(i).getTitle());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, titleArray);
        plannedRoadworksList.setAdapter(arrayAdapter);

        plannedRoadworksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                plannedRoadworksDetailsFragment = new DetailsFragment();
                plannedRoadworksDetailsBundle = new Bundle();
                plannedRoadworksDetailsBundle.putString("title", parsedArray.get(position).getTitle());
                plannedRoadworksDetailsBundle.putString("description", parsedArray.get(position).getDescription().replace("<br />", "\n"));
                plannedRoadworksDetailsBundle.putString("geolocation", parsedArray.get(position).getGeoLocation());
                plannedRoadworksDetailsFragment.setArguments(plannedRoadworksDetailsBundle);
                FragmentManager currentIncidentsDetailsFragmentManager = getFragmentManager();
                FragmentTransaction currentIncidentsDetailsFragmentTransaction = currentIncidentsDetailsFragmentManager.beginTransaction();
                currentIncidentsDetailsFragmentTransaction.replace(R.id.displayFragment, plannedRoadworksDetailsFragment).addToBackStack(null).commit();
            }
        });

        searchView = view.findViewById(R.id.searchPlannedRoadworksBar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<String> titleArray = new ArrayList<String>();

                ArrayList<String> descriptionArray = new ArrayList<String>();

                ArrayList<String> filteredArray = new ArrayList<String>();

                String fuzzySearchResult = "";

                for (int i = 0; i < parsedArray.size(); i++) {
                    titleArray.add(parsedArray.get(i).getTitle());
                    descriptionArray.add(parsedArray.get(i).getDescription());
                }

                String searchViewInput = searchView.getQuery().toString().toLowerCase();

                FuzzySearchBuilder fuzzySearchBuilder = new FuzzySearchBuilder(searchViewInput);
                Thread searchThread = new Thread(fuzzySearchBuilder);
                searchThread.start();

                try {
                    searchThread.join(0);
                    fuzzySearchResult = fuzzySearchBuilder.getResultCombined();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < titleArray.size(); i++) {
                    if (titleArray.get(i).toLowerCase().matches(fuzzySearchResult)) {
                        filteredArray.add(titleArray.get(i));
                    } else if (descriptionArray.get(i).toLowerCase().matches(fuzzySearchResult)) {
                        filteredArray.add(titleArray.get(i));
                    }
                }

                ArrayAdapter searchArrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, filteredArray);
                plannedRoadworksList.setAdapter(searchArrayAdapter);

                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        shouldRefreshOnResume = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        shouldRefreshOnResume = false;
    }
}
