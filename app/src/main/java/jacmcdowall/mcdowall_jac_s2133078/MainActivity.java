// Jac Stephen McDowall
// S2133078
// Glasgow Caledonian University

package jacmcdowall.mcdowall_jac_s2133078;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    Fragment mainFragment, currentIncidentsFragment, roadworksFragment, plannedRoadworksFragment;
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Don't delete this!
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mainFragment = new MainFragment();
        currentIncidentsFragment = new CurrentIncidentsFragment();
        roadworksFragment = new RoadworksFragment();
        plannedRoadworksFragment = new PlannedRoadworksFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.displayFragment, mainFragment).commit();

        toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.currentIncidentsNavItem:
                FragmentManager currentIncidentsFragmentManager = getFragmentManager();
                FragmentTransaction currentIncidentsFragmentTransaction = currentIncidentsFragmentManager.beginTransaction();
                currentIncidentsFragmentTransaction.replace(R.id.displayFragment, currentIncidentsFragment).addToBackStack(null).commit();
                break;
            case R.id.roadworksNavItem:
                FragmentManager roadworksFragmentManager = getFragmentManager();
                FragmentTransaction roadworksFragmentTransaction = roadworksFragmentManager.beginTransaction();
                roadworksFragmentTransaction.replace(R.id.displayFragment, roadworksFragment).addToBackStack(null).commit();
                break;
            case R.id.plannedRoadworksNavItem:
                FragmentManager plannedRoadworksFragmentManager = getFragmentManager();
                FragmentTransaction plannedRoadworksFragmentTransaction = plannedRoadworksFragmentManager.beginTransaction();
                plannedRoadworksFragmentTransaction.replace(R.id.displayFragment, plannedRoadworksFragment).addToBackStack(null).commit();
                break;
        }
        return true;
    }
}