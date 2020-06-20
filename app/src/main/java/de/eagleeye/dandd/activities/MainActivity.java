package de.eagleeye.dandd.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;

import de.eagleeye.dandd.R;
import de.eagleeye.dandd.fragments.base.BaseFilterFragment;

public class MainActivity extends AppCompatActivity {
    private static final ArrayList<Integer> mainFragments = new ArrayList<>(Arrays.asList(R.id.nav_sources, R.id.nav_books, R.id.nav_items, R.id.nav_spells, R.id.nav_monsters));
    private static final ArrayList<Integer> pdfFragments = new ArrayList<>(Arrays.asList(R.id.nav_books, R.id.nav_pdf));
    private static final ArrayList<Integer> sqlFragments = new ArrayList<>(Arrays.asList(R.id.nav_items, R.id.nav_spells, R.id.nav_monsters));
    private static final ArrayList<Integer> filterFragments = new ArrayList<>(Arrays.asList(R.id.nav_items_filter, R.id.nav_spells_filter, R.id.nav_monsters_filter));

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private Snackbar snackbar;

    private NavController navController;
    private int currentFragmentId;
    private long lastTimeMillis;
    private int fragmentSwitches;

    private Handler snackbarHandler;
    private Runnable snackbarRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentSwitches = 0;

        snackbarHandler = new Handler();
        snackbarRunnable = () -> snackbar.dismiss();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> fabClick());

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_sources, R.id.nav_books, R.id.nav_items, R.id.nav_spells, R.id.nav_monsters)
                .setDrawerLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_main_content);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            currentFragmentId = destination.getId();
            if(sqlFragments.contains(currentFragmentId)){
                fab.setVisibility(View.VISIBLE);
            }else{
                fab.setVisibility(View.GONE);
            }
            if(currentFragmentId == R.id.nav_monsters_show){
                fab.setVisibility(View.VISIBLE);
            }

            if(filterFragments.contains(currentFragmentId)){
                getMenuInflater().inflate(R.menu.tb_filter, toolbar.getMenu());
            }else{
                toolbar.getMenu().clear();
            }

            if(pdfFragments.contains(currentFragmentId) || sqlFragments.contains(currentFragmentId)){
                if(fragmentSwitches > 0) getPreferences(MODE_PRIVATE).edit().putInt("last_main_fragment", currentFragmentId).apply();
            }

            if(!sqlFragments.contains(currentFragmentId) && snackbar != null && snackbar.isShown()){
                snackbar.dismiss();
            }
            fragmentSwitches++;
        });
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mainFragments.contains(currentFragmentId)) {
                    if (drawer.isDrawerOpen(drawer.getForegroundGravity())) {
                        drawer.closeDrawer(Gravity.LEFT);
                    } else {
                        drawer.openDrawer(Gravity.LEFT);
                    }
                }else{
                    onBackPressed();
                }
                return true;
            case R.id.filter_clear:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(filterFragments.contains(navController.getCurrentDestination().getId())){
            getMenuInflater().inflate(R.menu.tb_filter, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_main_content);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if(mainFragments.contains(currentFragmentId)){
            finish();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        lastTimeMillis = System.currentTimeMillis();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(pdfFragments.contains(currentFragmentId) || sqlFragments.contains(currentFragmentId)) navController.navigate(getPreferences(MODE_PRIVATE).getInt("last_main_fragment", R.id.nav_sources));
        if(lastTimeMillis != 0){
            if(System.currentTimeMillis() - lastTimeMillis >= 1800000){
                sendToSplashScreen();
            }
            lastTimeMillis = 0;
        }
        super.onResume();
    }

    public void showSnackbar(int filteredItemsCount) {
        if(snackbar == null) snackbar = Snackbar.make(fab, "", BaseTransientBottomBar.LENGTH_INDEFINITE);
        if(filteredItemsCount == 1) snackbar.setText(filteredItemsCount + " Result");
        else snackbar.setText(filteredItemsCount + " Results");
        if(!snackbar.isShown()) snackbar.show();
        snackbarHandler.removeCallbacks(snackbarRunnable);
        if(filteredItemsCount != 0) snackbarHandler.postDelayed(snackbarRunnable, 2000);
    }

    private void fabClick(){
        if(mainFragments.contains(currentFragmentId)){
            showFilter();
        }else {
            Bundle args = new Bundle();
            navController.navigate(R.id.nav_monsters_model_show, args);
        }
    }

    private void showFilter() {
        switch(currentFragmentId){
            case R.id.nav_items:
                navController.navigate(R.id.nav_items_filter);
                break;
            case R.id.nav_spells:
                navController.navigate(R.id.nav_spells_filter);
                break;
            case R.id.nav_monsters:
                navController.navigate(R.id.nav_monsters_filter);
                break;
        }
    }

    private void sendToSplashScreen(){
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        MainActivity.this.startActivity(intent);
        MainActivity.this.finish();
    }
}
