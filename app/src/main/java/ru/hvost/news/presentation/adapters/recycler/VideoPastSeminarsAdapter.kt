package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_video_past_seminar.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.ItemVideoPastSeminarBinding
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.utils.startIntentActionView

class VideoPastSeminarsAdapter: RecyclerView.Adapter<VideoPastSeminarsAdapter.VideoViewHolder> () {

    private var videos = arrayListOf<OfflineSeminars.Video>()

    fun setVideos(videos:List<OfflineSeminars.Video>){
        this.videos = videos.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return  VideoViewHolder(ItemVideoPastSeminarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videos[position]
        holder.bind(video)
    }

    override fun getItemCount(): Int {
        return  videos.size
    }

    inner class VideoViewHolder(private val binding:ItemVideoPastSeminarBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(video: OfflineSeminars.Video){
            binding.textViewTitle.text = video.title.parseAsHtml()
            Glide.with(itemView.context).load(APIService.baseUrl + video.imageVideoUrl)
                .placeholder(R.drawable.not_found).centerCrop()
                .into(binding.imageViewVideo)
            binding.imageViewVideo.setOnClickListener {
                startIntentActionView(itemView.context, video.videoUrl)
            }
            binding.imageViewPlay.setOnClickListener {
                startIntentActionView(itemView.context, video.videoUrl)
            }
        }
    }
}