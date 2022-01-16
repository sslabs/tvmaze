package org.sslabs.tvmaze.ui.showdetails

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.sslabs.tvmaze.data.model.Episode

class SeasonPageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val seasonList = mutableListOf<Int>()
    private val episodeList = mutableListOf<Episode>()

    override fun getItemCount() = seasonList.size

    override fun createFragment(position: Int): Fragment {
        val seasonEpisodes = episodeList.filter { it.season == seasonList[position] }
        return SeasonEpisodesFragment.newInstance(seasonEpisodes)
    }

    override fun getItemId(position: Int): Long = seasonList[position].toLong()

    override fun containsItem(itemId: Long): Boolean = seasonList.any { it.toLong() == itemId }

    fun submitData(data: List<Episode>) {
        episodeList.apply {
            clear()
            addAll(data)

        }
        seasonList.apply {
            clear()
            addAll(episodeList.map { it.season!! }.distinct())
        }
        notifyDataSetChanged()
    }

    fun seasonAtPosition(position: Int) = seasonList[position]
}
