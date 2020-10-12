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
 * A 2D rectangles for use as a drawn object using OpenGL 2.0
 * @param coordinatesInfo array holding all point coordinates in graphic coordinate system(1 points with 2 coordinates x and y of the
 * top-left corner of the rectangle and the width and height of the rectangle which gives 4 values in total per rectangle [x1,y1,width1,height1, x2,y2,width2,height2,...])
 * @param colors array with integer representation of the color for each rectangle (1 integer value per rectangle)
 * @param strokeWidth stroke width for the rectangles in case the style is set to STYLE_STROKE
 * @param isVisible if rectangles should be drawn, or not when the methods draw() is called
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the rectangles
 * @param preloadProgram preloaded program in case we need to create rectangles, when the OpenGL Context is not available
 * @param style style for the rectangles if it should be filled with the color or stroked
 * @param useSingleColor if single color will be set to all shapes, instead of passing array with colors for each shape separately
 */
open class Rectangles(
    coordinatesInfo: FloatArray = floatArrayOf(),
    colors: IntArray = intArrayOf(),
    strokeWidth: Float = 1f,
    isVisible: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector,
    preloadProgram: Int = -1,
    val style: Int = STYLE_FILL,
    useSingleColor: Boolean = false
) : Shapes(getRectanglesCoordinatesByStyle(style, coordinatesInfo), colors, strokeWidth, isVisible, gestureDetector, getTypeByStyle(style), getRectanglesNumberOfVerticesFromStyle(style), preloadProgram, useSingleColor)

