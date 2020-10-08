uniform vec3 g_CameraPosition;
uniform mat4 g_ViewMatrix;

uniform sampler2D m_MatCap;
uniform vec4 m_Color;

in vec3 Normal;
in vec3 Position;

out vec4 outFragColor;


#ifdef CHECKBOARD
    void checkboard(in vec3 pos,in float size,inout vec3 color){
        vec3 v = cos((pos * 2.0 * 3.14159) / vec3(size));
        vec3 p = smoothstep(vec3(-0.1), vec3(0.1), v);
        p = p * vec3(2.0) + vec3(-1.0);
        float coeff = 0.8 + 0.4 * smoothstep(-0.05, 0.05, p.x * p.y * p.z);
        color.rgb *= coeff;
    }
#endif

void main() {
    vec3 n=normalize(Normal);
    #ifndef WORLD_SPACE
        n=(g_ViewMatrix * vec4(n, 0.0) ).xyz;
    #endif
    vec2 muv =n.xy*0.5+vec2(0.5,0.5);
    outFragColor = texture(m_MatCap, vec2(muv.x,1.0-muv.y));

    #ifdef CHECKBOARD
        checkboard(Position,CHECKBOARD,outFragColor.rgb);
    #endif

}
