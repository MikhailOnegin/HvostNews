package ru.hvost.news.sergeev

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import ru.hvost.news.utils.UniqueIdGenerator
import ru.hvost.news.utils.WordEnding
import ru.hvost.news.utils.getClearPhoneString
import ru.hvost.news.utils.getWordEndingType

@RunWith(AndroidJUnit4::class)
class UtilsTest {

    @Test
    fun wordEndingWorksCorrectOnTYPE_1() {
        assertThat(getWordEndingType(1), `is`(WordEnding.TYPE_1))
        assertThat(getWordEndingType(21), `is`(WordEnding.TYPE_1))
        assertThat(getWordEndingType(101), `is`(WordEnding.TYPE_1))
        assertThat(getWordEndingType(121), `is`(WordEnding.TYPE_1))
    }

    @Test
    fun wordEndingWorksCorrectOnTYPE_2() {
        assertThat(getWordEndingType(2), `is`(WordEnding.TYPE_2))
        assertThat(getWordEndingType(22), `is`(WordEnding.TYPE_2))
        assertThat(getWordEndingType(102), `is`(WordEnding.TYPE_2))
        assertThat(getWordEndingType(122), `is`(WordEnding.TYPE_2))
    }

    @Test
    fun wordEndingWorksCorrectOnTYPE_3() {
        assertThat(getWordEndingType(6), `is`(WordEnding.TYPE_3))
        assertThat(getWordEndingType(10), `is`(WordEnding.TYPE_3))
        assertThat(getWordEndingType(13), `is`(WordEnding.TYPE_3))
        assertThat(getWordEndingType(17), `is`(WordEnding.TYPE_3))
        assertThat(getWordEndingType(26), `is`(WordEnding.TYPE_3))
        assertThat(getWordEndingType(106), `is`(WordEnding.TYPE_3))
        assertThat(getWordEndingType(126), `is`(WordEnding.TYPE_3))
    }

    @Test
    fun uniqueIdGeneratorWorksCorrect() {
        assertThat(UniqueIdGenerator.nextId(), `is`(1L))
        assertThat(UniqueIdGenerator.nextId(), `is`(2L))
        assertThat(UniqueIdGenerator.nextId(), `is`(3L))
        assertThat(UniqueIdGenerator.nextId(), `is`(4L))
        assertThat(UniqueIdGenerator.nextId(), `is`(5L))
    }

    @Test
    fun getClearPhoneStringWorksCorrect() {
        assertThat(getClearPhoneString(null), `is`(""))
        assertThat(getClearPhoneString("79630956722"), `is`("79630956722"))
        assertThat(getClearPhoneString("7-963-095-67-22"), `is`("79630956722"))
        assertThat(getClearPhoneString("7A9=630+956-72CO2"), `is`("79630956722"))
    }

}