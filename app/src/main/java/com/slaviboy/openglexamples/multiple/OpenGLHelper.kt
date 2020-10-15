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
package com.slaviboy.openglexamples.multiple

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
import com.slaviboy.opengl.shapes.multiple.*
import com.slaviboy.opengl.shapes.Shapes.Companion.STYLE_FILL
import com.slaviboy.openglexamples.R

/**
 * Helper class that has method for drawing all available shapes
 */
class OpenGLHelper : View.OnTouchListener {

    // matrix used for allying transformations to all OpenGL objects: line, images, triangles..
    var mainGestureDetector: OpenGLMatrixGestureDetector = OpenGLMatrixGestureDetector()

    lateinit var lines: Lines                             // OpenGL lines object
    lateinit var triangles: Triangles                     // OpenGL triangles object
    lateinit var rectangles: Rectangles                   // OpenGL rectangles object
    lateinit var regularPolygons: RegularPolygons         // OpenGL regular polygons object
    lateinit var circles: Circles                         // OpenGL circles object
    lateinit var images: Images                           // OpenGL images object

    lateinit var requestRenderListener: (() -> Unit)      // listener for requesting new rendering(redrawing of the scene)

    /**
     * Creates the initial shapes that are drawn, that includes image texture of a circle
     * representing the points set by the user. The programs for the different OpenGL
     * objects, and the object them selves. This method is called only from the OpenGL renderer
     * since, programs and textures can be create only when the OpenGL context is available.
     */
    fun createShapes(context: Context) {

        // get the program for the OpenGL objects
        val multipleColorProgram = OpenGLStatic.setMultipleColorsProgram()
        val singleColorProgram = OpenGLStatic.setSingleColorsProgram()
        val textureProgram = OpenGLStatic.setTextureProgram()

        // initialize large number of shapes
        //initLines(singleColorProgram)
        //initRectangles(multipleColorProgram)
        //initTriangles(multipleColorProgram)
        //initRegularPolygons(multipleColorProgram)
        //initCircles(multipleColorProgram)
        initImages(textureProgram, context)

        // test methods for testing different methods
        /*testRectangles(program)
        testCircles(program)
        testLines(program)
        testTriangles(program)
        testRegularPolygon(program)
        testImages(program, context)*/
    }

    fun initLines(program: Int) {

        val xRange = (0 until DEVICE_WIDTH.toInt())
        val yRange = (0 until DEVICE_HEIGHT.toInt())
        val numberOfElements = 2000
        val coordinates = FloatArray(numberOfElements * 2)
        for (i in 0 until numberOfElements) {
            coordinates[i * 2] = xRange.random().toFloat()
            coordinates[i * 2 + 1] = yRange.random().toFloat()
        }

        // set object for the OpenGL lines
        lines = Lines(
            // in format: [x1,y1,x2,y2, x3,y3,x4,y4,...]
            coordinates = coordinates,
            colors = intArrayOf(Color.RED),
            strokeWidth = 1f,
            preloadProgram = program,
            gestureDetector = mainGestureDetector,
            useSingleColor = true
        )
    }

    fun initRectangles(program: Int) {

        val colorRange = (0..255)
        val xRange = (0 until DEVICE_WIDTH.toInt())
        val yRange = (0 until DEVICE_HEIGHT.toInt())
        val widthRange = (0 until DEVICE_HALF_WIDTH.toInt() / 5)
        val heightRange = (0 until DEVICE_HALF_HEIGHT.toInt() / 5)

        val numberOfElements = 2000
        val coordinatesInfo = FloatArray(numberOfElements * 4)
        val colors = IntArray(numberOfElements)
        for (i in 0 until numberOfElements) {

            // set random coordinates info for the rectangle
            coordinatesInfo[i * 4] = xRange.random().toFloat()
            coordinatesInfo[i * 4 + 1] = yRange.random().toFloat()
            coordinatesInfo[i * 4 + 2] = widthRange.random().toFloat()
            coordinatesInfo[i * 4 + 3] = heightRange.random().toFloat()

            // set random colors for the rectangle
            colors[i] = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())
        }

