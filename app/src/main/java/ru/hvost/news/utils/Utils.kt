package ru.hvost.news.utils

import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import ru.hvost.news.App
import ru.hvost.news.R

fun createSnackbar(
    anchorView: View,
    text: String?,
    buttonText: String? = null,
    onButtonClicked: (()->Unit)? = null
): Snackbar {
    val snackbar = Snackbar.make(
        anchorView,
        text.toString(),
        Snackbar.LENGTH_SHORT
    )
    snackbar.setBackgroundTint(ContextCompat.getColor(App.getInstance(), R.color.colorAccent))
    snackbar.setTextColor(ContextCompat.getColor(App.getInstance(), android.R.color.white))
    snackbar.setActionTextColor(ContextCompat.getColor(App.getInstance(), android.R.color.white))
    val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.maxLines = 5
    if(buttonText != null) {
        snackbar.setAction(buttonText) {
            onButtonClicked?.invoke() ?: snackbar.dismiss()
        }
        snackbar.duration = Snackbar.LENGTH_INDEFINITE
    }
    return snackbar
}

fun isPhoneFieldIncorrect(field: TextInputEditText): Boolean {
    //TODO Реализовать проверку номера телефона. Устанавливать ошибку в поле.
    return false
}

fun isEmailFieldIncorrect(field: TextInputEditText): Boolean {
    if(!Patterns.EMAIL_ADDRESS.matcher(field.text.toString()).matches()) {
        field.error = App.getInstance().getString(R.string.incorrectEmail)
        field.requestFocus()
        return true
    }
    return false
}

fun hasBlankField(vararg fields: TextInputEditText): Boolean {
    for(field in fields) {
        if(field.text.isNullOrBlank()){
            field.error = App.getInstance().getString(R.string.requiredField)
            field.requestFocus()
            return true
        }
    }
    return false
}

fun hasTooLongField(vararg fields: TextInputEditText): Boolean {
    val res = App.getInstance().resources
    for(field in fields) {
        if(field.text?.length ?: 0 > res.getInteger(R.integer.editTextFieldMaxSize)){
            field.error = App.getInstance().getString(R.string.tooBigField)
            field.requestFocus()
            return true
        }
    }
    return false
}