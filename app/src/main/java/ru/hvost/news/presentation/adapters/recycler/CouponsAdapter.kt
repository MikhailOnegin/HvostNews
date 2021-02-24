package ru.hvost.news.presentation.adapters.recycler

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.databinding.ItemCouponBinding
import ru.hvost.news.models.Coupons
import ru.hvost.news.utils.startIntentActionView

class CouponsAdapter : RecyclerView.Adapter<CouponsAdapter.ViewHolder>() {
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
        return ViewHolder(ItemCouponBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ))
    }

    override fun getItemCount(): Int {
        return coupons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = coupons[holder.adapterPosition]
        holder.bind(item)
    }

    inner class ViewHolder(private val binding:ItemCouponBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(coupon: Coupons.Coupon) {
            binding.textViewCouponTitle.text = coupon.title.parseAsHtml()
            binding.textViewCouponDate.text = coupon.expirationDate
            if (coupon.isUsed) {
                binding.textViewCouponStatus.text = itemView.context.getString(R.string.used)
                binding.textViewCouponStatus.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_coupon_staus_false)
            } else {
                binding.textViewCouponStatus.background = ContextCompat.getDrawable(itemView.context, R.drawable.background_coupon_status_true)
                binding.textViewCouponStatus.text = itemView.context.getString(R.string.active)
            }
            if (coupon.isOnlineStore) {
                val websiteWithoutProtocol = coupon.website.substringAfterLast('/')
                val spannable = SpannableString(websiteWithoutProtocol)
                val colorSpan =
                        ForegroundColorSpan(ContextCompat.getColor(itemView.context, R.color.colorPrimary))
                val clickableSpan1 = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        startIntentActionView(itemView.context, coupon.website)
                    }
                }
                spannable.setSpan(clickableSpan1, 0, websiteWithoutProtocol.lastIndex + 1 , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannable.setSpan(colorSpan, 0, websiteWithoutProtocol.lastIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                binding.textViewAddress.text = spannable
                binding.textViewAddress.movementMethod = LinkMovementMethod.getInstance()
            }
            else{
                binding.textViewAddress.text = coupon.address
            }
            binding.rootConstraint.setOnClickListener {
                clickCoupon?.click(coupon)
            }
        }
    }
}