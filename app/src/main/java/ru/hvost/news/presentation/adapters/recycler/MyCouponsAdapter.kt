package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_coupon.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.models.Coupons

class MyCouponsAdapter : RecyclerView.Adapter<MyCouponsAdapter.ViewHolder>() {
    private var couponsFull = arrayListOf<Coupons.Coupon>()
    private var coupons = arrayListOf<Coupons.Coupon>()
    var clickCoupon: ClickCoupon? = null

    interface ClickCoupon {
        fun click(item: Coupons.Coupon)
    }

    fun setCoupons(coupons: List<Coupons.Coupon>) {
        this.couponsFull = coupons.toCollection(ArrayList())
        this.coupons = coupons.toCollection(ArrayList())
        notifyDataSetChanged()
    }

    fun filter(used: String) {
        if (used == "Все") {
            coupons = couponsFull
        }
        if (used == "Активные") {
            coupons = couponsFull.filter { !it.isUsed }.toCollection(ArrayList())
        }
        if (used == "Использованные") {
            coupons = couponsFull.filter { it.isUsed }.toCollection(ArrayList())
        }
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coupon, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return coupons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = coupons[holder.adapterPosition]
        holder.bind(item)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clRoot = itemView.root_constraint
        private val tVCouponTitle = itemView.textView_coupon_title
        private val tVCouponMaxDate = itemView.textView_coupon_date
        private val tVUsed = itemView.textView_coupon_status
        private val tVAddress = itemView.textView_address

        fun bind(coupon: Coupons.Coupon) {
            tVCouponTitle.text = coupon.title.parseAsHtml()
            tVCouponMaxDate.text = coupon.expirationDate
            if (coupon.isUsed) {
                tVUsed.text = itemView.context.getString(R.string.used)
                tVUsed.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_coupon_staus_false)
            } else {
                tVUsed.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_coupon_status_true)
                tVUsed.text = itemView.context.getString(R.string.active)
            }
            if (coupon.address.isNotBlank()) {
                tVAddress.text = coupon.address
            } else {
                tVAddress.visibility = View.GONE
            }
            clRoot.setOnClickListener {
                clickCoupon?.click(coupon)
            }
        }
    }
}