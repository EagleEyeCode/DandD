package de.eagleeye.dandd.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
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

    private int[] modelMonsterId;

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

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                hideKeyboard(MainActivity.this);
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_sources, R.id.nav_books, R.id.nav_items, R.id.nav_spells, R.id.nav_monsters)
                .setDrawerLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_main_content);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            currentFragmentId = destination.getId();
            hideKeyboard(MainActivity.this);
            if(sqlFragments.contains(currentFragmentId)){
                fab.setImageResource(R.drawable.baseline_filter_list_black_18dp);
                fab.setVisibility(View.VISIBLE);
            }else{
                fab.setVisibility(View.GONE);
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
        hideKeyboard(this);
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
            if(modelMonsterId != null && modelMonsterId.length == 2) {
                Bundle args = new Bundle();
                args.putInt("id", modelMonsterId[0]);
                args.putInt("sourceId", modelMonsterId[1]);
                navController.navigate(R.id.nav_monsters_model_show, args);
            }
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

    public void setModelMonsterId(int[] modelMonsterId) {
        this.modelMonsterId = modelMonsterId;
        if(modelMonsterId != null) {
            fab.setImageResource(R.drawable.ar_foreground);
            fab.setVisibility(View.VISIBLE);
        }else {
            fab.setVisibility(View.GONE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
