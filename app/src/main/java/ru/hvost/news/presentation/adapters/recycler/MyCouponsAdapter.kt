package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_coupon.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.models.Coupons

class MyCouponsAdapter:RecyclerView.Adapter<MyCouponsAdapter.ViewHolder>() {
    private var couponsFull = arrayListOf<Coupons.Coupon>()
    private var coupons = arrayListOf<Coupons.Coupon>()
    var clickCoupon:ClickCoupon? = null

    interface ClickCoupon {
        fun click(item:Coupons.Coupon)
    }
    fun setCoupons(coupons:List<Coupons.Coupon>){
        this.couponsFull = coupons.toCollection(ArrayList())
        this.coupons = coupons.toCollection(ArrayList())
        notifyDataSetChanged()
    }
    fun filter(used:String){
        if(used == "")
        coupons = couponsFull.filter { it.isUsed == used}.toCollection(ArrayList())
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

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val iVCoupon = itemView.imageView_coupon
        private val tVCouponTitle = itemView.textView_coupon_code
        private val tVCouponMaxDate = itemView.textView_coupon_date
        private val tVConst = itemView.coupon_constraint
        private val tVUsed = itemView.textView_coupon_status
        fun bind(coupon: Coupons.Coupon){
            Glide.with(itemView.context).load(baseUrl + coupon.imageUrl).placeholder(R.drawable.not_found).centerCrop()
                .into(iVCoupon)
            tVCouponTitle.text = coupon.title
            tVCouponMaxDate.text = coupon.expirationDate
            val status:String = coupon.isUsed
            if(status == "used") {
                tVUsed.background = itemView.resources.getDrawable(R.drawable.shape_red)
                tVUsed.text = "Использован"
            }
            else{
                tVUsed.background = itemView.resources.getDrawable(R.drawable.shape_green)
                tVUsed.text = "Активный"
            }
            tVConst.setOnClickListener {
                clickCoupon?.click(coupon)
            }
        }
    }
}