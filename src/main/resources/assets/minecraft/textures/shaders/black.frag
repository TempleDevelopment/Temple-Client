#version 120

uniform sampler2D texture;

void main() {
    float alpha =texture2D(texture, gl_TexCoord[0].xy).a;
    if (alpha != 0f) {
        gl_FragColor = vec4(0, 0, 0, 0.5);
    }
}