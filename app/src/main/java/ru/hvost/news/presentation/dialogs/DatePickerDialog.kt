package ru.hvost.news.presentation.dialogs

import android.app.Dialog
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import ru.hvost.news.R
import java.util.*

class DatePickerDialog(
        private val onDateSelected: (Date) -> Unit,
        private val initialDate: Date? = null,
        private val maxDate: Date? = null
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val contentView = LayoutInflater.from(requireActivity())
                .inflate(R.layout.dialog_date_picker, null)
        val picker = contentView.findViewById<DatePicker>(R.id.datePicker)

        val buttonSelect = contentView.findViewById<Button>(R.id.buttonSelect)
        val selectedDate = Calendar.getInstance()
        if(initialDate != null) selectedDate.time = initialDate
        buttonSelect.setOnClickListener{
            if (isLollipop()) setDate(selectedDate, picker.year, picker.month, picker.dayOfMonth)
            dismiss()
            onDateSelected.invoke(selectedDate.time)
        }
        picker.init(
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)) { _, year, month, day ->
            setDate(selectedDate, year, month, day)
        }
        maxDate?.run{ picker.maxDate = time }

        dialog.setContentView(contentView)
        dialog.setCancelable(true)
        return dialog
    }

    private fun setDate(calendar: Calendar, year: Int, month: Int, day: Int){
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }

    private fun isLollipop(): Boolean{
        val sdkInt = Build.VERSION.SDK_INT
        return (sdkInt == VERSION_CODES.LOLLIPOP) || (sdkInt == VERSION_CODES.LOLLIPOP_MR1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Dialog)
    }

}