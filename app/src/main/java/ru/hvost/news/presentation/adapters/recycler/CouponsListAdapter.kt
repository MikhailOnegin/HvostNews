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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.R
import ru.hvost.news.databinding.ItemCouponBinding
import ru.hvost.news.models.Coupons
import ru.hvost.news.utils.startIntentActionView

class CouponsListAdapter(
        private val clickCoupon: ((String) -> Unit)? = null,
): ListAdapter<Coupons.Coupon, RecyclerView.ViewHolder>(DiffUtilCouponsCallback()) {

    private var coupons = arrayListOf<Coupons.Coupon>()

    override fun submitList(list: List<Coupons.Coupon>?) {
        list?.let {
            this.coupons = list.toCollection(ArrayList())
        }
        super.submitList(list)
    }

    fun filter(used: String) {
        if (used == "Все") {
            coupons = currentList.toCollection(ArrayList())
        }
        if (used == "Активные") {
            coupons = currentList.filter { !it.isUsed }.toCollection(ArrayList())
        }
        if (used == "Использованные") {
            coupons = currentList.filter { it.isUsed }.toCollection(ArrayList())
        }
        this.submitList(coupons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CouponViewHolder(ItemCouponBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = coupons[holder.adapterPosition]
        (holder as CouponViewHolder).bind(item)
    }

    override fun getItemCount(): Int {
        return coupons.size
    }

    class DiffUtilCouponsCallback : DiffUtil.ItemCallback<Coupons.Coupon>(){
        override fun areItemsTheSame(oldItem: Coupons.Coupon, newItem: Coupons.Coupon): Boolean {
            return oldItem.couponId == newItem.couponId
        }

        override fun areContentsTheSame(oldItem: Coupons.Coupon, newItem: Coupons.Coupon): Boolean {
            return  oldItem == newItem
        }
    }

    inner class CouponViewHolder(private val binding: ItemCouponBinding) : RecyclerView.ViewHolder(binding.root) {
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
                clickCoupon?.invoke(coupon.couponId)
            }
        }
    }
}