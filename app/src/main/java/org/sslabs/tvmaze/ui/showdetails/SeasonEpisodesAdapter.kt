package org.sslabs.tvmaze.ui.showdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.sslabs.tvmaze.R
import org.sslabs.tvmaze.data.model.Episode
import org.sslabs.tvmaze.databinding.ItemSeasonEpisodeBinding

class SeasonEpisodesAdapter(
    private val itemInteraction: SeasonEpisodeViewHolder.Interaction
) : RecyclerView.Adapter<SeasonEpisodeViewHolder>() {

    private val seasonEpisodeList = mutableListOf<Episode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonEpisodeViewHolder =
        SeasonEpisodeViewHolder(
            ItemSeasonEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            itemInteraction
        )

    override fun onBindViewHolder(holder: SeasonEpisodeViewHolder, position: Int) {
        holder.bind(seasonEpisodeList[position])
    }

    override fun getItemCount(): Int = seasonEpisodeList.size

    fun submitData(data: List<Episode>) {
        seasonEpisodeList.apply {
            clear()
            addAll(data)
            notifyDataSetChanged()
        }
    }
}

class SeasonEpisodeViewHolder(
    private val itemBinding: ItemSeasonEpisodeBinding,
    private val interaction: Interaction
) : RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(episode: Episode) {
        itemBinding.episodeTitle.apply {
            text = itemBinding.root.context.getString(
                R.string.season_episode_name,
                episode.number,
                episode.name
            )
            setOnClickListener {
                interaction.onEpisodeSelected(adapterPosition, episode)
            }
        }
    }

    interface Interaction {
        fun onEpisodeSelected(position: Int, item: Episode)
    }
}
