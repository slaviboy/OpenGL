package com.slaviboy.opengl

import com.google.common.truth.Truth
import com.slaviboy.opengl.main.OpenGLStatic.concat
import com.slaviboy.opengl.main.OpenGLStatic.delete
import org.junit.Test

class OpenGLStaticUnitTest {

    @Test
    fun MainTest(){
        ConcatFloatArrays()
        DeleteFloatArray()
    }
    fun ConcatFloatArrays() {

        val array1 = floatArrayOf(
            1f, 2f, 3f, 4f,     // first line coordinate
            5f, 6f, 7f, 8f,     // second line coordinate
            9f, 10f, 11f, 12f   // third line coordinate
        )
        val array2 = floatArrayOf(44f, 55f, 66f, 77f)

        val array3 = array1.concat(1, array2, 4)
        Truth.assertThat(array3).isEqualTo(
            floatArrayOf(
                1f, 2f, 3f, 4f,     // first line coordinate
                44f, 55f, 66f, 77f, // new line coordinate at i = 1
                5f, 6f, 7f, 8f,     // second line coordinate
                9f, 10f, 11f, 12f   // third line coordinate
            )
        )

        // add line coordinates at first index
        val array4 = floatArrayOf(99f, 88f, 77f, 66f)
        val array5 = array3.concat(0, array4, 4)
        Truth.assertThat(array5).isEqualTo(
            floatArrayOf(
                99f, 88f, 77f, 66f, // new line coordinate at i = 0
                1f, 2f, 3f, 4f,
                44f, 55f, 66f, 77f,
                5f, 6f, 7f, 8f,
                9f, 10f, 11f, 12f
            )
        )

        // add line coordinates at last index
        val array6 = floatArrayOf(51f, 52f, 53f, 54f)
        val array7 = array5.concat(5, array6, 4)
        Truth.assertThat(array7).isEqualTo(
            floatArrayOf(
                99f, 88f, 77f, 66f,
                1f, 2f, 3f, 4f,
                44f, 55f, 66f, 77f,
                5f, 6f, 7f, 8f,
                9f, 10f, 11f, 12f,
                51f, 52f, 53f, 54f // new line coordinate at i=5
            )
        )
    }

    fun DeleteFloatArray() {

        val array1 = floatArrayOf(
            1f, 2f, 3f, 4f,     // first line coordinate
            5f, 6f, 7f, 8f,     // second line coordinate
            9f, 10f, 11f, 12f   // third line coordinate
        )

        // remove coordinates for the first line
        val array2 = array1.delete(0, 4)
        Truth.assertThat(array2).isEqualTo(
            floatArrayOf(
                5f, 6f, 7f, 8f,     // second line coordinate
                9f, 10f, 11f, 12f   // third line coordinate
            )
        )

        // remove coordinates for the second line
        val array3 = array1.delete(1, 4)
        Truth.assertThat(array3).isEqualTo(
            floatArrayOf(
                1f, 2f, 3f, 4f,     // first line coordinate
                9f, 10f, 11f, 12f   // third line coordinate
            )
        )

        // remove coordinates for the last line
        val array4 = array1.delete(2, 4)
        Truth.assertThat(array4).isEqualTo(
            floatArrayOf(
                1f, 2f, 3f, 4f,     // first line coordinate
                5f, 6f, 7f, 8f      // second line coordinate
            )
        )
    }


}