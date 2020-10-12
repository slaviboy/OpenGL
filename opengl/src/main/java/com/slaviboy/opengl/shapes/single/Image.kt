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
package com.slaviboy.opengl.shapes.single

import com.slaviboy.opengl.main.OpenGLMatrixGestureDetector
import com.slaviboy.opengl.shapes.multiple.Images

/**
 * A 2D representation of a single image, using OpenGL ES2.0
 * @param bitmapWidth width of the bitmap in px
 * @param bitmapHeight height of the bitmap in px
 * @param x top left x coordinate in graphic coordinate system
 * @param y top left y coordinate in graphic coordinate system
 * @param width image width in graphic coordinate system
 * @param height image height in graphic coordinate system
 * @param isVisible boolean indicating whether the image should be drawn
 * @param keepSize whether to keep image size when scale is made with the gesture detector
 * @param usePositionAsCenter use the image position with the coordinates x, y as center point for the image
 * @param gestureDetector gesture detect, with the transformation that will be applied when generating the coordinates for the image
 */
class Image(
    bitmapWidth: Float,
    bitmapHeight: Float,
    x: Float = 0f,
    y: Float = 0f,
    width: Float = 100f,
    height: Float = 100f,
    textureHandle: Int = -1,
    preloadProgram: Int = -1,
    isVisible: Boolean = true,
    keepSize: Boolean = false,
    usePositionAsCenter: Boolean = true,
    gestureDetector: OpenGLMatrixGestureDetector
) : Images(bitmapWidth, bitmapHeight, floatArrayOf(x, y), width, height, isVisible, gestureDetector, keepSize, usePositionAsCenter, textureHandle, preloadProgram) {

    var x: Float = x
        set(value) {
            field = value
            positions[0] = value
            needUpdate = true
        }

    var y: Float = y
        set(value) {
            field = value
            positions[1] = value
            needUpdate = true
        }
}