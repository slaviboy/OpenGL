package com.slaviboy.opengl

import android.graphics.Matrix
import android.graphics.PointF
import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector.Companion.angle
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector.Companion.scale
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector.Companion.translate
import com.slaviboy.opengl.main.OpenGLStatic.NEAR
import com.slaviboy.opengl.main.OpenGLStatic.RATIO
import com.slaviboy.opengl.main.OpenGLStatic.setComponents
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(RobolectricTestRunner::class)
class MatrixGestureDetectorUnitTest {

    @Test
    fun MainTest() {

        // set device width, since the matrix is dependent on it
        setComponents(1080f, 1920f)

        CheckStaticMathods()
        CheckMethods()
    }

    fun CheckMethods() {

        // create the gesture detector
        val openGLMatrixGestureDetector = OpenGLMatrixGestureDetector()

        // check matrix is identity
        assertThat(openGLMatrixGestureDetector.matrix.isIdentity).isTrue()

        // check transformations
        assertThat(openGLMatrixGestureDetector.scale).isEqualTo(1.0f)
        assertThat(openGLMatrixGestureDetector.angle).isEqualTo(0.0f)
        assertThat(openGLMatrixGestureDetector.translate).isEqualTo(PointF(0f, 0f))

        var normalizeWidth = openGLMatrixGestureDetector.normalizeWidth(400f, true)
        var normalizeHeight = openGLMatrixGestureDetector.normalizeHeight(400f, true)
        assertThat(normalizeWidth).isEqualTo(1.2499999f)
        assertThat(normalizeHeight).isEqualTo(1.25f)

        // check if the normalized width and height of the device is 2f in the range [-1,1]
        normalizeWidth = openGLMatrixGestureDetector.normalizeWidth(1080f, true)
        normalizeHeight = openGLMatrixGestureDetector.normalizeHeight(1920f, true)
        assertThat(normalizeWidth).isEqualTo(2f * (NEAR * RATIO))
        assertThat(normalizeHeight).isEqualTo(2f * NEAR)


        // apply transformations to the matrix
        openGLMatrixGestureDetector.matrix.apply {
            postRotate(20f)
            postScale(3f, 3f)
            postTranslate(100f, 200f)
        }
        openGLMatrixGestureDetector.setTransformations()
        assertThat(openGLMatrixGestureDetector.scale).isEqualTo(3.0000002f)
        assertThat(openGLMatrixGestureDetector.angle).isEqualTo(20.0f)
        assertThat(openGLMatrixGestureDetector.translate).isEqualTo(PointF(100.0f, 200.0f))

        normalizeWidth = openGLMatrixGestureDetector.normalizeWidth(400f, false)
        normalizeHeight = openGLMatrixGestureDetector.normalizeHeight(400f, false)
        assertThat(normalizeWidth).isEqualTo(0.4166666f)
        assertThat(normalizeHeight).isEqualTo(0.41666663f)

        // check transformation from the graphic to the OpenGL matrix
        val openGLMatrix = FloatArray(16)
        val openGLTransformedMatrix = FloatArray(16)
        android.opengl.Matrix.setIdentityM(openGLMatrix, 0)
        openGLMatrixGestureDetector.transform(openGLMatrix, openGLTransformedMatrix)
        assertThat(openGLTransformedMatrix).isEqualTo(
            floatArrayOf(
                2.819078f, 1.0260605f, 0.0f, 0.0f,
                -1.0260605f, 2.819078f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                1.375f, 2.375f, 0.0f, 1.0f
            )
        )

        // check conversion of point from graphic to OpenGL coordinate system, considering the previously applied transformations
        val transformedPoint = PointF()
        openGLMatrixGestureDetector.normalizeCoordinate(432.2f, 12.42f, transformedPoint)
        assertThat(transformedPoint).isEqualTo(PointF(-0.25834346f, 0.30196524f))

    }

    fun CheckStaticMathods() {

        val matrix = Matrix()
        matrix.apply {
            postRotate(20f)
            postScale(3f, 3f)
            postTranslate(100f, 200f)
        }

        // extension methods for extracting transformation from matrix
        assertThat(matrix.scale()).isEqualTo(3.0000002f)
        assertThat(matrix.angle()).isEqualTo(20.0f)
        assertThat(matrix.translate()).isEqualTo(PointF(100f, 200f))

        // check x normalization from graphic to OpenGL coordinate system
        var x = OpenGLMatrixGestureDetector.normalizeTranslateX(43.2f, false)
        assertThat(x).isEqualTo(1.5525f)

        x = OpenGLMatrixGestureDetector.normalizeTranslateX(0f, false)
        assertThat(x).isEqualTo(0.0f)

        x = OpenGLMatrixGestureDetector.normalizeTranslateX(0f, true)
        assertThat(x).isEqualTo(1.6875f)


        // check y normalization from graphic to OpenGL coordinate system
        var y = OpenGLMatrixGestureDetector.normalizeTranslateY(43.2f, false)
        assertThat(y).isEqualTo(2.865f)

        y = OpenGLMatrixGestureDetector.normalizeTranslateY(0f, false)
        assertThat(y).isEqualTo(0.0f)

        y = OpenGLMatrixGestureDetector.normalizeTranslateY(0f, true)
        assertThat(y).isEqualTo(3.0f)

        // check transformation from the graphic to the OpenGL matrix
        val openGLMatrix = FloatArray(16)
        val openGLTransformedMatrix = FloatArray(16)
        android.opengl.Matrix.setIdentityM(openGLMatrix, 0)
        OpenGLMatrixGestureDetector.transform(openGLMatrix, openGLTransformedMatrix, matrix)
        assertThat(openGLTransformedMatrix).isEqualTo(
            floatArrayOf(
                2.819078f, 1.0260605f, 0.0f, 0.0f,
                -1.0260605f, 2.819078f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                1.375f, 2.375f, 0.0f, 1.0f
            )
        )
    }
}