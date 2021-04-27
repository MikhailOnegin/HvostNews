package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import ru.hvost.news.databinding.ItemVideoPastSeminarBinding
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.utils.getDefaultShimmer
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
        fun bind(video: OfflineSeminars.Video) {
            binding.textViewTitle.text = video.title.parseAsHtml()
            Glide.with(itemView.context)
                    .load(APIService.baseUrl + video.imageVideoUrl)
                    .placeholder(getDefaultShimmer(itemView.context))
                    .error(R.drawable.empty_image)
                    .into(binding.imageViewVideo)
            binding.root
            binding.constraintVideo.setOnClickListener {
                startIntentActionView(itemView.context, video.videoUrl)
            }
            if (adapterPosition== videos.lastIndex){
                val padding = itemView.resources.getDimension(R.dimen.largeMargin).toInt()
                binding.rootConstraint.setPadding(0,0,padding,0)
            }
        }
    }
}