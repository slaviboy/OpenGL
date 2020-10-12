/*
* Copyright (C) 2020 Stanislav Georgiev
* https://github.com/slaviboy
*
* Licensed under the Apache License, Version 2.0 (the "License")
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
package com.slaviboy.opengl.shapes.single

import android.graphics.Color
import android.opengl.GLES20
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.shapes.multiple.Shapes

/**
 * Class that draw curve that passes by given points
 * @param coordinates coordinates array holding all point coordinates in graphic coordinate system (2 coordinates x and y for each point in format [x1,y1,  x2,y2,  x3,y3,  x4,y4,...])
 * @param color color for the curve
 * @param strokeWidth stroke width for the curve
 * @param isVisible if curve should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the curve
 * @param preloadProgram  preloaded program in case we need to create curve, when the OpenGL Context is not available
 * @param isClosed if the path for the curve is closed and first and last point of the curve should be connected
 * @param tension tension for the curve
 * @param numOfSegments number of line segments that we generated between each two neighbouring points
 */
class PassByCurve(
    coordinates: FloatArray = floatArrayOf(),
    color: Int = Color.BLACK,
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    val isClosed: Boolean = false,
    val tension: Float = 0.5f,
    val numOfSegments: Int = 16
) : Shapes(getPassByCurveCoordinates(coordinates, isClosed, tension, numOfSegments), intArrayOf(color), strokeWidth, isVisible, gestureDetector, GLES20.GL_LINE_STRIP, 2, preloadProgram, true)