package id.co.dif.base_project.presentation.adapter

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseViewModel

class TitledViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    fun clear() {
        mFragmentList.clear()
        mFragmentTitleList.clear()
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun add(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
        notifyDataSetChanged()
    }

    fun replaceAll(fragments: ArrayList<BaseFragment<out BaseViewModel, out ViewDataBinding>>, titles: List<String>) {
        mFragmentList.clear()
        mFragmentTitleList.clear()
        mFragmentList.addAll(fragments)
        mFragmentTitleList.addAll(titles)
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

}