
#ifdef VERTEX_SHADER 
    uniform mat4 g_WorldMatrix;
    uniform mat4 g_WorldViewProjectionMatrix;
    
    attribute vec3 inPosition;
    attribute vec4 inTangent;
    attribute vec3 inNormal;
    
    varying vec4 tangent;
    varying vec3 normal;

    vec3 TransformWorldNormal(vec3 normal) {
        return normalize((g_WorldMatrix * vec4(normal,0.0)).xyz);
    }
    void main(){
        tangent=vec4(TransformWorldNormal(inTangent.xyz),inTangent.w);
        normal  = TransformWorldNormal(inNormal);
        gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    }
#endif

#ifdef FRAGMENT_SHADER
    varying vec4 tangent;
    varying vec4 normal;

    void main(){
        float tangentX=tangent.x/2.+.5;
        float tangentY=tangent.y/2.+.5;
        float tangentZ=tangent.z/2.+.5;
        float parity=tangent.w/2.+.5;   

        gl_FragColor = vec4(tangentX, tangentY, tangentZ, 1.0);
    }
#endif