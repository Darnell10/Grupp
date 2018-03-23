package com.example.c4q.capstone.userinterface.navdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.c4q.capstone.LoginActivity;
import com.example.c4q.capstone.R;
import com.example.c4q.capstone.userinterface.events.EventActivity;
import com.example.c4q.capstone.userinterface.events.VenueVoteSwipeActivity;
import com.example.c4q.capstone.userinterface.user.SettingsActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by @ajoxe on 3/23/18.
 * This class is a Navigation Drawer presenter to handle nav drawer actions
 */

public class NavDrawerPresenter {
    private DrawerLayout navDrawerLayout;
    private NavigationView navigationView;
    private Activity activity;
    private Context context;
    private Toolbar toolbar;
    private ActionBar actionbar;

    public NavDrawerPresenter(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public void setNavDrawerViews(DrawerLayout navDrawerLayout, NavigationView navigationView){
        this.navDrawerLayout = navDrawerLayout;
        this.navigationView = navigationView;

    }
    public void setToolbarViews(Toolbar toolbar, ActionBar actionBar){
        this.toolbar = toolbar;
        this.actionbar = actionBar;
    }
    /**
     * ajoxe : This method sets item clicks for the nav drawer
     *
     */
    public void setNavigationViewMethods(){
        if(activity == null){
            Log.d("NAV DRAWER PRESENTER", "activity is null");
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);

                        switch (menuItem.getItemId()) {
                            case R.id.notifications_menu_item:
                                //TODO start userProfile with notifications fragment loaded.
                                break;
                            case R.id.upcoming_events_menu_item:
                                //TODO start userProfile with upcomining events fragment loaded.
                                break;
                            case R.id.create_new_event:
                                Intent createEventIntent = new Intent(context, EventActivity.class);
                                activity.startActivity(createEventIntent);
                                //TODO start settings activity.
                                break;
                            case R.id.my_groups_menu_item:

                                //TODO start userProfile with groups fragment loaded.
                                break;
                            case R.id.settings_menu_item:
                                Intent settings = new Intent(context, SettingsActivity.class);
                                activity.startActivity(settings);
                                //TODO start settings activity.
                                break;
                            case R.id.venue_swipe_test:
                                Intent venueSwipeIntent = new Intent(context, VenueVoteSwipeActivity.class);
                                activity.startActivity(venueSwipeIntent);
                                //TODO start settings activity.
                                break;

                            case R.id.signout_menu_item:
                                AuthUI.getInstance()
                                        .signOut(context)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // ...
                                                Intent signOutIntent = new Intent(context, LoginActivity.class);
                                                signOutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                activity.startActivity(signOutIntent);
                                            }
                                        });
                                break;

                        }
                        // close drawer when item is tapped
                        navDrawerLayout.closeDrawers();

                        return true;
                    }
                });
    }
    /**
     * ajoxe : This method sets the nav header
     * **Incomplete** TODO (@ajoxe) - programatically set user name and initials
     */
    public void setNavHeader(String userInitals, String userName){
        View hView =  navigationView.getHeaderView(0);
        TextView nav_userInitials = (TextView)hView.findViewById(R.id.drawer_username_initials);
        TextView nav_userName = (TextView)hView.findViewById(R.id.drawer_username);
        nav_userInitials.setText(userInitals);
        nav_userName.setText(userName);
    }
    /**
     * ajoxe : This method sets up the action bar and takes in a title
     *
     */
    public void setActionbar(String title) {
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(title);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }
    /**
     * ajoxe : This method sets the nav drawer layout listener.
     * **Incomplete**
     */
    public void setNavDrawerLayout(){
        navDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }
}
