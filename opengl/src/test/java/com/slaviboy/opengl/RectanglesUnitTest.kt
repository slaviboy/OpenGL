package com.slaviboy.opengl

import android.graphics.Color
import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.main.OpenGLStatic
import com.slaviboy.opengl.shapes.multiple.Shapes.Companion.STYLE_FILL
import com.slaviboy.opengl.shapes.multiple.Shapes.Companion.STYLE_STROKE
import com.slaviboy.opengl.shapes.multiple.Rectangles
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class RectanglesUnitTest {

    @Test
    fun MainTest() {

        // set device width, since the matrix is dependent on it
        OpenGLStatic.setComponents(1080f, 1920f)

        FillRectanglesTest()
        StrokeRectangleTest()
    }

    /**
     * Unit test for fill rectangles, when the type is set to STYLE_FILL for filling each rectangle
     * with given color. Here the coordinates for the vertices are generated from two triangles, that
     * make up the shape.
     */
    fun FillRectanglesTest() {

        val mainGestureDetector = OpenGLMatrixGestureDetector()

        // coordinates and colors for the triangles in format [x1,y1,width1,height1, x2,y2,width2,height2,...]
        val rectanglesCoordinates = floatArrayOf(
            100f, 100f, 200f, 200f, // first rectangle coordinates
            400f, 400f, 500f, 100f, // second rectangle coordinates
            250f, 400f, 340f, 310f  // third rectangle coordinates
        )
        val rectanglesColors = intArrayOf(
            Color.RED,   // first rectangle colors
            Color.GREEN, // second rectangle colors
            Color.YELLOW // third rectangle colors
        )


        // initialize fill rectangle
        val rectangles = Rectangles(rectanglesCoordinates, rectanglesColors, 1f, true, mainGestureDetector, 1, STYLE_FILL)
        assertThat(rectangles.coordinates).isEqualTo(
            floatArrayOf(
                100f, 100f, 100f, 300f, 300f, 300f, 300f, 300f, 300f, 100f, 100f, 100f,
                400f, 400f, 400f, 500f, 900f, 500f, 900f, 500f, 900f, 400f, 400f, 400f,
                250f, 400f, 250f, 710f, 590f, 710f, 590f, 710f, 590f, 400f, 250f, 400f
            )
        )
        assertThat(rectangles.colors).isEqualTo(
            intArrayOf(
                Color.RED,   // first rectangle colors
                Color.GREEN, // second rectangle colors
                Color.YELLOW // third rectangle colors
            )
        )
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // change color for particular rectangle at given index
        rectangles.setColor(1, Color.TRANSPARENT)
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // delete second rectangle
        rectangles.delete(1)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // add new rectangle at given index with coordinates given in graphic coordinate system
        rectangles.add(1, Color.WHITE, 50f, 50f, 150f, 50f)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -0.15625f, -0.15625f, -0.15625f, -0.3125f, -0.625f, -0.3125f, -0.625f, -0.3125f, -0.625f, -0.15625f, -0.15625f, -0.15625f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // move rectangle at given index by changing the coordinates in graphic coordinate system
        rectangles.change(1, 400f, 400f, 500f, 100f)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f
            )
        )

        // add new rectangle at given index with coordinates given in OpenGL coordinate system
        rectangles.addOpenGL(3, Color.TRANSPARENT, 1.5f, -0.74f, 1.16f, 0.31f)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f,
                1.5f, -0.74f, 1.5f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.74f, 1.5f, -0.74f
            )
        )

        // move rectangle at given index by changing the coordinates in OpenGL coordinate system
        rectangles.changeOpenGL(2, 1.2f, 0.2f, 2.2f, 3f)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                1.2f, 0.2f, 1.2f, 3.2f, 3.4f, 3.2f, 3.4f, 3.2f, 3.4f, 0.2f, 1.2f, 0.2f,
                1.5f, -0.74f, 1.5f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.74f, 1.5f, -0.74f
            )
        )

        // set shapes using arrays, coordinates as float array in graphic coordinate system and the colors as integer array (1 int value per shape)
        rectangles.setShape(
            floatArrayOf(
                400f, 400f, 500f, 100f,
                100f, 100f, 200f, 200f
            ),
            intArrayOf(Color.WHITE, Color.TRANSPARENT)
        )
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f
            )
        )

        // set shapes using arrays, coordinates as float array in OpenGL coordinate system and the colors as integer array (1 int value per shape)
        rectangles.setShapeOpenGL(
            floatArrayOf(
                1.5f, -0.74f, 1.16f, 0.31f,
                1.2f, 0.2f, 2.2f, 3f
            ),
            intArrayOf(Color.TRANSPARENT, Color.WHITE)
        )
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                1.5f, -0.74f, 1.5f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.74f, 1.5f, -0.74f,
                1.2f, 0.2f, 1.2f, 3.2f, 3.4f, 3.2f, 3.4f, 3.2f, 3.4f, 0.2f, 1.2f, 0.2f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f
            )
        )

    }

    /**
     * Unit test for fill rectangles, when the type is set to STYLE_STROKE for stroking each rectangle
     * with given color. Here the coordinates for the vertices are converted in to line coordinate,
     * each line connect the coordinates from each two vertices coordinate in the input array and
     * lines are drawn instead of triangles.
     */
    fun StrokeRectangleTest() {

        val mainGestureDetector = OpenGLMatrixGestureDetector()

        // coordinates and colors for the triangles in format [x1,y1,width1,height1, x2,y2,width2,height2,...]
        val rectanglesCoordinates = floatArrayOf(
            100f, 100f, 200f, 200f, // first rectangle coordinates
            400f, 400f, 500f, 100f, // second rectangle coordinates
            250f, 400f, 340f, 310f  // third rectangle coordinates
        )
        val rectanglesColors = intArrayOf(
            Color.RED,   // first rectangle colors
            Color.GREEN, // second rectangle colors
            Color.YELLOW // third rectangle colors
        )

        // initialize stroke rectangle
        val rectangles = Rectangles(rectanglesCoordinates, rectanglesColors, 1f, true, mainGestureDetector, 1, STYLE_STROKE)
        assertThat(rectangles.coordinates).isEqualTo(
            floatArrayOf(
                100f, 100f, 100f, 300f, 100f, 300f, 300f, 300f, 300f, 300f, 300f, 100f, 300f, 100f, 100f, 100f,
                400f, 400f, 400f, 500f, 400f, 500f, 900f, 500f, 900f, 500f, 900f, 400f, 900f, 400f, 400f, 400f,
                250f, 400f, 250f, 710f, 250f, 710f, 590f, 710f, 590f, 710f, 590f, 400f, 590f, 400f, 250f, 400f
            )
        )
        assertThat(rectangles.colors).isEqualTo(
            intArrayOf(
                Color.RED,
                Color.GREEN,
                Color.YELLOW
            )
        )
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // change color for particular rectangle at given index
        rectangles.setColor(1, Color.TRANSPARENT)
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // delete second rectangle
        rectangles.delete(1)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // add new rectangle at given index with coordinates given in graphic coordinate system
        rectangles.add(1, Color.WHITE, 50f, 50f, 150f, 50f)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -0.15625f, -0.15625f, -0.15625f, -0.3125f, -0.15625f, -0.3125f, -0.625f, -0.3125f, -0.625f, -0.3125f, -0.625f, -0.15625f, -0.625f, -0.15625f, -0.15625f, -0.15625f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // move rectangle at given index by changing the coordinates in graphic coordinate system
        rectangles.change(1, 400f, 400f, 500f, 100f)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f
            )
        )

        // add new rectangle at given index with coordinates given in OpenGL coordinate system
        rectangles.addOpenGL(3, Color.TRANSPARENT, 1.5f, -0.74f, 1.16f, 0.31f)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                -0.78125006f, -1.2499999f, -0.78125006f, -2.21875f, -0.78125006f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -2.21875f, -1.84375f, -1.2499999f, -1.84375f, -1.2499999f, -0.78125006f, -1.2499999f,
                1.5f, -0.74f, 1.5f, -0.43f, 1.5f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.74f, 2.6599998f, -0.74f, 1.5f, -0.74f
            )
        )

        // move rectangle at given index by changing the coordinates in OpenGL coordinate system
        rectangles.changeOpenGL(2, 1.2f, 0.2f, 2.2f, 3f)
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f,
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                1.2f, 0.2f, 1.2f, 3.2f, 1.2f, 3.2f, 3.4f, 3.2f, 3.4f, 3.2f, 3.4f, 0.2f, 3.4f, 0.2f, 1.2f, 0.2f,
                1.5f, -0.74f, 1.5f, -0.43f, 1.5f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.74f, 2.6599998f, -0.74f, 1.5f, -0.74f
            )
        )

        // set shapes using arrays, coordinates as float array in graphic coordinate system and the colors as integer array (1 int value per shape)
        rectangles.setShape(
            floatArrayOf(
                400f, 400f, 500f, 100f,
                100f, 100f, 200f, 200f
            ),
            intArrayOf(Color.WHITE, Color.TRANSPARENT)
        )
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.25f, -1.2499999f, -1.25f, -1.5625f, -1.25f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.5625f, -2.8125f, -1.2499999f, -2.8125f, -1.2499999f, -1.25f, -1.2499999f,
                -0.3125f, -0.3125f, -0.3125f, -0.9375f, -0.3125f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.9375f, -0.93750006f, -0.3125f, -0.93750006f, -0.3125f, -0.3125f, -0.3125f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f
            )
        )

        // set shapes using arrays, coordinates as float array in OpenGL coordinate system and the colors as integer array (1 int value per shape)
        rectangles.setShapeOpenGL(
            floatArrayOf(
                1.5f, -0.74f, 1.16f, 0.31f,
                1.2f, 0.2f, 2.2f, 3f
            ),
            intArrayOf(Color.TRANSPARENT, Color.WHITE)
        )
        assertThat(rectangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                1.5f, -0.74f, 1.5f, -0.43f, 1.5f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.43f, 2.6599998f, -0.74f, 2.6599998f, -0.74f, 1.5f, -0.74f,
                1.2f, 0.2f, 1.2f, 3.2f, 1.2f, 3.2f, 3.4f, 3.2f, 3.4f, 3.2f, 3.4f, 0.2f, 3.4f, 0.2f, 1.2f, 0.2f
            )
        )
        assertThat(rectangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f
            )
        )

    }
}