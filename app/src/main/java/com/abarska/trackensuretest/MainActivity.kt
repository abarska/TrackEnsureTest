package com.abarska.trackensuretest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        val adapter = TabFragmentPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
    }
}
