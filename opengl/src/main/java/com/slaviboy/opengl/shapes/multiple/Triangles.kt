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

import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector

/**
 * A 2D representation of multiple triangles, using OpenGL 2.0
 * @param coordinates array holding all vertices coordinates in graphic coordinate system(3 points, each with 2 coordinates x and y that gives total of 6 values per triangle [x1,y1,x2,y2,x3,y3,  x4,y4,x5,y5,x6,y6...])
 * @param colors array with integer representation of the color for each triangle (1 integer value per triangle)
 * @param strokeWidth stroke width for the triangles in case the style is set to STYLE_STROKE
 * @param isVisible if triangles should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the triangles
 * @param preloadProgram preloaded program in case we need to create a triangles, when the OpenGL Context is not available
 * @param style style for the triangles if it should be filled with the color or stroked
 * @param useSingleColor if single color will be set to all shapes, instead of passing array with colors for each shape separately
 */
open class Triangles(
    coordinates: FloatArray = floatArrayOf(),
    colors: IntArray = intArrayOf(),
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    val style: Int = STYLE_FILL,
    useSingleColor: Boolean = false
) : Shapes(getTrianglesCoordinatesByStyle(style, coordinates), colors, strokeWidth, isVisible, gestureDetector, getTypeByStyle(style), getTrianglesNumberOfVerticesFromStyle(style), preloadProgram, useSingleColor)

