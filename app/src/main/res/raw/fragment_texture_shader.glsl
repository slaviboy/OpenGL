precision mediump float;
uniform vec4 vColor;
uniform sampler2D u_Texture;
varying vec2 v_TexCoordinate;
void main() {
  gl_FragColor = (vColor * texture2D(u_Texture, v_TexCoordinate));
}