package ru.hvost.news.utils.events

import android.view.View
import androidx.lifecycle.Observer
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State

class DefaultNetworkEventObserver(
    private val anchorView: View,
    private val doOnLoading: (()->Unit)? = null,
    private val doOnSuccess: (()->Unit)? = null,
    private val doOnError: (()->Unit)? = null,
    private val doOnFailure: (()->Unit)? = null
) : Observer<NetworkEvent<State>> {

    override fun onChanged(event: NetworkEvent<State>?) {
        event?.run {
            event.getContentIfNotHandled()?.run {
                val context = App.getInstance()
                when(this) {
                    State.LOADING -> doOnLoading?.invoke()
                    State.SUCCESS -> doOnSuccess?.invoke()
                    State.ERROR -> {
                        createSnackbar(
                            anchorView = anchorView,
                            text = error ?: context.getString(R.string.networkErrorMessage),
                            context.getString(R.string.buttonOk),
                            doOnError
                        ).show()
                    }
                    State.FAILURE -> {
                        createSnackbar(
                            anchorView = anchorView,
                            text = context.getString(R.string.networkFailureMessage),
                            context.getString(R.string.buttonOk),
                            doOnFailure
                        ).show()
                    }
                }
            }
        }
    }

}