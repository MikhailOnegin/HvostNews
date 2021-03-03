package ru.hvost.news.presentation.adapters.recycler

import android.content.res.ColorStateList
import android.graphics.Color
import android.text.method.LinkMovementMethod
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
import com.google.android.material.button.MaterialButton
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.*
import ru.hvost.news.models.*
import ru.hvost.news.presentation.fragments.articles.ArticleViewModel
import ru.hvost.news.utils.getDefaultShimmer
import ru.hvost.news.utils.moneyFormat
import java.lang.IllegalArgumentException

class ArticleContentAdapter(
    private val articleVM: ArticleViewModel,
    private val mainVM: MainViewModel,
    var isLiked: Boolean,
    var itemId: String?,
    private val setLiked: (String, Boolean) -> Unit
) : ListAdapter<ArticleContent, RecyclerView.ViewHolder>(ArticleContentDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> ArticleHeaderVH.getViewHolder(parent)
            TYPE_FOOTER -> ArticleFooterVH.getViewHolder(
                parent,
                articleVM,
                mainVM,
                this,
                itemId,
                setLiked
            )
            TYPE_TITLE -> ArticleTitleVH.getViewHolder(parent)
            TYPE_QUOTE -> ArticleQuoteVH.getViewHolder(parent)
            TYPE_IMAGE -> ArticleImageVH.getViewHolder(parent)
            TYPE_TEXT -> ArticleTextVH.getViewHolder(parent)
            TYPE_MARKER -> ArticleMarkerVH.getViewHolder(parent)
            else -> throw IllegalArgumentException("Wrong article view type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ArticleHeader -> (holder as ArticleHeaderVH).bind(item)
            is ArticleFooter -> (holder as ArticleFooterVH).bind(item)
            is HtmlTitle -> (holder as ArticleTitleVH).bind(item)
            is HtmlQuote -> (holder as ArticleQuoteVH).bind(item)
            is HtmlImage -> (holder as ArticleImageVH).bind(item)
            is HtmlText -> (holder as ArticleTextVH).bind(item)
            is HtmlMarker -> (holder as ArticleMarkerVH).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ArticleHeader -> TYPE_HEADER
            is ArticleFooter -> TYPE_FOOTER
            is HtmlTitle -> TYPE_TITLE
            is HtmlQuote -> TYPE_QUOTE
            is HtmlImage -> TYPE_IMAGE
            is HtmlText -> TYPE_TEXT
            is HtmlMarker -> TYPE_MARKER
        }
    }

    override fun onCurrentListChanged(
        previousList: MutableList<ArticleContent>,
        currentList: MutableList<ArticleContent>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        articleVM.sendRecyclerViewReadyEvent()
    }

    class ArticleHeaderVH(
        private val binding: RvArticleHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val article = App.getInstance().getString(R.string.defaultArticleTitle)

        fun bind(header: ArticleHeader) {
            binding.apply {
                Glide.with(binding.root)
                        .load(header.imageUri)
                        .placeholder(getDefaultShimmer(itemView.context))
                        .error(R.drawable.ic_load_error)
                        .into(image)
                title.text = header.title
                category.text = header.categoryTitle
                views.text = moneyFormat.format(header.viewsCount)
                date.text = header.publicationDate
                type.text = if (header.postTypeName.isEmpty()) article else header.postTypeName
                typeIcon.apply {
                    when (header.postTypeId) {
                        ArticleAdapter.NEWS_ID -> setImageResource(R.drawable.ic_news_wob)
                        ArticleAdapter.ASK_ID -> setImageResource(R.drawable.ic_ask_wob)
                        ArticleAdapter.VIDEO_ID -> setImageResource(R.drawable.ic_video_wob)
                        else -> setImageResource(R.drawable.ic_article)
                    }
                }
            }
            binding.image.doOnLayout {
                binding.image.layoutParams = ConstraintLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (it.width / 1.5f).toInt()
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

            fun getViewHolder(parent: ViewGroup): ArticleHeaderVH {
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
        private val binding: RvArticleFooterBinding,
        private val articleVM: ArticleViewModel,
        private val adapter: ArticleContentAdapter,
        private val itemId: String?,
        private val setLiked: (String, Boolean) -> Unit,
        private val mainVM: MainViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(footer: ArticleFooter) {
            binding.apply {
                buttonLike.text = moneyFormat.format(footer.likesCount)
                buttonShare.setOnClickListener {
                    articleVM.sendShareArticleEvent(footer.shareLink)
                }
                if (adapter.isLiked) {
                    (buttonLike as MaterialButton).setIconResource(R.drawable.ic_like_checked)
                    buttonLike.iconTint = ColorStateList.valueOf(Color.parseColor("#FF0000"))
                }
                buttonLike.setOnClickListener {
                    adapter.isLiked = !adapter.isLiked
                    if (!itemId.isNullOrEmpty()) setLiked(itemId, adapter.isLiked)
                    if (adapter.isLiked) {
                        changeCount(footer, true)
                        (buttonLike as MaterialButton).apply {
                            setIconResource(R.drawable.ic_like_checked)
                            iconTint = ColorStateList.valueOf(Color.parseColor("#FF0000"))
                            text = moneyFormat.format(footer.likesCount)
                        }
                    } else {
                        changeCount(footer, false)
                        (buttonLike as MaterialButton).apply {
                            setIconResource(R.drawable.ic_like_unchecked)
                            iconTint = ColorStateList.valueOf(Color.parseColor("#7D82AF"))
                            text = moneyFormat.format(footer.likesCount)
                        }
                    }
                }
            }
        }

        private fun changeCount(footer: ArticleFooter, plus: Boolean) {
            mainVM.articles.value?.firstOrNull { it.articleId == itemId }?.apply {
                likesCount = likesCount.plus(if (plus) 1 else -1)
                isLiked = plus
            }
            mainVM.allArticles.value?.firstOrNull { it.articleId == itemId }?.apply {
                likesCount = likesCount.plus(if (plus) 1 else -1)
                isLiked = plus
            }
            mainVM.likedArticleList.value = mainVM.articles.value?.toMutableList()
            mainVM.likedAllArticles.value = mainVM.allArticles.value?.toMutableList()
            footer.likesCount = footer.likesCount.plus(if (plus) 1 else -1)
        }

        companion object {

            fun getViewHolder(
                parent: ViewGroup,
                articleVM: ArticleViewModel,
                mainVM: MainViewModel,
                adapter: ArticleContentAdapter,
                itemId: String?,
                setLiked: (String, Boolean) -> Unit
            ): ArticleFooterVH {
                val binding = RvArticleFooterBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleFooterVH(binding, articleVM, adapter, itemId, setLiked, mainVM)
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

            fun getViewHolder(parent: ViewGroup): ArticleTitleVH {
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
            binding.text.movementMethod = LinkMovementMethod()
            binding.text.text = quote.text.parseAsHtml().trim()
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): ArticleQuoteVH {
                val binding = RvArticleQuoteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleQuoteVH(binding)
            }

        }

    }

    class ArticleMarkerVH(
        private val binding: RvArticleMarkerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(quote: HtmlMarker) {
            binding.text.movementMethod = LinkMovementMethod()
            binding.text.text = quote.text.parseAsHtml().trim()
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): ArticleMarkerVH {
                val binding = RvArticleMarkerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ArticleMarkerVH(binding)
            }

        }

    }

    class ArticleImageVH(
        private val binding: RvArticleImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val aspectRatio = 0.615f

        fun bind(image: HtmlImage) {
            Glide
                .with(binding.root)
                .load(image.imageUri)
                .placeholder(R.drawable.loader_anim)
                .error(R.drawable.ic_load_error)
                .into(binding.image)
            binding.image.doOnLayout {
                binding.image.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (it.width * aspectRatio).toInt()
                )
            }
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): ArticleImageVH {
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
            binding.text.movementMethod = LinkMovementMethod()
            binding.text.text = text.text.parseAsHtml()
        }

        companion object {

            fun getViewHolder(parent: ViewGroup): ArticleTextVH {
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
        const val TYPE_MARKER = 7

    }

}