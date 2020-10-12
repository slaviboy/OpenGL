package com.slaviboy.opengl

import android.graphics.Color
import android.os.Build
import com.google.common.truth.Truth
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.main.OpenGLStatic
import com.slaviboy.opengl.shapes.multiple.Rectangles
import com.slaviboy.opengl.shapes.multiple.RegularPolygons
import com.slaviboy.opengl.shapes.multiple.Shapes
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class RegularPolygonsUnitTest {

    @Test
    fun MainTest() {

        // set device width, since the matrix is dependent on it
        OpenGLStatic.setComponents(1080f, 1920f)

        FillRegularPolygonsTest()
        StrokeRegularPolygonTest()
    }

    fun FillRegularPolygonsTest() {

        val mainGestureDetector = OpenGLMatrixGestureDetector()

        // coordinates and colors for the regular polygons in format [cx1,cy1,radius1,angle1,  cx2,cy2,radius2,angle2,...]
        val regularPolygonsCoordinatesInfo = floatArrayOf(
            100f, 100f, 100f, 0f, // first regular polygon coordinates info
            400f, 400f, 200f, 0f  // second regular polygon coordinates info
        )
        val regularPolygonsColors = intArrayOf(
            Color.RED,   // first regular polygon colors
            Color.GREEN  // second regular polygon colors
        )

        // initialize fill regular polygon
        val regularPolygons = RegularPolygons(regularPolygonsCoordinatesInfo, regularPolygonsColors, 1f, true, mainGestureDetector, 1, Shapes.STYLE_FILL, 5)
        Truth.assertThat(regularPolygons.coordinates).isEqualTo(
            floatArrayOf(
                130.9017f, 4.894348f, 200.0f, 100.0f, 100.0f, 100.0f, 200.0f, 100.0f, 130.9017f, 195.10565f, 100.0f, 100.0f, 130.9017f, 195.10565f, 19.098297f, 158.77853f, 100.0f, 100.0f, 19.098297f, 158.77853f, 19.098297f, 41.221474f, 100.0f, 100.0f, 19.098297f, 41.221474f, 130.9017f, 4.894348f, 100.0f, 100.0f, 461.8034f, 209.7887f, 600.0f, 400.0f, 400.0f, 400.0f, 600.0f, 400.0f, 461.8034f, 590.2113f, 400.0f, 400.0f, 461.8034f, 590.2113f, 238.1966f, 517.55707f, 400.0f, 400.0f, 238.1966f, 517.55707f, 238.1966f, 282.44293f, 400.0f, 400.0f, 238.1966f, 282.44293f, 461.8034f, 209.7887f, 400.0f, 400.0f
            )
        )
        Truth.assertThat(regularPolygons.colors).isEqualTo(
            intArrayOf(
                Color.RED,   // first rectangle colors
                Color.GREEN  // second rectangle colors
            )
        )
        Truth.assertThat(regularPolygons.coordinatesOpenGL).isEqualTo(
            floatArrayOf(
                -0.40906775f, -0.01529479f, -0.625f, -0.3125f, -0.3125f, -0.3125f, -0.625f, -0.3125f, -0.40906775f, -0.609705f, -0.3125f, -0.3125f, -0.40906775f, -0.609705f, -0.05968213f, -0.49618292f, -0.3125f, -0.3125f, -0.05968213f, -0.49618292f, -0.05968213f, -0.12881708f, -0.3125f, -0.3125f, -0.05968213f, -0.12881708f, -0.40906775f, -0.01529479f, -0.3125f, -0.3125f, -1.4431356f, -0.6555896f, -1.875f, -1.2499999f, -1.25f, -1.2499999f, -1.875f, -1.2499999f, -1.4431356f, -1.8444103f, -1.25f, -1.2499999f, -1.4431356f, -1.8444103f, -0.7443643f, -1.6173658f, -1.25f, -1.2499999f, -0.7443643f, -1.6173658f, -0.7443643f, -0.88263416f, -1.25f, -1.2499999f, -0.7443643f, -0.88263416f, -1.4431356f, -0.6555896f, -1.25f, -1.2499999f
            )
        )
        Truth.assertThat(regularPolygons.colorsOpenGL).isEqualTo(
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f
            )
        )
    }

    fun StrokeRegularPolygonTest() {

    }
}