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
package com.slaviboy.opengl.shapes.multiple

import android.opengl.GLES20
import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.shapes.Shapes

/**
 * A 2D representation of multiple lines, using OpenGL ES2.0
 * @param coordinates coordinates array holding all point coordinates in graphic coordinate system(2 points with 2 coordinates x and y gives 4 values per line [x1,y1,x2,y2, x3,y3,x4,y4,...])
 * @param colors array with integer representation of a color for each line (1 integer value per line)
 * @param strokeWidth stroke width for all the line
 * @param isVisible if lines should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the lines
 * @param preloadProgram  preloaded program in case we need to create lines, when the OpenGL Context is not available
 * @param useSingleColor if single color will be set to all shapes, instead of passing array with colors for each shape separately
 */
open class Lines(
    coordinates: FloatArray = floatArrayOf(),
    colors: IntArray = intArrayOf(),
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    useSingleColor: Boolean = false
) : Shapes(coordinates, colors, strokeWidth, isVisible, gestureDetector, GLES20.GL_LINES, 2, preloadProgram, useSingleColor)

