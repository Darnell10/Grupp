package com.example.c4q.capstone.userinterface.events.createevent;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ToxicBakery.viewpager.transforms.FlipVerticalTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.example.c4q.capstone.R;

public class CreateEventActivity extends AppCompatActivity {
    ViewPager vpPager;
    FragmentPagerAdapter adapterViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new CreateEventPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setPageTransformer(true, new FlipVerticalTransformer());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.create_event_tab_layout);
        tabLayout.setupWithViewPager(vpPager);

    }
}