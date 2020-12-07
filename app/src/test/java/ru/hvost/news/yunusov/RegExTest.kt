package ru.hvost.news.yunusov

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import ru.hvost.news.utils.imageRegEx
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class RegExTest {

    @Test
    fun ageCorrectCount(){
        val birthday = "11.11.2018"
        val format = SimpleDateFormat("dd.MM.yyyy")
        val birthDate = format.parse(birthday) ?: Date()
        val today = System.currentTimeMillis()
        val age = (today - birthDate.time)/(365L*24L*60L*60L*1000L)
        assert(age == 2L)
    }

}