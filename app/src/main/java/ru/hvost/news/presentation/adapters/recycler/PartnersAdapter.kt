package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.ItemPartnerBinding
import ru.hvost.news.models.OfflineSeminars
import ru.hvost.news.utils.getDefaultShimmer
import ru.hvost.news.utils.startIntentActionView

class PartnersAdapter: RecyclerView.Adapter<PartnersAdapter.PartnerViewHolder>() {

    private var partners = arrayListOf<OfflineSeminars.Partner>()

    fun setPartners(partners: List<OfflineSeminars.Partner>){
        this.partners = partners.toCollection(ArrayList())
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnerViewHolder {
        val binding = ItemPartnerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return  PartnerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PartnerViewHolder, position: Int) {
        val partner = partners[position]
        holder.bind(partner)
    }

    override fun getItemCount(): Int {
        return partners.size
    }

    inner class PartnerViewHolder(private val binding: ItemPartnerBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(partner: OfflineSeminars.Partner){
            Glide.with(binding.root.context)
                    .load(baseUrl + partner.image)
                    .placeholder(getDefaultShimmer(itemView.context))
                    .error(R.drawable.ic_load_error)
                    .fitCenter()
                    .into(binding.imageViewPartner)
            binding.textViewTitle.text = partner.name.parseAsHtml()
            binding.cardView.setOnClickListener {
                startIntentActionView(
                        itemView.context,
                        partner.website
                )
            }
        }

    }
}