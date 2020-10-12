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
package  com.slaviboy.openglexamples.single

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.slaviboy.opengl.main.OpenGLConfigChooser
import com.slaviboy.opengl.main.OpenGLStatic.ENABLE_ALPHA
import com.slaviboy.opengl.main.OpenGLStatic.ENABLE_ANTIALIASING

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
class OpenGLSurfaceView : GLSurfaceView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    val openGLRenderer: OpenGLRenderer
    var openGLHelper: OpenGLHelper

    init {

        if (ENABLE_ALPHA) {
            holder.setFormat(PixelFormat.TRANSLUCENT)
        }

        // create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2)

        // fix for error No Config chosen
        if (ENABLE_ANTIALIASING) {
            setEGLConfigChooser(OpenGLConfigChooser())
        } else {
            this.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        }

        // create the helper object
        openGLHelper = OpenGLHelper()
        this.setOnTouchListener(openGLHelper)

        // set the Renderer for drawing on the GLSurfaceView
        openGLRenderer = OpenGLRenderer(context, openGLHelper) {
            requestRender()
        }
        setRenderer(openGLRenderer)

        // render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
    }

}