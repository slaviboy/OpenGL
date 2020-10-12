/*
* Copyright (C) 2020 Stanislav Georgiev
* https://github.com/slaviboy
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.slaviboy.openglexamples.single

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.main.OpenGLStatic
import com.slaviboy.opengl.main.OpenGLStatic.DEVICE_HALF_HEIGHT
import com.slaviboy.opengl.main.OpenGLStatic.DEVICE_HALF_WIDTH
import com.slaviboy.opengl.main.OpenGLStatic.DEVICE_HEIGHT
import com.slaviboy.opengl.main.OpenGLStatic.DEVICE_WIDTH
import com.slaviboy.opengl.shapes.multiple.Shapes.Companion.STYLE_FILL
import com.slaviboy.opengl.shapes.multiple.Shapes.Companion.STYLE_STROKE
import com.slaviboy.opengl.shapes.single.*
import com.slaviboy.openglexamples.R

/**
 * Helper class that holds and has method for drawing all available shapes on the
 * OpenGL surface view
 */
class OpenGLHelper : View.OnTouchListener {

    // matrix used for allying transformations to all OpenGL objects: line, images, triangles..
    var mainGestureDetector: OpenGLMatrixGestureDetector =
        OpenGLMatrixGestureDetector()

    lateinit var line: Line                          // OpenGL line object
    lateinit var triangle: Triangle                  // OpenGL triangle object
    lateinit var rectangle: Rectangle                // OpenGL rectangle object
    lateinit var regularPolygon: RegularPolygon      // OpenGL regular polygons object
    lateinit var circle: Circle                      // OpenGL circle object
    lateinit var image: Image                        // OpenGL image object
    lateinit var ellipse: Ellipse                    // OpenGL ellipse object
    lateinit var passByCurve: PassByCurve            // OpenGL pass by curve

    lateinit var requestRenderListener: (() -> Unit) // listener for requesting new rendering(redrawing of the scene)

    var style: Int = STYLE_STROKE                    // style for the shapes FILL or STROKE
    var singleColorsProgram: Int = -1                // preload program in case a shape need to be created when the OpenGL context is not available

    /**
     * Creates the initial shapes that are drawn, that includes image texture of a circle
     * representing the points set by the user. The programs for the different OpenGL
     * objects, and the object them selves. This method is called only from the OpenGL renderer
     * since, programs and textures can be create only when the OpenGL context is available.
     */
    fun createShapes(context: Context? = null) {

        // preload program in case a shape need to be created when the OpenGL context is not available 
        singleColorsProgram = OpenGLStatic.setSingleColorsProgram()

        if (context != null) {
            val imageProgram = OpenGLStatic.setTextureProgram()
            val textureHandler = OpenGLStatic.loadTexture(context, R.drawable.earth)
            image = Image(302f, 303f, 50f, 50f, 100f, 100f, textureHandler, imageProgram, true, true, false, mainGestureDetector)
        }
        strokeFillShapes()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        // apply finger gesture to the matrix with transformations
        mainGestureDetector.onTouchEvent(event)
        requestRenderListener.invoke()
        return true
    }

    /**
     * Update shapes with random coordinates and color
     */
    fun updateShapes() {

        val colorRange = (0..255)
        val xRange = (0..DEVICE_WIDTH.toInt())
        val yRange = (0..DEVICE_HEIGHT.toInt())
        val widthRange = (0..DEVICE_HALF_WIDTH.toInt())
        val heightRange = (0..DEVICE_HALF_HEIGHT.toInt())

        // update line coordinates
        line.x1 = xRange.random().toFloat()
        line.y1 = yRange.random().toFloat()
        line.x2 = xRange.random().toFloat()
        line.y2 = yRange.random().toFloat()

        // update color
        line.color = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())


        // update rectangle coordinates
        rectangle.x = xRange.random().toFloat()
        rectangle.y = yRange.random().toFloat()
        rectangle.width = widthRange.random().toFloat()
        rectangle.height = heightRange.random().toFloat()

