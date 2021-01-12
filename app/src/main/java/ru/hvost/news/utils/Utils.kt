package ru.hvost.news.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.text.InputFilter
import android.text.Spanned
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService
import java.lang.Exception
import java.lang.StringBuilder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

fun createSnackbar(
    anchorView: View,
    text: String?,
    buttonText: String? = null,
    onButtonClicked: (() -> Unit)? = null
): Snackbar {
    val snackbar = Snackbar.make(
        anchorView,
        text.toString(),
        Snackbar.LENGTH_SHORT
    )
    snackbar.setBackgroundTint(ContextCompat.getColor(App.getInstance(), R.color.colorAccent))
    snackbar.setTextColor(ContextCompat.getColor(App.getInstance(), android.R.color.white))
    snackbar.setActionTextColor(ContextCompat.getColor(App.getInstance(), android.R.color.white))
    val textView =
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.maxLines = 10
    if (buttonText != null) {
        snackbar.setAction(buttonText) {
            onButtonClicked?.invoke() ?: snackbar.dismiss()
        }
        snackbar.duration = Snackbar.LENGTH_INDEFINITE
    }
    return snackbar
}

fun isPhoneFieldIncorrect(field: TextInputEditText): Boolean {
    if (!PhoneInputFilter.phoneRegex.matcher(field.text.toString()).matches()) {
        field.error = App.getInstance().getString(R.string.incorrectPhone)
        field.requestFocus()
        return true
    }
    return false
}

fun isEmailFieldIncorrect(field: TextInputEditText): Boolean {
    if (!Patterns.EMAIL_ADDRESS.matcher(field.text.toString()).matches()) {
        field.error = App.getInstance().getString(R.string.incorrectEmail)
        field.requestFocus()
        return true
    }
    return false
}

fun hasBlankField(vararg fields: TextInputEditText): Boolean {
    for (field in fields) {
        if (field.text.isNullOrBlank()) {
            field.error = App.getInstance().getString(R.string.requiredField)
            field.requestFocus()
            return true
        }
    }
    return false
}

fun hasTooLongField(vararg fields: TextInputEditText): Boolean {
    val res = App.getInstance().resources
    for (field in fields) {
        if (field.text?.length ?: 0 > res.getInteger(R.integer.editTextFieldMaxSize)) {
            field.error = App.getInstance().getString(R.string.tooBigField)
            field.requestFocus()
            return true
        }
    }
    return false
}

class PhoneInputFilter : InputFilter {
    private val filterRegex = "\\+?|\\+[7]?|\\+[7]-\\d{0,3}|\\+[7]-\\d{3}-\\d{0,3}" +
            "|\\+[7]-\\d{3}-\\d{3}-\\d{0,2}|\\+[7]-\\d{3}-\\d{3}-\\d{2}-\\d{0,2}"
    private val pattern = Pattern.compile(filterRegex)
    private val builder = StringBuilder()

    override fun filter(
        src: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dStart: Int,
        dEnd: Int
    ): CharSequence? {
        builder.clear()
        builder.append(dest)
        builder.replace(dStart, dEnd, src.substring(start, end))
        if (builder.length < 3) return dest.substring(dStart, dEnd)
        return if (!pattern.matcher(builder.toString()).matches()) ""
        else {
            builder.append("-")
            if (pattern.matcher(builder.toString()).matches() && src.isNotEmpty()) {
                src.substring(start, end) + "-"
            } else null
        }
    }

    companion object {
        private const val phonePattern = "\\+[7]-\\d{3}-\\d{3}-\\d{2}-\\d{2}"
        val phoneRegex: Pattern = Pattern.compile(phonePattern)
    }

}

class PasswordInputFilter : InputFilter {

    private val patternString = "[\\p{ASCII}]*"
    private val pattern: Pattern = Pattern.compile(patternString)

    override fun filter(
        src: CharSequence,
        sStart: Int,
        sEnd: Int,
        dest: Spanned,
        dStart: Int,
        dEnd: Int
    ): CharSequence? {
        val builder = StringBuilder(dest)
        builder.replace(dStart, dEnd, src.substring(sStart, sEnd))
        return if (pattern.matcher(builder.toString()).matches()) null else ""
    }

}

fun scrollToTheTop(scrollView: NestedScrollView) {
    scrollView.smoothScrollTo(0, 0)
}

@SuppressLint("ConstantLocale")
val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

@SuppressLint("SimpleDateFormat")
val serverDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")

fun tryStringToDate(
    dateString: String
): Date {
    return try {
        val date = simpleDateFormat.parse(dateString)
        date
    } catch (exc: ParseException) {
        Date()
    }
}

fun tryFormatDate(
    patternFrom: SimpleDateFormat,
    patternTo: SimpleDateFormat,
    dateString: String?,
    default: String
): String {
    dateString?.run {
        return try {
            val date = patternFrom.parse(this)
            if (date != null) patternTo.format(date)
            else default
        } catch (exc: ParseException) {
            default
        }
    } ?: return default
}

