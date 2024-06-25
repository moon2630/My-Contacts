package com.example.mycontacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;


public class login extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewPagerAdapter adapter;

    ConstraintLayout constraintLayout;
    Animation top,T1,T2,T3,T4;

    TextView txt1,txt2,txt3,txt4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        txt1 = findViewById(R.id.text1);
        txt2 = findViewById(R.id.text2);
        txt3 = findViewById(R.id.text3);
        txt4 = findViewById(R.id.textView);


        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        constraintLayout = findViewById(R.id.constraintLayout);


        //For top animation
        top = AnimationUtils.loadAnimation(this,R.anim.top24);
        constraintLayout.setAnimation(top);

        T1 = AnimationUtils.loadAnimation(this,R.anim.top24);
        txt1.setAnimation(T1);
        T2 = AnimationUtils.loadAnimation(this,R.anim.top24);
        txt2.setAnimation(T2);
        T3 = AnimationUtils.loadAnimation(this,R.anim.top24);
        txt3.setAnimation(T3);
        T4 = AnimationUtils.loadAnimation(this,R.anim.middle);
        txt4.setAnimation(T4);






        tabLayout.addTab(tabLayout.newTab().setText("Signup"));
        tabLayout.addTab(tabLayout.newTab().setText("Login"));



        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new ViewPagerAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }
}


