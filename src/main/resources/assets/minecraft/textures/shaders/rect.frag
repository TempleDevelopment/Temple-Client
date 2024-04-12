#version 120

uniform vec4 colorIn1, colorIn2, colorIn3, colorIn4, colorOut1, colorOut2, colorOut3, colorOut4;
uniform vec2 size;
uniform float roundRadius, smoothFactor, outlineWidth;

#define noise 0.3

float d(vec2 pos) {
    float r = max(roundRadius, outlineWidth);
    return length(max(abs(size * 0.5 - (pos * size)) - (size * 0.5 - r - 1.0), 0.0)) - r;
}

vec4 r(float dist) {
    return vec4(1.0, 1.0, 1.0, 1.0 - smoothstep(0.0, smoothFactor, -dist));
}

vec4 grad(vec4 c1, vec4 c2, vec4 c3, vec4 c4, vec2 pos) {
    return mix(mix(c1, c2, pos.y), mix(c3, c4, pos.y), pos.x);
}

void main() {
    vec2 pos = gl_TexCoord[0].st;

    vec4 color = grad(colorIn1, colorIn2, colorIn3, colorIn4, pos);
    vec4 outlineColor = grad(colorOut1, colorOut2, colorOut3, colorOut4, pos);

    float dist = d(pos);
    float t = clamp(smoothstep(0.0, smoothFactor, -abs(dist) + outlineWidth), 0.0, 1.0);

    gl_FragColor = mix(color, outlineColor, t) * r(-dist);
}