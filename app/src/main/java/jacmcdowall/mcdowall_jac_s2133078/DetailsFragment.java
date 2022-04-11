// Jac Stephen McDowall
// S2133078
// Glasgow Caledonian University

package jacmcdowall.mcdowall_jac_s2133078;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DetailsFragment extends Fragment {
    TextView detailTitleInformation, detailDescriptionInformation, detailGeoLocationInformation;
    View view;

    public DetailsFragment() {
        // Required constructor.
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();

        detailTitleInformation = view.findViewById(R.id.detailTitleInformationText);
        detailDescriptionInformation = view.findViewById(R.id.detailDescriptionInformationText);
        detailGeoLocationInformation = view.findViewById(R.id.detailGeoLocationInformationText);

        detailTitleInformation.setText(bundle.getString("title"));
        detailDescriptionInformation.setText(bundle.getString("description"));
        detailGeoLocationInformation.setText(bundle.getString("geolocation"));
    }
}