val jsonDoubleFormat = DecimalFormat("0.######").apply {
    val symbols = DecimalFormatSymbols()
    symbols.decimalSeparator = '.'
    decimalFormatSymbols = symbols
}

fun tryParseDoubleValue(string: String?): Double {
    if (string.isNullOrBlank()) return 0.0
    val doubleFormat = jsonDoubleFormat
    return try {
        doubleFormat.parse(string)?.toDouble() ?: 0.0
    } catch (exc: ParseException) {
        0.0
    }
}

val emptyImageUri: Uri = Uri.parse("android.resource://ru.hvost.news/drawable/empty_image")

fun getUriForBackendImagePath(imagePath: String?): Uri {
    if (imagePath == null) return emptyImageUri
    return Uri.parse(APIService.baseUrl + imagePath)
}

val moneyFormat = DecimalFormat("###,###,##0")

enum class WordEnding { TYPE_1, TYPE_2, TYPE_3 }

fun getWordEndingType(count: Int): WordEnding {
    return when {
        count % 100 in 11..19 -> WordEnding.TYPE_3
        count % 10 == 1 -> WordEnding.TYPE_1
        count % 10 in 2..4 -> WordEnding.TYPE_2
        else -> WordEnding.TYPE_3
    }
}

fun showNotReadyToast() {
    Toast.makeText(
        App.getInstance(),
        App.getInstance().getString(R.string.notReadyToast),
        Toast.LENGTH_SHORT
    ).show()
}

class LinearRvItemDecorations(
    sideMarginsDimension: Int? = null,
    marginBetweenElementsDimension: Int? = null
) : RecyclerView.ItemDecoration() {

    private val res = App.getInstance().resources
    private val sideMargins =
        if (sideMarginsDimension != null)
            res.getDimension(sideMarginsDimension).toInt()
        else 0
    private val verticalMargin =
        if (marginBetweenElementsDimension != null)
            res.getDimension(marginBetweenElementsDimension).toInt()
        else 0

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        outRect.set(
            sideMargins,
            if (position == 0) verticalMargin else 0,
            sideMargins,
            verticalMargin
        )
    }

}

class GridRvItemDecorations(
    sideMarginsDimension: Int? = null,
    marginBetweenElementsDimension: Int? = null
) : RecyclerView.ItemDecoration() {

    private val res = App.getInstance().resources
    private val sideMargin =
        if (sideMarginsDimension != null)
            res.getDimension(sideMarginsDimension).toInt()
        else 0
    private val elementsMargin =
        if (marginBetweenElementsDimension != null)
            res.getDimension(marginBetweenElementsDimension).toInt()
        else 0

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position % 2 == 0) {
            outRect.left = sideMargin
            outRect.right = elementsMargin / 2
        } else {
            outRect.left = elementsMargin / 2
            outRect.right = sideMargin
        }
        outRect.bottom = elementsMargin
    }
}

val ordersStatuses = mapOf(
    Pair("all", App.getInstance().getString(R.string.orderStatusAll)),
    Pair("N", App.getInstance().getString(R.string.orderStatusN)),
    Pair("DT", App.getInstance().getString(R.string.orderStatusDT)),
    Pair("P", App.getInstance().getString(R.string.orderStatusP)),
    Pair("F", App.getInstance().getString(R.string.orderStatusF)),
    Pair("OT", App.getInstance().getString(R.string.orderStatusOT))
)

val String.getValue: String
    get() = this

object UniqueIdGenerator {

    private var uniqueId = 0L

    fun nextId() = ++uniqueId

}

fun getClearPhoneString(source: String?): String {
    if (source == null) return ""
    val builder = StringBuilder()
    for (char in source) {
        if (char.isDigit()) builder.append(char)
    }
    return builder.toString()
}

fun startIntentActionView(context: Context, url: String) {
    val fileIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(url)
    )
    try {
        context.startActivity(fileIntent)
    } catch (e: Exception) {
        if (e is ActivityNotFoundException) {
            Toast.makeText(
                context,
                context.getString(R.string.no_required_attachments_fo_intent),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(context, context.getString(R.string.unknown_error), Toast.LENGTH_SHORT)
                .show()
        }
    }
}

fun formatPhoneString(phone: String?): String {
    val prefix = App.getInstance().getString(R.string.phonePrefix)
    if (phone.isNullOrBlank()) return prefix
    else {
        val numbers = getClearPhoneString(phone)
        val pattern = Pattern.compile("7[0-9]{10}")
        val matcher = pattern.matcher(numbers)
        return if (matcher.matches()) {
            val builder = StringBuilder(numbers)
            builder.insert(0, "+")
            builder.insert(2, "-")
            builder.insert(6, "-")
            builder.insert(10, "-")
            builder.insert(13, "-")
            builder.toString()
        } else {
            prefix
        }
    }
}