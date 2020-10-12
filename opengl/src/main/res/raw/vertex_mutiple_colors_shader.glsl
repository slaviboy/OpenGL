uniform mat4 uMVPMatrix;
attribute vec4 vPosition;
attribute vec4 vColor;
varying vec4 aColor;
void main() {
    aColor = vColor;
    gl_Position = uMVPMatrix * vPosition;
}