package ru.hvost.news.presentation.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.parseAsHtml
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.*
import ru.hvost.news.models.*
import ru.hvost.news.utils.moneyFormat
import java.lang.IllegalArgumentException

class ArticleContentAdapter :
    ListAdapter<ArticleContent, RecyclerView.ViewHolder>(ArticleContentDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_HEADER -> ArticleHeaderVH.getViewHolder(parent)
            TYPE_FOOTER -> ArticleFooterVH.getViewHolder(parent)
            TYPE_TITLE -> ArticleTitleVH.getViewHolder(parent)
            TYPE_QUOTE -> ArticleQuoteVH.getViewHolder(parent)
            TYPE_IMAGE -> ArticleImageVH.getViewHolder(parent)
            TYPE_TEXT -> ArticleTextVH.getViewHolder(parent)
            else -> throw IllegalArgumentException("Wrong article view type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is ArticleHeader -> (holder as ArticleHeaderVH).bind(item)
            is ArticleFooter -> (holder as ArticleFooterVH).bind(item)
            is HtmlTitle -> (holder as ArticleTitleVH).bind(item)
            is HtmlQuote -> (holder as ArticleQuoteVH).bind(item)
            is HtmlImage -> (holder as ArticleImageVH).bind(item)
            is HtmlText -> (holder as ArticleTextVH).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is ArticleHeader -> TYPE_HEADER
            is ArticleFooter -> TYPE_FOOTER
            is HtmlTitle -> TYPE_TITLE
            is HtmlQuote -> TYPE_QUOTE
            is HtmlImage -> TYPE_IMAGE
            is HtmlText -> TYPE_TEXT
        }
    }

    class ArticleHeaderVH(
        private val binding: RvArticleHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val stub = App.getInstance().getString(R.string.stub)

        fun bind(header: ArticleHeader) {
            binding.apply {
                Glide.with(binding.root).load(header.imageUri).into(image)
                title.text = header.title
                domain.text = header.domainTitle
                category.text = header.categoryTitle
                views.text = moneyFormat.format(header.viewsCount)
                date.text = stub
                type.text = stub
            }
            binding.image.doOnLayout {
                binding.image.layoutParams = ConstraintLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    it.width
                ).apply {
                    topToBottom = R.id.dateIcon
                    setMargins(
                        0,
                        App.getInstance().resources.getDimension(R.dimen.normalMargin).toInt(),
                        0,
                        0
                    )
                }
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup) : ArticleHeaderVH {
                val binding = RvArticleHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleHeaderVH(binding)
            }

        }

    }

    class ArticleFooterVH(
        private val binding: RvArticleFooterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(footer: ArticleFooter) {
            binding.apply {
                buttonLike.text = moneyFormat.format(footer.likesCount)
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup) : ArticleFooterVH {
                val binding = RvArticleFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleFooterVH(binding)
            }

        }

    }

    class ArticleTitleVH(
        private val binding: RvArticleTitleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(title: HtmlTitle) {
            binding.text.text = title.text.parseAsHtml()
        }

        companion object {

            fun getViewHolder(parent: ViewGroup) : ArticleTitleVH {
                val binding = RvArticleTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleTitleVH(binding)
            }

        }

    }

    class ArticleQuoteVH(
        private val binding: RvArticleQuoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quote: HtmlQuote) {
            binding.text.text = quote.text.parseAsHtml()
        }

        companion object {

            fun getViewHolder(parent: ViewGroup) : ArticleQuoteVH {
                val binding = RvArticleQuoteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleQuoteVH(binding)
            }

        }

    }

    class ArticleImageVH(
        private val binding: RvArticleImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val aspectRatio = 0.615f

        fun bind(image: HtmlImage) {
            Glide.with(binding.root).load(image.imageUri).into(binding.image)
            binding.image.doOnLayout {
                binding.image.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (it.width * aspectRatio).toInt()
                )
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup) : ArticleImageVH {
                val binding = RvArticleImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleImageVH(binding)
            }

        }

    }

    class ArticleTextVH(
        private val binding: RvArticleTextBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: HtmlText) {
            binding.text.text = text.text.parseAsHtml()
        }

        companion object {

            fun getViewHolder(parent: ViewGroup) : ArticleTextVH {
                val binding = RvArticleTextBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleTextVH(binding)
            }

        }

    }

    class ArticleContentDiffUtilCallback : DiffUtil.ItemCallback<ArticleContent>() {

        override fun areItemsTheSame(oldItem: ArticleContent, newItem: ArticleContent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ArticleContent, newItem: ArticleContent): Boolean {
            return false
        }

    }

    companion object {

        const val TYPE_HEADER = 1
        const val TYPE_FOOTER = 2
        const val TYPE_TITLE = 3
        const val TYPE_QUOTE = 4
        const val TYPE_IMAGE = 5
        const val TYPE_TEXT = 6

    }

}