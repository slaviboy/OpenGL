package com.slaviboy.opengl

import android.graphics.Color
import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.main.OpenGLStatic
import com.slaviboy.opengl.shapes.Shapes.Companion.STYLE_FILL
import com.slaviboy.opengl.shapes.Shapes.Companion.STYLE_STROKE
import com.slaviboy.opengl.shapes.multiple.Triangles
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class TrianglesUnitTest {

    @Test
    fun MainTest() {

        // set device width, since the matrix is dependent on it
        OpenGLStatic.setComponents(1080f, 1920f)

        FillTrianglesTest()
        StrokeTriangleTest()
    }

    /**
     * Unit test for fill triangles, when the type is set to STYLE_FILL for filling each triangle
     * with given color. Here the coordinates for the vertices are kept the same and the triangles
     * are drawn.
     */
    fun FillTrianglesTest() {

        val mainGestureDetector = OpenGLMatrixGestureDetector()

        // coordinates and colors for the triangles
        val trianglesCoordinates = floatArrayOf(
            490f, 380f, 100f, 540f, 500f, 600f,  // first triangle coordinates
            800f, 200f, 750f, 600f, 400f, 900f,  // second triangle coordinates
            900f, 600f, 950f, 700f, 400f, 900f   // third triangle coordinates
        )
        val trianglesColors = intArrayOf(
            Color.RED,   // first triangle colors
            Color.GREEN, // second triangle colors
            Color.YELLOW // third triangle colors
        )


        // initialize fill triangle
        val triangles = Triangles(trianglesCoordinates, trianglesColors, 1f, true, mainGestureDetector, 1, STYLE_FILL)

        // check initial values
        assertThat(triangles.coordinates).isEqualTo(floatArrayOf(490f, 380f, 100f, 540f, 500f, 600f, 800f, 200f, 750f, 600f, 400f, 900f, 900f, 600f, 950f, 700f, 400f, 900f))
        assertThat(triangles.colors).isEqualTo(intArrayOf(Color.RED, Color.GREEN, Color.YELLOW))
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -1.5625f, -1.875f,  // first triangle coordinate
                -2.5f, -0.625f, -2.34375f, -1.875f, -1.25f, -2.8125f,           // second triangle coordinate
                -2.8125f, -1.875f, -2.96875f, -2.1875f, -1.25f, -2.8125f        // third triangle coordinate
            )
        )
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // first triangle vertices color channels r,g,b,a
                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, // second triangle vertices color channels r,g,b,a
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f  // third triangle vertices color channels r,g,b,a
            )
        )

        // delete triangle at given index
        triangles.delete(2)
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -1.5625f, -1.875f,  // first triangle coordinate
                -2.5f, -0.625f, -2.34375f, -1.875f, -1.25f, -2.8125f            // second triangle coordinate
            )
        )
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // first triangle vertices color channels r,g,b,a
                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f  // second triangle vertices color channels r,g,b,a
            )
        )


        // add new triangle at given index with coordinates given in graphic coordinate system
        triangles.add(1, Color.WHITE, 343.2f, 32.9f, 100.91f, 54.2f, 71.2f, 312f)
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -1.5625f, -1.875f,                  // first triangle coordinate
                -1.0725f, -0.10281253f, -0.31534386f, -0.16937494f, -0.22249997f, -0.9749999f,  // new triangle coordinate
                -2.5f, -0.625f, -2.34375f, -1.875f, -1.25f, -2.8125f                            // second triangle coordinate
            )
        )
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // first triangle vertices color channels r,g,b,a
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, // new triangle vertices color channels r,g,b,a
                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f  // second triangle vertices color channels r,g,b,a
            )
        )

        // move triangle at given index, by changing the coordinates for that particular triangle in graphic coordinate system
        triangles.change(1, 10f, 332f, 231f, 12.3f, 91.2f, 66.6f)
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -1.5625f, -1.875f,                   // first triangle coordinate
                -0.03125f, -1.0374999f, -0.72187495f, -0.038437366f, -0.28499997f, -0.20812488f, // new triangle coordinate
                -2.5f, -0.625f, -2.34375f, -1.875f, -1.25f, -2.8125f                             // second triangle coordinate
            )
        )

        // set color for particular triangle at given index, check if color channels values {0.0f, 0.0f, 0.0f, 1.0f} are set for each vertex
        triangles.setColor(1, Color.BLACK)
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // new triangle vertices color channels r,g,b,a
                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f
            )
        )

        // set triangle color as integer representation (1 value per shape) and coordinates in OpenGL coordinate system
        triangles.setShapeOpenGL(
            floatArrayOf(
                21f, 212f, 522f, 71f, 321f, 73f, // first triangle coordinates
                77f, 412f, 71f, 51f, 174f, 273f  // second triangle coordinates
            ),
            intArrayOf(Color.GREEN, Color.GRAY)
        )
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                21f, 212f, 522f, 71f, 321f, 73f,
                77f, 412f, 71f, 51f, 174f, 273f
            )
        )
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f,
                0.53333336f, 0.53333336f, 0.53333336f, 1.0f, 0.53333336f, 0.53333336f, 0.53333336f, 1.0f, 0.53333336f, 0.53333336f, 0.53333336f, 1.0f
            )
        )

        // set shapes using arrays the colors as integer array (1 int value per shape) and coordinates as float array in graphic coordinate system
        triangles.setShape(
            floatArrayOf(
                490f, 380f, 100f, 540f, 500f, 600f,  // first triangle coordinates
                800f, 200f, 750f, 600f, 400f, 900f   // second triangle coordinates
            ),
            intArrayOf(Color.WHITE, Color.BLACK)
        )
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -1.5625f, -1.875f,  // first triangle coordinate
                -2.5f, -0.625f, -2.34375f, -1.875f, -1.25f, -2.8125f            // second triangle coordinate
            )
        )
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, // first triangle vertices color channels r,g,b,a
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f  // second triangle vertices color channels r,g,b,a
            )
        )
    }

    /**
     * Unit test for fill triangles, when the type is set to STYLE_STROKE for stroking each triangle
     * with given color. Here the coordinates for the vertices are converted in to line coordinate,
     * each line connect the coordinates from each two vertices coordinate in the input array and
     * lines are drawn instead of triangles.
     */
    fun StrokeTriangleTest() {

        val mainGestureDetector = OpenGLMatrixGestureDetector()

        // coordinates and colors for the triangles
        val trianglesCoordinates = floatArrayOf(
            490f, 380f, 100f, 540f, 500f, 600f,  // first triangle coordinates
            800f, 200f, 750f, 600f, 400f, 900f,  // second triangle coordinates
            900f, 600f, 950f, 700f, 400f, 900f   // third triangle coordinates
        )
        val trianglesColors = intArrayOf(
            Color.RED,   // first triangle colors
            Color.GREEN, // second triangle colors
            Color.YELLOW // third triangle colors
        )


        // initialize stroke triangle
        val triangles = Triangles(trianglesCoordinates, trianglesColors, 1f, true, mainGestureDetector, 1, STYLE_STROKE)

        // check initial values
        assertThat(triangles.coordinates).isEqualTo(
            floatArrayOf(
                490f, 380f, 100f, 540f, 100f, 540f, 500f, 600f, 500f, 600f, 490f, 380f,
                800f, 200f, 750f, 600f, 750f, 600f, 400f, 900f, 400f, 900f, 800f, 200f,
                900f, 600f, 950f, 700f, 950f, 700f, 400f, 900f, 400f, 900f, 900f, 600f
            )
        )
        assertThat(triangles.colors).isEqualTo(intArrayOf(Color.RED, Color.GREEN, Color.YELLOW))
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f, // first triangle lines coordinates
                -2.5f, -0.625f, -2.34375f, -1.875f, -2.34375f, -1.875f, -1.25f, -2.8125f, -1.25f, -2.8125f, -2.5f, -0.625f,                   // second triangle lines coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f, -2.96875f, -2.1875f, -1.25f, -2.8125f, -1.25f, -2.8125f, -2.8125f, -1.875f            // third triangle lines coordinates
            )
        )
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // first triangle vertices color channels r,g,b,a
                0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, // second triangle vertices color channels r,g,b,a
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f  // third triangle vertices color channels r,g,b,a
            )
        )

        // delete second triangle
        triangles.delete(1)
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f, // first triangle lines coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f, -2.96875f, -2.1875f, -1.25f, -2.8125f, -1.25f, -2.8125f, -2.8125f, -1.875f            // third triangle lines coordinates
            )
        )
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // first triangle vertices color channels r,g,b,a
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f  // third triangle vertices color channels r,g,b,a
            )
        )

        // add new triangle using coordinates in graphic coordinate system
        triangles.add(1, Color.TRANSPARENT, 234f, 21f, 121f, 91.4f, 66.6f, 314.7f)
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f,                                       // first triangle lines coordinates
                -0.73125f, -0.06562519f, -0.37812495f, -0.28562498f, -0.37812495f, -0.28562498f, -0.20812488f, -0.98343754f, -0.20812488f, -0.98343754f, -0.73125f, -0.06562519f,   // added triangle lines coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f, -2.96875f, -2.1875f, -1.25f, -2.8125f, -1.25f, -2.8125f, -2.8125f, -1.875f                                                  // third triangle lines coordinates
            )
        )
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // first triangle vertices color channels r,g,b,a
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, // added triangle vertices color channels r,g,b,a
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f  // third triangle vertices color channels r,g,b,a
            )
        )

        // move triangle by changing the coordinates in graphic coordinate system
        triangles.change(1, 490f, 380f, 100f, 540f, 500f, 600f)
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f, // first triangle lines coordinates
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f, // second triangle lines coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f, -2.96875f, -2.1875f, -1.25f, -2.8125f, -1.25f, -2.8125f, -2.8125f, -1.875f            // third triangle lines coordinates
            )
        )

        // add triangle using coordinates in OpenGL coordinate system
        triangles.addOpenGL(3, Color.WHITE, 2.5f, -0.625f, -2.34375f, -1.875f, -1.25f, -2.8125f)
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f, // first triangle lines coordinates
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f, // second triangle lines coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f, -2.96875f, -2.1875f, -1.25f, -2.8125f, -1.25f, -2.8125f, -2.8125f, -1.875f,           // third triangle lines coordinates
                2.5f, -0.625f, -2.34375f, -1.875f, -2.34375f, -1.875f, -1.25f, -2.8125f, -1.25f, -2.8125f, 2.5f, -0.625f                      // added triangle line coordinates
            )
        )
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // first triangle vertices color channels r,g,b,a
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, // second triangle vertices color channels r,g,b,a
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, // third triangle vertices color channels r,g,b,a
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f  // added triangle vertices color channels r,g,b,a
            )
        )

        // move triangle by changing the coordinates in OpenGL coordinate system
        triangles.changeOpenGL(3, 1.7f, 0.3f, -1.62f, 1.31f, 0.52f, 0.91f)
        assertThat(triangles.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f, // first triangle lines coordinates
                -1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f, // second triangle lines coordinates
                -2.8125f, -1.875f, -2.96875f, -2.1875f, -2.96875f, -2.1875f, -1.25f, -2.8125f, -1.25f, -2.8125f, -2.8125f, -1.875f,           // third triangle lines coordinates
                1.7f, 0.3f, -1.62f, 1.31f, -1.62f, 1.31f, 0.52f, 0.91f, 0.52f, 0.91f, 1.7f, 0.3f                                              // moved triangle line coordinates
            )
        )

        // set color to black, check if color channels values {0.0f, 0.0f, 0.0f, 1.0f} are set for each vertex of the second triangle
        triangles.setColor(2, Color.BLACK)
        assertThat(triangles.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // first triangle vertices color channels r,g,b,a
                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, // second triangle vertices color channels r,g,b,a
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, // changed color triangle vertices color channels r,g,b,a
                1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f  // fourth triangle vertices color channels r,g,b,a
            )
        )

        // set shapes using arrays, coordinates as float array in graphic coordinate system and the colors as integer array (1 int value per shape)
        triangles.setShape(floatArrayOf(490f, 380f, 100f, 540f, 500f, 600f), intArrayOf(Color.TRANSPARENT))
        assertThat(triangles.coordinatesOpenGL).isEqualTo(floatArrayOf(-1.53125f, -1.1875001f, -0.3125f, -1.6875f, -0.3125f, -1.6875f, -1.5625f, -1.875f, -1.5625f, -1.875f, -1.53125f, -1.1875001f))
        assertThat(triangles.colorsOpenGL).isEqualTo(floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))

        // set shapes using arrays, coordinates as float array in OpenGL coordinate system and the colors as integer array (1 int value per shape)
        triangles.setShapeOpenGL(floatArrayOf(-2.5f, -0.625f, -2.34375f, -1.875f, -1.25f, -2.8125f), intArrayOf(Color.WHITE))
        assertThat(triangles.coordinatesOpenGL).isEqualTo(floatArrayOf(-2.5f, -0.625f, -2.34375f, -1.875f, -2.34375f, -1.875f, -1.25f, -2.8125f, -1.25f, -2.8125f, -2.5f, -0.625f))
        assertThat(triangles.colorsOpenGL).isEqualTo(floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f))

    }
}