package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.data.api.APIService
import ru.hvost.news.utils.UniqueIdGenerator
import ru.hvost.news.utils.getUriForBackendImagePath
import java.lang.IllegalArgumentException
import java.util.regex.Pattern
import java.util.regex.Pattern.*

sealed class ArticleContent(
    val id: Long
)

class ArticleHeader(
    id: Long,
    val title: String,
    val imageUri: Uri,
    val domainTitle: String,
    val categoryTitle: String,
    val publicationDate: String,
    val postTypeId: String,
    val postTypeName: String,
    val viewsCount: Int
) : ArticleContent(id)

class ArticleFooter(
    id: Long,
    var likesCount: Int,
    val shareLink: String
) : ArticleContent(id)

class HtmlTitle(
    id: Long,
    val text: String
) : ArticleContent(id) {

    override fun toString(): String {
        return text
    }

}

class HtmlQuote(
    id: Long,
    val text: String
) : ArticleContent(id) {

    override fun toString(): String {
        return text
    }

}

class HtmlMarker(
    id: Long,
    val text: String
) : ArticleContent(id) {

    override fun toString(): String {
        return text
    }

}

class HtmlImage(
    id: Long,
    val imageUrl: String
) : ArticleContent(id) {

    private val pattern: Pattern = compile("src=\"[^\"=]*\"")
    val imageUri: Uri = getUriForBackendImagePath(extractImageUrl(imageUrl))

    private fun extractImageUrl(src: String): String? {
        val matcher = pattern.matcher(src)
        return if (matcher.find()) {
            val path = matcher.group()
            path.substring(5, path.length - 1)
        } else null
    }

    override fun toString(): String = imageUrl

}

class HtmlText(
    id: Long,
    val text: String
) : ArticleContent(id) {

    override fun toString(): String {
        return text
    }

}

val TextWithHeaderPattern: Pattern = compile(".*<h4>.*</h4>", DOTALL)
val TextWithQuotePattern: Pattern = compile(".*<blockquote>.*</blockquote>", DOTALL)
val TextWithImagePattern: Pattern = compile(".*<img.*>", DOTALL)
val TextWithMarkerPattern: Pattern = compile(".*<li>.*</li>", DOTALL)
val HeaderPattern: Pattern = compile("<h4>.*</h4>", DOTALL)
val QuotePattern: Pattern = compile("<blockquote>.*</blockquote>", DOTALL)
val ImagePattern: Pattern = compile("<img.*>", DOTALL)
val MarkerPattern: Pattern = compile("<li>.*</li>", DOTALL)

fun String.toHtmlContent(): List<ArticleContent> {
    val result = mutableListOf<ArticleContent>()
    val builder = StringBuilder()
    this.forEach {
        builder.append(it)
        when {
            TextWithHeaderPattern.matcher(builder.toString()).matches() -> {
                processString(HeaderPattern, builder, result)
            }
            TextWithQuotePattern.matcher(builder.toString()).matches() -> {
                processString(QuotePattern, builder, result)
            }
            TextWithImagePattern.matcher(builder.toString()).matches() -> {
                processString(ImagePattern, builder, result)
            }
            TextWithMarkerPattern.matcher(builder.toString()).matches() -> {
                processString(MarkerPattern, builder, result)
            }
        }
    }
    if (builder.isNotBlank()) result.add(HtmlText(UniqueIdGenerator.nextId(), builder.toString()))
    return result
}

fun processString(
    pattern: Pattern,
    builder: StringBuilder,
    result: MutableList<ArticleContent>
) {
    val matcher = pattern.matcher(builder.toString())
    matcher.find()
    val textBeforeTag = builder.replaceFirst(pattern.toRegex(), "")
    if (textBeforeTag.isNotBlank()) result.add(HtmlText(UniqueIdGenerator.nextId(), textBeforeTag))
    result.add(
        when (pattern) {
            HeaderPattern -> HtmlTitle(UniqueIdGenerator.nextId(), matcher.group())
            QuotePattern -> {
                val text = matcher.group()
                HtmlQuote(
                    UniqueIdGenerator.nextId(),
                    text.substring(12, text.length - 13)
                )
            }
            ImagePattern -> HtmlImage(UniqueIdGenerator.nextId(), matcher.group())
            MarkerPattern -> {
                val text = matcher.group()
                HtmlMarker(
                    UniqueIdGenerator.nextId(),
                    text.substring(4, text.length - 5)
                )
            }
            else -> throw IllegalArgumentException("Illegal pattern.")
        }
    )
    builder.clear()
}

fun Article.toArticleContent(): List<ArticleContent> {
    val result = this.content.toHtmlContent().toMutableList()
    result.add(
        0, ArticleHeader(
            id = UniqueIdGenerator.nextId(),
            title = title,
            imageUri = getUriForBackendImagePath(imageUrl),
            domainTitle = domainTitle,
            categoryTitle = categoryTitle,
            publicationDate = publicationDate,
            postTypeId = postTypeId,
            postTypeName = postTypeName,
            viewsCount = viewsCount
        )
    )
    result.add(
        ArticleFooter(
            id = UniqueIdGenerator.nextId(),
            likesCount = likesCount,
            shareLink = APIService.baseUrl + articleUrl
        )
    )
    return result
}