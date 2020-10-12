package ru.hvost.news.presentation.adapters.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_coupon.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.response.CouponsResponse
import java.util.zip.Inflater

class MyCouponsAdapter:RecyclerView.Adapter<MyCouponsAdapter.CouponViewHolder>() {

    private var coupons = arrayListOf<CouponsResponse.Coupon>()

    fun setCoupons(coupons:List<CouponsResponse.Coupon>){
        this.coupons = coupons.toCollection(ArrayList())
        notifyDataSetChanged()
    }
    fun addCoupons(coupons:List<CouponsResponse.Coupon>){
        this.coupons.addAll(coupons)
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
        val item = coupons[holder.adapterPosition]
        holder.bind(item)
    }

    inner class CouponViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val iVCoupon = itemView.imageView_coupon
        private val tVCouponCode = itemView.textView_coupon_code
        private val tVCouponMaxDate = itemView.textView_coupon_date

        fun bind(item: CouponsResponse.Coupon){
            item.imageUrl?.run {
                // add placeHolder()
                Glide.with(itemView.context).load(this).centerCrop()
                    .into(iVCoupon)
            }
            item.qrCode?.run {
                tVCouponCode.text = this
            }

        }
    }
}