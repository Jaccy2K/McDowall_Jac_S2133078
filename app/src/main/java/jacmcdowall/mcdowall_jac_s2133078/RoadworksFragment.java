// Jac Stephen McDowall
// S2133078
// Glasgow Caledonian University

package jacmcdowall.mcdowall_jac_s2133078;

import static jacmcdowall.mcdowall_jac_s2133078.FuzzySearchBuilder.*;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.time.*;
import java.util.concurrent.TimeUnit;

public class RoadworksFragment extends Fragment {
    Fragment roadworksDetailsFragment;
    Bundle roadworksDetailsBundle;

    ListView roadworksList;
    View view;

    SearchView searchView;

    boolean shouldRefreshOnResume = true;

    ArrayList<String> titleArray = new ArrayList<String>();
    ArrayList<Long> daysArray = new ArrayList<Long>();

    ArrayList<Item> parsedArray = new ArrayList<Item>();

    public RoadworksFragment() {
        // Required constructor.
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.roadworks_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roadworksList = view.findViewById(R.id.roadworksList);

        XmlPullParserHandler xmlPullParserHandler = new XmlPullParserHandler("https://trafficscotland.org/rss/feeds/roadworks.aspx");
        Thread parserThread = new Thread(xmlPullParserHandler);
        parserThread.start();

        try {
            parserThread.join(0);
            parsedArray = xmlPullParserHandler.getItemArrayList();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < parsedArray.size(); i++) {
            titleArray.add(parsedArray.get(i).getTitle());

            Date d1 = null;
            Date d2 = null;
            long daysBetween = 0;

            String[] splicedDateList = parsedArray.get(i).getDescription().split("<br />");
            ArrayList<String> splicedDateArray = new ArrayList<String>(Arrays.asList(splicedDateList));
            if (splicedDateArray.size() == 3) {
                splicedDateArray.remove(2);
            }
            String splicedDateStart = splicedDateArray.get(0).replace("Start Date: ", "");
            splicedDateStart = splicedDateStart.replaceAll(" - \\d\\d[:]\\d\\d", "");
            String splicedDateEnd = splicedDateArray.get(1).replace("End Date: ", "");
            splicedDateEnd = splicedDateEnd.replaceAll(" - \\d\\d[:]\\d\\d", "");

            splicedDateArray.remove(1);
            splicedDateArray.remove(0);
            splicedDateArray.add(splicedDateStart);
            splicedDateArray.add(splicedDateEnd);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEEE, dd MMMMM yyy", Locale.ENGLISH);

            try {
                d1 = simpleDateFormat.parse((String) splicedDateArray.get(0));
                d2 = simpleDateFormat.parse((String) splicedDateArray.get(1));
                daysBetween = getDateDiff(d1, d2, TimeUnit.DAYS);

                daysArray.add(daysBetween);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, titleArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                if (daysArray.get(position) > 42) {
                    view.setBackgroundColor(getResources().getColor(R.color.red));
                } else if (daysArray.get(position) > 21) {
                    view.setBackgroundColor(getResources().getColor(R.color.yellow));
                } else {
                    view.setBackgroundColor(getResources().getColor(R.color.green));
                }

                return view;
            }
        };
        roadworksList.setAdapter(arrayAdapter);

        roadworksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                roadworksDetailsFragment = new DetailsFragment();
                roadworksDetailsBundle = new Bundle();
                roadworksDetailsBundle.putString("title", parsedArray.get(position).getTitle());
                roadworksDetailsBundle.putString("description", parsedArray.get(position).getDescription().replace("<br />", "\n"));
                roadworksDetailsBundle.putString("geolocation", parsedArray.get(position).getGeoLocation());
                roadworksDetailsFragment.setArguments(roadworksDetailsBundle);
                FragmentManager roadworksDetailsFragmentManager = getFragmentManager();
                FragmentTransaction roadworksDetailsFragmentTransaction = roadworksDetailsFragmentManager.beginTransaction();
                roadworksDetailsFragmentTransaction.replace(R.id.displayFragment, roadworksDetailsFragment).addToBackStack(null).commit();
            }
        });

        searchView = view.findViewById(R.id.searchRoadworksBar);

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

                ArrayAdapter<String> searchArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, filteredArray) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);

                        if (daysArray.get(position) > 42) {
                            view.setBackgroundColor(getResources().getColor(R.color.red));
                        } else if (daysArray.get(position) > 21) {
                            view.setBackgroundColor(getResources().getColor(R.color.yellow));
                        } else {
                            view.setBackgroundColor(getResources().getColor(R.color.green));
                        }

                        return view;
                    }
                };
                roadworksList.setAdapter(searchArrayAdapter);

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

    public long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMilliseconds = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMilliseconds,TimeUnit.MILLISECONDS);
    }
}