        // set object for the OpenGL rectangles
        rectangles = Rectangles(
            // in format: [x1,y1,width1,height1, x2,y2,width2,height2,...]
            coordinatesInfo = coordinatesInfo,
            colors = colors,
            style = STYLE_FILL,
            strokeWidth = 1f,
            preloadProgram = program,
            gestureDetector = mainGestureDetector
        )
    }

    fun initTriangles(program: Int) {

        val colorRange = (0..255)
        val xRange = (0 until DEVICE_WIDTH.toInt())
        val yRange = (0 until DEVICE_HEIGHT.toInt())

        val numberOfElements = 2000
        val coordinates = FloatArray(numberOfElements * 6)
        val colors = IntArray(numberOfElements)
        for (i in 0 until numberOfElements) {

            // set random coordinates info for the triangles
            coordinates[i * 6] = xRange.random().toFloat()
            coordinates[i * 6 + 1] = yRange.random().toFloat()
            coordinates[i * 6 + 2] = xRange.random().toFloat()
            coordinates[i * 6 + 3] = yRange.random().toFloat()
            coordinates[i * 6 + 4] = xRange.random().toFloat()
            coordinates[i * 6 + 5] = yRange.random().toFloat()

            // set random colors for the triangles
            colors[i] = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())
        }

        // set object for the OpenGL triangles
        triangles = Triangles(
            // in format: [x1,y1,x2,y2,x3,y3,  x4,y4,x5,y5,x6,y6...]
            coordinates = coordinates,
            colors = colors,
            style = STYLE_FILL,
            strokeWidth = 1f,
            preloadProgram = program,
            gestureDetector = mainGestureDetector
        )
    }

    fun initRegularPolygons(program: Int) {

        val colorRange = (0..255)
        val xRange = (0 until DEVICE_WIDTH.toInt())
        val yRange = (0 until DEVICE_HEIGHT.toInt())
        val radiusRange = (0 until DEVICE_HALF_WIDTH.toInt() / 10)
        val startAngleRange = (0..360)

        val numberOfElements = 2000
        val coordinatesInfo = FloatArray(numberOfElements * 4)
        val colors = IntArray(numberOfElements)
        for (i in 0 until numberOfElements) {

            // set random coordinates info for the regular polygons
            coordinatesInfo[i * 4] = xRange.random().toFloat()
            coordinatesInfo[i * 4 + 1] = yRange.random().toFloat()
            coordinatesInfo[i * 4 + 2] = radiusRange.random().toFloat()
            coordinatesInfo[i * 4 + 3] = startAngleRange.random().toFloat()

            // set random colors for the regular polygons
            colors[i] = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())
        }

        // set object for the OpenGL regular polygons
        regularPolygons = RegularPolygons(
            // in format: [cx1,cy1,radius1,angle1,  cx2,cy2,radius2,angle2,...]
            coordinatesInfo = coordinatesInfo,
            colors = colors,
            style = STYLE_FILL,
            strokeWidth = 1f,
            numberOfVertices = 5,
            preloadProgram = program,
            gestureDetector = mainGestureDetector
        )
    }

    fun initCircles(program: Int) {

        val colorRange = (0..255)
        val xRange = (0 until DEVICE_WIDTH.toInt())
        val yRange = (0 until DEVICE_HEIGHT.toInt())
        val radiusRange = (0 until (DEVICE_HALF_WIDTH / 5).toInt())

        val numberOfElements = 400
        val coordinatesInfo = FloatArray(numberOfElements * 4)
        val colors = IntArray(numberOfElements)
        for (i in 0 until numberOfElements) {

            // set random coordinates info for the circles
            coordinatesInfo[i * 4] = xRange.random().toFloat()
            coordinatesInfo[i * 4 + 1] = yRange.random().toFloat()
            coordinatesInfo[i * 4 + 2] = radiusRange.random().toFloat()
            coordinatesInfo[i * 4 + 3] = 0f

            // set random colors for the circles
            colors[i] = Color.rgb(colorRange.random(), colorRange.random(), colorRange.random())
        }

        // set object for the OpenGL circles
        circles = Circles(
            // in format: [cx1,cy1,radius1,angle1,  cx2,cy2,radius2,angle2,...]
            coordinatesInfo = coordinatesInfo,
            colors = colors,
            style = STYLE_FILL,
            strokeWidth = 1f,
            preloadProgram = program,
            gestureDetector = mainGestureDetector
        )
    }

    fun initImages(program: Int, context: Context) {

        val textureHandler = OpenGLStatic.loadTexture(context, R.drawable.earth)

        val xRange = (0 until DEVICE_WIDTH.toInt())
        val yRange = (0 until DEVICE_HEIGHT.toInt())

        val numberOfElements = 1000
        val coordinates = FloatArray(numberOfElements * 4)
        for (i in 0 until numberOfElements) {

            // set random coordinates info for the images
            coordinates[i * 2] = xRange.random().toFloat()
            coordinates[i * 2 + 1] = yRange.random().toFloat()
        }

        // set object for the OpenGL images
        images = Images(
            // in format: [x1,y1, x2,y2, x3,y3,...]
            positions = coordinates,
            preloadProgram = program,
            textureHandle = textureHandler,
            gestureDetector = mainGestureDetector,
            bitmapWidth = 302f,
            bitmapHeight = 303f,
            width = 25f,
            height = 25f
        )
    }

    fun testLines(program: Int) {

        // set object for the OpenGL lines
        lines = Lines(

            // in format: [x1,y1,x2,y2, x3,y3,x4,y4,...]
            coordinates = floatArrayOf(
                0f, 0f, 200f, 200f,
                200f, 200f, 400f, 200f,
                400f, 200f, 400f, 400f
            ),
            colors = intArrayOf(
                Color.RED, Color.BLUE, Color.GREEN
            ),
            strokeWidth = 11f,
            preloadProgram = program,
            gestureDetector = mainGestureDetector
        )

        val a = mainGestureDetector.normalizeCoordinate(200f, 200f)
        val b = mainGestureDetector.normalizeCoordinate(400f, 200f)

        lines.delete(1)

        lines.add(1, Color.BLACK, 200f, 200f, 400f, 200f)

        lines.addOpenGL(2, Color.YELLOW, a.x, a.y, b.x, b.y)

        lines.change(2, 400f, 400f, 700f, 400f)

        lines.changeOpenGL(2, a.x, a.y, b.x, b.y)

        lines.setShape(
            floatArrayOf(
                0f, 0f, 200f, 200f,
                400f, 200f, 400f, 400f
            ),
            intArrayOf(Color.CYAN, Color.MAGENTA)
        )

        lines.setShapeOpenGL(
            floatArrayOf(
                a.x, a.y, b.x, b.y
            ),
            intArrayOf(Color.CYAN)
        )
    }

    fun testTriangles(program: Int) {

        // set object for the OpenGL triangles
        triangles = Triangles(

            // in format: [x1,y1,x2,y2,x3,y3,  x4,y4,x5,y5,x6,y6...]
            floatArrayOf(
                490f, 380f, 100f, 540f, 500f, 600f,  // first triangle coordinates
                800f, 200f, 750f, 600f, 400f, 900f,  // second triangle coordinates
                900f, 600f, 950f, 700f, 400f, 900f   // third triangle coordinates
            ),
            intArrayOf(
                Color.RED,   // first triangle color
                Color.GREEN, // second triangle color
                Color.YELLOW // third triangle color
            ),
            style = STYLE_FILL,
            preloadProgram = program,
            gestureDetector = mainGestureDetector
        )

        triangles.delete(1)

        triangles.add(2, Color.BLACK, 0f, 0f, 300f, 0f, 300f, 300f)

        triangles.addOpenGL(3, Color.BLUE, 0f, 0f, -3f, 0f, -3f, -3f)

        triangles.change(3, 490f, 380f, 100f, 540f, 500f, 600f)

        triangles.changeOpenGL(0, 0f, 0f, -3f, 0f, -3f, -3f)

        triangles.setShape(
            floatArrayOf(
                490f, 380f, 100f, 540f, 500f, 600f,
                900f, 600f, 950f, 700f, 400f, 900f
            ),
            intArrayOf(Color.MAGENTA, Color.CYAN)
        )
    }

    fun testRectangles(program: Int) {

        // set object for the OpenGL rectangles
        rectangles = Rectangles(

            // in format: [x1,y1,width1,height1, x2,y2,width2,height2,...]
            coordinatesInfo = floatArrayOf(
                0f, 0f, 200f, 200f,
                400f, 400f, 500f, 100f,
                200f, 600f, 200f, 100f
            ),
            colors = intArrayOf(
                Color.RED,
                Color.GREEN,
                Color.GRAY
            ),
            style = STYLE_FILL,
            strokeWidth = 11f,
            preloadProgram = program,
            gestureDetector = mainGestureDetector
        )

        rectangles.delete(1)

        rectangles.add(2, Color.BLUE, 400f, 400f, 500f, 100f)

        rectangles.addOpenGL(1, Color.YELLOW, 0f, 0f, -1f, -3f)

        rectangles.change(0, 400f, 0f, 500f, 100f)

        rectangles.changeOpenGL(0, 0f, 0f, 1f, -3f)

        rectangles.setShape(
            floatArrayOf(
                0f, 0f, 200f, 200f,
                200f, 600f, 200f, 100f
            ),
            intArrayOf(
                Color.BLACK,
                Color.BLUE
            )
        )
    }

    fun testRegularPolygon(program: Int) {

        // set object for the OpenGL regular polygons
        regularPolygons = RegularPolygons(

            // format : [cx1,cy1,radius1,angle1,  cx2,cy2,radius2,angle2,...]
            coordinatesInfo = floatArrayOf(
                600f, 200f, 100f, 90f,
                550f, 700f, 60f, 45f,
                200f, 400f, 180f, 0f
            ),
            colors = intArrayOf(
                Color.BLUE,
                Color.GREEN,
                Color.GRAY
            ),
            style = STYLE_FILL,
            numberOfVertices = 6,
            strokeWidth = 11f,
            preloadProgram = program,
            gestureDetector = mainGestureDetector
        )

        regularPolygons.delete(1)

        regularPolygons.add(0, Color.BLACK, 750f, 450f, 60f, 45f)

        regularPolygons.addOpenGL(3, Color.GREEN, 0f, 0f, 0.2f, 0f)

        regularPolygons.change(3, 750f, 800f, 160f, 45f)

        regularPolygons.changeOpenGL(3, 0f, 0f, 0.5f, 0f)

        regularPolygons.setShape(
            floatArrayOf(
                550f, 700f, 60f, 45f,
                200f, 400f, 180f, 0f
            ),
            intArrayOf(
                Color.RED,
                Color.YELLOW
            )
        )
    }

    fun testCircles(program: Int) {

        // set object for the OpenGL circles
        circles = Circles(
            coordinatesInfo = floatArrayOf(
                130f, 130f, 100f, 0f,
                400f, 400f, 50f, 0f,
                700f, 200f, 150f, 0f
            ),
            colors = intArrayOf(
                Color.RED,
                Color.BLACK,
                Color.MAGENTA
            ),
            style = STYLE_FILL,
            strokeWidth = 11f,
            preloadProgram = program,
            gestureDetector = mainGestureDetector
        )

        circles.delete(1)

        circles.add(1, Color.GREEN, 200f, 880f, 80f, 0f)

        circles.addOpenGL(3, Color.BLACK, 0f, 0f, 0.5f, 0f)

        circles.change(3, 400f, 400f, 50f, 0f)

        circles.changeOpenGL(3, 0f, 0f, 0.6f, 0f)
    }

    fun testImages(program: Int, context: Context) {

        val tenPercentWidth = OpenGLStatic.DEVICE_WIDTH / 10f
        val textureHandler = OpenGLStatic.loadTexture(context, R.drawable.earth)
        val program = OpenGLStatic.setTextureProgram()

        var coordinates = FloatArray(5000 * 2)
        for (i in 0 until coordinates.size / 2) {
            coordinates[i * 2] = (0..DEVICE_WIDTH.toInt()).random().toFloat()
            coordinates[i * 2 + 1] = (0..DEVICE_HEIGHT.toInt()).random().toFloat()
        }
        coordinates = floatArrayOf(
            100f, 100f,
            300f, 300f
        )

        // set object for the OpenGL images
        images = Images(
            bitmapWidth = 100f,
            bitmapHeight = 100f,
            positions = coordinates,
            //sizes =
            preloadProgram = program,
            textureHandle = textureHandler,
            gestureDetector = mainGestureDetector
        )

    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        mainGestureDetector.onTouchEvent(event)
        requestRenderListener.invoke()
        return true
    }

    /**
     * Method called when the shapes need to be redrawn, with the responsible OpenGL matrix values, that are applied by the
     * user from his finger gestures.
     * @param transformedMatrixOpenGL OpenGL matrix values, for transformation applied to all shapes
     */
    fun draw(transformedMatrixOpenGL: FloatArray) {

        // draw lines
        if (::lines.isInitialized) {
            lines.draw(transformedMatrixOpenGL)
        }

        // draw triangles
        if (::triangles.isInitialized) {
            triangles.draw(transformedMatrixOpenGL)
        }

        // draw rectangles
        if (::rectangles.isInitialized) {
            rectangles.draw(transformedMatrixOpenGL)
        }

        // draw regular polygons
        if (::regularPolygons.isInitialized) {
            regularPolygons.draw(transformedMatrixOpenGL)
        }

        // draw circles
        if (::circles.isInitialized) {
            circles.draw(transformedMatrixOpenGL)
        }

        if (::images.isInitialized) {
            images.draw(transformedMatrixOpenGL)
        }
    }

    fun updateShapes() {

    }
}
