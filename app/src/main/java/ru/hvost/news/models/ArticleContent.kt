package ru.hvost.news.models

import android.net.Uri
import ru.hvost.news.utils.UniqueIdGenerator
import ru.hvost.news.utils.getUriForBackendImagePath
import java.lang.IllegalArgumentException
import java.util.regex.Pattern
import java.util.regex.Pattern.*

sealed class ArticleContent(
    val id: Long
)

class ArticleHeader(
    id:Long,
    val title: String,
    val imageUri: Uri,
    val domainTitle: String,
    val categoryTitle: String,
    val viewsCount: Int
) : ArticleContent(id)

class ArticleFooter(
    id:Long,
    val likesCount: Int
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
) : ArticleContent(id){

    override fun toString(): String {
        return text
    }

}

class HtmlImage(
    id: Long,
    val imageUrl: String
) : ArticleContent(id){

    private val pattern: Pattern = compile("src=\"[^\"=]*\"")
    val imageUri: Uri = getUriForBackendImagePath(extractImageUrl(imageUrl))

    private fun extractImageUrl(src: String): String? {
        val matcher = pattern.matcher(src)
        return if(matcher.find()){
            val path = matcher.group()
            path.substring(5, path.length-1)
        } else null
    }

    override fun toString(): String = imageUrl

}

class HtmlText(
    id: Long,
    val text: String
) : ArticleContent(id){

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

fun String.toHtmlContent() : List<ArticleContent> {
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
        }
    }
    if(builder.isNotBlank()) result.add(HtmlText(UniqueIdGenerator.nextId(), builder.toString()))
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
    if(textBeforeTag.isNotBlank()) result.add(HtmlText(UniqueIdGenerator.nextId(), textBeforeTag))
    result.add(
        when(pattern){
            HeaderPattern -> HtmlTitle(UniqueIdGenerator.nextId(), matcher.group())
            QuotePattern -> {
                val text = matcher.group()
                HtmlQuote(
                    UniqueIdGenerator.nextId(),
                    text.substring(12, text.length-13)
                )
            }
            ImagePattern -> HtmlImage(UniqueIdGenerator.nextId(), matcher.group())
            else -> throw IllegalArgumentException("Illegal pattern.")
        }
    )
    builder.clear()
}

fun Article.toArticleContent() : List<ArticleContent> {
    val result = this.content.toHtmlContent().toMutableList()
    result.add(0, ArticleHeader(
        id = UniqueIdGenerator.nextId(),
        title = title,
        imageUri = getUriForBackendImagePath(imageUrl),
        domainTitle = domainTitle,
        categoryTitle = categoryTitle,
        viewsCount = viewsCount
    ))
    result.add(ArticleFooter(
        id = UniqueIdGenerator.nextId(),
        likesCount = likesCount
    ))
    return result
}

const val testArticle = "<p>\n" +
        "\t Дрессировка декоративных крыс – это очень интересный и увлекательный процесс, который позволяет лучше изучить повадки питомца и раскрыть его таланты. Поверьте, при правильном подходе к занятиям вы не раз удивитесь острому уму вашего зверька, его способности быстро находить решения, а также активности и жизнерадостности.\n" +
        "</p>\n" +
        "<p>\n" +
        "\t Счастливые обладатели декоративных крыс по всему миру успешно обучили питомцев огромному количеству трюков, которым позавидует любая цирковая арена. В интернете вы без труда найдете сайты, целиком и полностью посвященные дрессировке крыс, с наглядным пошаговым руководством: текстовыми, графическими и видеоматериалами.\n" +
        "</p>\n" +
        "<p>\n" +
        "\t Преодоление сложных препятствий и лабиринтов, решение замысловатых задач, стойка на задних лапках, прыжки через обруч, катание предметов и умывание по команде – это лишь базовые трюки, которым можно обучить талантливого зверька. Наблюдайте за склонностями питомца, задействуйте свою фантазию – и, возможно, вдвоем вы изобретете совершенно новые удивительные трюки, которые удивят всех окружающих!\n" +
        "</p>\n" +
        "<p>\n" +
        " <img alt=\"Дрессировка крыс\" src=\"/upload/medialibrary/648/dressirovka_krys_v_domashnih_uslovijah.jpg\" title=\"Дрессировка крыс\"><br>\n" +
        "</p>\n" +
        "<p>\n" +
        "\t Многим владельцам нравится обустраивать для крыс кукольные домики с миниатюрными комнатами и мебелью и прививать питомцам «человеческое» поведение, например, отдых на кроватке. Все это достигается при помощи мощного стимула – лакомств, которые в данном случае можно положить под одеяло. Находя на игрушечной кроватке лакомства, крыса будет испытывать позитивные ассоциации и, соответственно, повторять свой трюк. И еще немного о человеческом поведении: многие крысы с удовольствием катаются в игрушечных радиоуправляемых машинах! Наблюдать за этим действом – одно загляденье.\n" +
        "</p>\n" +
        "<p>\n" +
        "\t Но чтобы дрессировка была успешной, необходимо соблюдать ряд правил. Во-первых, дрессировка ни в коем случае не должна стать навязчивой мыслью. Ее следует воспринимать как приятное времяпрепровождение с питомцем, как игру, увлекательную для обоих, а не как изнуряющие тренировки и сложную работу. Если крыса утомилась и отказывается от занятий, повторите попытку на следующей день. Конечно, любые методы наказания по отношению к грызуну неприемлемы. Ваш питомец не поймет, чего вы от него хотите, и только начнет вас бояться.\n" +
        "</p>\n" +
        "<h4>\n" +
        "Как дрессировать крысу? Основные принципы </h4>\n" +
        "<p>\n" +
        "\t Дрессировать крысу в домашних условиях помогут следующие базисы:\n" +
        "</p>\n" +
        "<p>\n" +
        "\t - приступайте к дрессировке в раннем возрасте, но после того, как крыса полностью адаптируется к новому дому;\n" +
        "</p>\n" +
        "<p>\n" +
        "\t - прежде чем перейти к дрессировке, наладьте контакт с грызуном. Занятия будут эффективны только если крыса будет вам доверять и не будет вас бояться. Она должна к вам привыкнуть;\n" +
        "</p>\n" +
        "<p>\n" +
        "\t - двигайтесь от простого к сложному. Сначала обучите питомца легким трюкам и постепенно повышайте их сложность;\n" +
        "</p>\n" +
        "<p>\n" +
        "\t - обязательно учитывайте природные склонности, уровень интеллекта и индивидуальные особенности грызуна. Не требуйте невозможного;\n" +
        "</p>\n" +
        "<p>\n" +
        "\t - постарайтесь придумывать трюки на основе естественных склонностей грызуна, не навязывайте не свойственного ему поведения;\n" +
        "</p>\n" +
        "<p>\n" +
        "\t - регулярно поощряйте крысу за выполненные трюки лакомством, закрепляйте навыки повторением команд.\n" +
        "</p>\n" +
        "<p>\n" +
        " <img alt=\"Дрессировка декоративных крыс\" src=\"/upload/medialibrary/786/dressirovka_dekorativnyh_krys.jpg\" title=\"Дрессировка декоративных крыс\"><br>\n" +
        "</p>\n" +
        "<p>\n" +
        "\t Вот и все, о чем нужно знать на первых этапах.\n" +
        "</p>\n" +
        "<p>\n" +
        "\t Мы будем рады, если вы поделитесь с нами успехами и расскажете (а еще лучше покажете на видео), что умеет ваш питомец. Присоединяйтесь к нам в социальных сетях!\n" +
        "</p>\n"