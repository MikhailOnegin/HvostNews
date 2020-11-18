package ru.hvost.news.yunusov

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import ru.hvost.news.utils.imageRegEx
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class RegExTest () {

    val correctImageHtml = "<img alt=\"Уход за щенком после кастрации\" src=\"/upload/medialibrary/c2b/uhud_za_shenkom_posle_kastracii.jpg\" title=\"Уход за щенком после кастрации\">"
    val incorrectImageHtml = "<img alt=\"Уход за щенком после кастрации\" src=\"/upload/medialibrary/c2b/uhud_za_shenkom_posle_kastracii.jpg\" title=\"Уход за щенком после кастрации\"><br>"

    @Test
    fun imageRegExWorksCorrect(){
        assert(imageRegEx.matcher(correctImageHtml).matches())
    }

    @Test
    fun imageRegExWorksWithIncorrect(){
        assert(!imageRegEx.matcher(incorrectImageHtml).matches())
    }

    @Test
    fun ageCorrectCount(){
        val birthday = "11.11.2018"
        val format = SimpleDateFormat("dd.MM.yyyy")
        val birthDate = format.parse(birthday) ?: Date()
        val today = System.currentTimeMillis()
        val age = (today - birthDate.time)/(365L*24L*60L*60L*1000L)
        assert(age == 2L)
    }

    @Test
    fun ageIncorrectCount(){
        assert(!imageRegEx.matcher(incorrectImageHtml).matches())
    }

}