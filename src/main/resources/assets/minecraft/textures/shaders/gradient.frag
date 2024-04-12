#version 120

uniform sampler2D texture;
uniform vec3 rgb;
uniform vec3 rgb1;
uniform float step;
uniform float offset;
uniform float mix;

void main() {
    float alpha =texture2D(texture, gl_TexCoord[0].xy).a;
    if (alpha != 0.0f) {
        float distance = sqrt(gl_FragCoord.x * gl_FragCoord.x + gl_FragCoord.y * gl_FragCoord.y) + offset;

        distance = distance / step;

        distance = ((sin(distance) + 1.0) / 2.0);

        float distanceInv = 1 - distance;
        float r = rgb.r * distance + rgb1.r * distanceInv;
        float g = rgb.g * distance + rgb1.g * distanceInv;
        float b = rgb.b * distance + rgb1.b * distanceInv;
        gl_FragColor = vec4(r, g, b, mix);
    }
}