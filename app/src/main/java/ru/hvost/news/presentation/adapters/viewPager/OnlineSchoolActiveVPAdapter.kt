package ru.hvost.news.presentation.adapters.viewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.hvost.news.presentation.fragments.school.active.CourseInfoFragment
import ru.hvost.news.presentation.fragments.school.active.CourseMaterialsFragment
import ru.hvost.news.presentation.fragments.school.parents.LessonsInYourCityFragment

class OnlineSchoolActiveVPAdapter (fm: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(fm, lifecycle)
{

    var fragments:ArrayList<Fragment> = arrayListOf(
        CourseInfoFragment(),
        CourseMaterialsFragment()
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}