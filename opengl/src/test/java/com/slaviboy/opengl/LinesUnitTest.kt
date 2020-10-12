package com.slaviboy.opengl

import android.graphics.Color
import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.main.OpenGLStatic
import com.slaviboy.opengl.shapes.multiple.Lines
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class LinesUnitTest {

    @Test
    fun MainTest() {

        // set device width, since the matrix is dependent on it
        OpenGLStatic.setComponents(1080f, 1920f)

        val mainGestureDetector = OpenGLMatrixGestureDetector()

        // coordinates and colors for the lines
        val linesCoordinates = floatArrayOf(
            490f, 380f, 100f, 540f,  // first line coordinates
            800f, 200f, 750f, 600f,  // second line coordinates
            900f, 600f, 950f, 700f   // third line coordinates
        )
        val linesColors = intArrayOf(
            Color.RED,   // first line colors
            Color.GREEN, // second line colors
            Color.YELLOW // third line colors
        )


        // initialize stroke line
        val lines = Lines(linesCoordinates, linesColors, 1f, true, mainGestureDetector, 1)

        // check initial values
        assertThat(lines.coordinates).isEqualTo(
            floatArrayOf(
                490f, 380f, 100f, 540f,  // first line coordinates
                800f, 200f, 750f, 600f,  // second line coordinates
                900f, 600f, 950f, 700f   // third line coordinates
            )
        )
        assertThat(lines.colors).isEqualTo(intArrayOf(Color.RED, Color.GREEN, Color.YELLOW))
        assertThat(lines.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f,   // first line coordinates
                -2.5f, -0.625f, -2.34375f, -1.875f,           // second line coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f        // third line coordinates
            )
        )
        assertThat(lines.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // first line vertices color channels r,g,b,a
                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, // second line vertices color channels r,g,b,a
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f  // third line vertices color channels r,g,b,a
            )
        )

        // set color for particular line at given index, check if color channels values {1.0f, 1.0f, 1.0f, 1.0f} are set for each vertex
        lines.setColor(1, Color.WHITE)
        assertThat(lines.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, // new line vertices color channels r,g,b,a
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // delete line at given index
        lines.delete(0)
        assertThat(lines.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -2.5f, -0.625f, -2.34375f, -1.875f,           // second line coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f        // third line coordinates
            )
        )
        assertThat(lines.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // add new line at given index with coordinates given in graphic coordinate system
        lines.add(0, Color.TRANSPARENT, 490f, 380f, 100f, 540f)
        assertThat(lines.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f,  // new line coordinates
                -2.5f, -0.625f, -2.34375f, -1.875f,
                -2.8125f, -1.875f, -2.96875f, -2.1875f
            )
        )
        assertThat(lines.colorsOpenGL).isEqualTo(
            floatArrayOf(
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, // new color added
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // move line at given index, by changing the coordinates for that particular line in graphic coordinate system
        lines.change(1, 190f, 180f, 500f, 140f)
        assertThat(lines.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f,
                -0.59375f, -0.5625f, -1.5625f, -0.4375f,  // moved line coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f
            )
        )

        // move line at given index, by changing the coordinates for that particular line in OpenGL coordinate system
        lines.changeOpenGL(2, -1.2f, -1.5f, -0.41f, -1.62f)
        assertThat(lines.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f,
                -0.59375f, -0.5625f, -1.5625f, -0.4375f,
                -1.2f, -1.5f, -0.41f, -1.62f  // moved line coordinates
            )
        )

        // set lines colors as integer representation (1 value per shape) and coordinates in graphic coordinate system
        lines.setShape(
            floatArrayOf(
                490f, 380f, 100f, 540f,  // first line coordinates
                900f, 600f, 950f, 700f   // second line coordinates
            ),
            intArrayOf(Color.BLACK, Color.WHITE)
        )
        assertThat(lines.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f,   // first line coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f        // second line coordinates
            )
        )
        assertThat(lines.colorsOpenGL).isEqualTo(
            floatArrayOf(
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // first line vertices color channels r,g,b,a
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f  // second line vertices color channels r,g,b,a
            )
        )

        // set lines colors as integer representation (1 value per shape) and coordinates in OpenGL coordinate system
        lines.setShapeOpenGL(
            floatArrayOf(
                1.2f, 3.2f, 9.1f, -5.0f,     // first line coordinates
                3.12f, -2.14f, -5.1f, 3.1f   // second line coordinates
            ),
            intArrayOf(Color.WHITE, Color.BLACK)
        )
        assertThat(lines.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                1.2f, 3.2f, 9.1f, -5.0f,     // first line coordinates
                3.12f, -2.14f, -5.1f, 3.1f   // second line coordinates
            )
        )
        assertThat(lines.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,  // second line vertices color channels r,g,b,a
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f   // first line vertices color channels r,g,b,a
            )
        )
    }

}