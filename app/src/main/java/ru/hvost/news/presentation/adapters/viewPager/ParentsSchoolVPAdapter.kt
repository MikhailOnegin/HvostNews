package ru.hvost.news.presentation.adapters.viewPager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.hvost.news.presentation.fragments.school.SeminarsFragment
import ru.hvost.news.presentation.fragments.school.SeminarsInYourCityFragment

class ParentsSchoolVPAdapter(fm:FragmentManager, lifecycle:Lifecycle):
FragmentStateAdapter(fm, lifecycle)
{

    var fragments:ArrayList<Fragment> = arrayListOf(
        SeminarsFragment(),
        SeminarsInYourCityFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}