package ru.hvost.news.models

import ru.hvost.news.utils.UniqueIdGenerator
import java.lang.IllegalArgumentException
import java.util.regex.Pattern
import java.util.regex.Pattern.*

sealed class HtmlContent(
    val id: Long
)

class HtmlHeader(
    id: Long,
    val text: String
) : HtmlContent(id) {

    override fun toString(): String {
        return text
    }

}

class HtmlQuote(
    id: Long,
    val text: String
) : HtmlContent(id){

    override fun toString(): String {
        return text
    }

}

class HtmlImage(
    id: Long,
    val imageUri: String
) : HtmlContent(id){

    override fun toString(): String {
        return imageUri
    }

}

class HtmlText(
    id: Long,
    val text: String
) : HtmlContent(id){

    override fun toString(): String {
        return text
    }

}

val TextWithHeaderPattern: Pattern = compile(".*<h4>.*</h4>", DOTALL)
val TextWithQuotePattern: Pattern = compile(".*<blockquote>.*</blockquote>", DOTALL)
val TextWithImagePattern: Pattern = compile(".*<img.*>", DOTALL)
val HeaderPattern: Pattern = compile("<h4>.*</h4>", DOTALL)
val QuotePattern: Pattern = compile("<blockquote>.*</blockquote>", DOTALL)
val ImagePattern: Pattern = compile("<img.*>", DOTALL)

fun String.toHtmlContent() : List<HtmlContent> {
    val result = mutableListOf<HtmlContent>()
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
        }
    }
    if(builder.isNotBlank()) result.add(HtmlText(UniqueIdGenerator.nextId(), builder.toString()))
    return result
}

fun processString(
    pattern: Pattern,
    builder: StringBuilder,
    result: MutableList<HtmlContent>
) {
    val matcher = pattern.matcher(builder.toString())
    matcher.find()
    val textBeforeTag = builder.replaceFirst(pattern.toRegex(), "")
    if(textBeforeTag.isNotBlank()) result.add(HtmlText(UniqueIdGenerator.nextId(), textBeforeTag))
    result.add(
        when(pattern){
            HeaderPattern -> HtmlHeader(UniqueIdGenerator.nextId(), matcher.group())
            QuotePattern -> HtmlQuote(UniqueIdGenerator.nextId(), matcher.group())
            ImagePattern -> HtmlImage(UniqueIdGenerator.nextId(), matcher.group())
            else -> throw IllegalArgumentException("Illegal pattern.")
        }
    )
    builder.clear()
}