#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"

in vec3 inPosition;
in vec3 inNormal;

out vec3 Normal;
out vec3 Position;

void main() {

   vec4 modelSpacePos = vec4(inPosition, 1.0);
   vec3 modelSpaceNormal = inNormal;
    Position = modelSpacePos.xyz;

    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos, modelSpaceNormal);
    #endif
   
   Normal = normalize(TransformWorldNormal(modelSpaceNormal.xyz));
    
    vec4 pos = TransformWorld(modelSpacePos);
    
    gl_Position=g_ViewProjectionMatrix*pos;

}