        // update rectangle color
        rectangle.color = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())


        // update triangle coordinates
        triangle.x1 = xRange.random().toFloat()
        triangle.y1 = yRange.random().toFloat()
        triangle.x2 = xRange.random().toFloat()
        triangle.y2 = yRange.random().toFloat()
        triangle.x3 = xRange.random().toFloat()
        triangle.y3 = yRange.random().toFloat()

        // update triangle color
        triangle.color = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())


        // update regular polygon coordinates
        regularPolygon.x = xRange.random().toFloat()
        regularPolygon.y = yRange.random().toFloat()
        regularPolygon.radius = widthRange.random().toFloat() / 4
        regularPolygon.angle = (0..360).random().toFloat()

        // update regular polygon color
        regularPolygon.color = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())

        // update the number of vertices for the polygon
        regularPolygon.numberOfVertices = (3..10).random()


        // update circle coordinates
        circle.x = xRange.random().toFloat()
        circle.y = yRange.random().toFloat()
        circle.radius = widthRange.random().toFloat() / 4

        // update circle color
        circle.color = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())


        // update ellipse coordinates
        ellipse.x = xRange.random().toFloat()
        ellipse.y = yRange.random().toFloat()
        ellipse.rx = widthRange.random().toFloat() / 4
        ellipse.ry = heightRange.random().toFloat() / 4

        // update ellipse color
        ellipse.color = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())


        // update image coordinates
        image.x = xRange.random().toFloat()
        image.y = yRange.random().toFloat()

        // generate new random coordinates for the curve
        val curveCoordinates = ArrayList<Float>()
        var x = 10
        val yRand = yRange.random()
        for (i in 0 until 25) {
            val y = Math.floor((Math.random() * 200) + yRand)
            curveCoordinates.add(x.toFloat())
            curveCoordinates.add(y.toFloat())
            x += 40
        }
        passByCurve.setShape(curveCoordinates.toFloatArray(), Color.rgb(colorRange.random(), colorRange.random(), colorRange.random()))


        // request redrawing of the scene
        requestRenderListener.invoke()
    }

    fun strokeFillShapes() {

        style = if (style == STYLE_FILL) {
            STYLE_STROKE
        } else {
            STYLE_FILL
        }

        // create shapes
        line = Line(100f, 800f, 300f, 800f, Color.RED, 10f, true, mainGestureDetector, singleColorsProgram)
        circle = Circle(700f, 700f, 110f, Color.MAGENTA, 5f, true, mainGestureDetector, singleColorsProgram, style)
        ellipse = Ellipse(800f, 280f, 50f, 200f, Color.CYAN, 5f, true, mainGestureDetector, singleColorsProgram, style)
        rectangle = Rectangle(300f, 200f, 300f, 150f, Color.BLUE, 5f, true, mainGestureDetector, singleColorsProgram, style)
        triangle = Triangle(40f, 550f, 200f, 350f, 310f, 650f, Color.RED, 5f, true, mainGestureDetector, singleColorsProgram, style)
        regularPolygon = RegularPolygon(500f, 500f, 100f, 0f, Color.GREEN, 5f, true, mainGestureDetector, singleColorsProgram, style, 6)
        passByCurve = PassByCurve(
            floatArrayOf(
                10.0f, 1042.0f, 50.0f, 1012.0f, 90.0f, 951.0f, 130.0f, 943.0f, 170.0f, 939.0f, 210.0f, 1099.0f, 250.0f, 1021.0f,
                290.0f, 1085.0f, 330.0f, 1032.0f, 370.0f, 912.0f, 410.0f, 983.0f, 450.0f, 927.0f, 490.0f, 1021.0f, 530.0f, 935.0f,
                570.0f, 976.0f, 610.0f, 1063.0f, 650.0f, 1055.0f, 690.0f, 1089.0f, 730.0f, 1022.0f, 770.0f, 1052.0f, 810.0f,
                950.0f, 850.0f, 920.0f, 890.0f, 925.0f, 930.0f, 1047.0f, 970.0f, 993.0f
            ), Color.BLUE, 5f, true, mainGestureDetector, singleColorsProgram, false, 1f, 40
        )

        requestRenderListener.invoke()
    }

    /**
     * Method called when the shapes need to be redrawn, with the responsible OpenGL matrix values, that are applied by the
     * user from his finger gestures.
     * @param transformedMatrixOpenGL OpenGL matrix values, for transformation applied to all shapes
     */
    fun draw(transformedMatrixOpenGL: FloatArray) {

        // draw shapes
        line.draw(transformedMatrixOpenGL)
        circle.draw(transformedMatrixOpenGL)
        ellipse.draw(transformedMatrixOpenGL)
        rectangle.draw(transformedMatrixOpenGL)
        triangle.draw(transformedMatrixOpenGL)
        regularPolygon.draw(transformedMatrixOpenGL)
        image.draw(transformedMatrixOpenGL)
        passByCurve.draw(transformedMatrixOpenGL)
    }

}
