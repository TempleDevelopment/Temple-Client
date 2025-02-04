uniform float smoothFactor;
uniform float roundRadius;
uniform vec2 size;
uniform vec4 color1, color2, color3, color4;

float alpha(vec2 p, vec2 b) {
    return length(max(abs(p) - b, 0.0)) - roundRadius;
}

void main() {
    vec2 coords = gl_TexCoord[0].st;
    vec2 centre = 0.5 * size;
    vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);

    float d = alpha(centre - (coords * size), centre - roundRadius - smoothFactor);
    float t = smoothstep(-smoothFactor, smoothFactor, d);

    gl_FragColor = vec4(color.rgb, color.a * (1.0 - t));
}
