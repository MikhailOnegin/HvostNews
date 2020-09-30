package ru.hvost.news.presentation.adapters.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.data.api.response.CouponsResponse
import java.util.zip.Inflater

class MyCouponsAdapter:RecyclerView.Adapter<MyCouponsAdapter.CouponViewHolder>() {

    private var coupons = arrayListOf<CouponsResponse>()

    fun setCoupons(coupons:List<CouponsResponse>){
        this.coupons = coupons.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coupon, parent, false)
        return CouponViewHolder(view)
    }

    override fun getItemCount(): Int {
        return coupons.size
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind()
    }

    inner class CouponViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(){

        }
    }
}