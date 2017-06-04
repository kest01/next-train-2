package ru.kest.trainswidget.util

import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

/**
 * DateUtil test
 * Created by Konstantin on 04.06.2017.
 */
class DateUtilTest {

    val TEST_DATE = Date(1496571823662L)

    @Test
    fun testGetTime() {
        val res = DateUtil.getTime(TEST_DATE)

        assertTrue(res == "13:23")
    }

    @Test
    fun testGetDay() {
        val res = DateUtil.getDay(TEST_DATE)

        assertTrue(res == "2017-06-04")
    }
}