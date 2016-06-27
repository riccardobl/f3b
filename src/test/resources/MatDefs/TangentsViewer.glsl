#ifdef VERTEX_SHADER 


    uniform mat4 g_WorldMatrix;
    uniform mat4 g_WorldViewProjectionMatrix;
    
    attribute vec3 inPosition;
    attribute vec4 inTangent;
    attribute vec3 inNormal;
    attribute vec2 inTexCoord;
    
    
    varying vec2 texCoord;   
    varying vec4 tangent;
    varying vec3 normal;
    varying vec3 position;
    
	vec4 TransformWorld(vec4 position){
	    return g_WorldMatrix * position;
	}
    
    vec3 TransformWorldNormal(vec3 normal) {
        return normalize((g_WorldMatrix * vec4(normal,0.0)).xyz);
    }
    
    void main(){
       
    	texCoord=inTexCoord;
         normal  = TransformWorldNormal(inNormal);
        #ifdef COMPUTE_TANGENTS_IN_VERTEX
          vec3 c1 = cross(normal, vec3(0.0, 0.0, 1.0)); 
          vec3 c2 = cross(normal, vec3(0.0, 1.0, 0.0)); 
          if (length(c1) > length(c2))  tangent.xyz  = c1;	
          else   tangent.xyz  = c2;	
          tangent.xyz = normalize(tangent.xyz);
          vec3 btan=normalize(cross(normal,   tangent.xyz)); 

          if(dot(cross( normal,  tangent.xyz),btan)<0)tangent.w = -1;
          else tangent.w =1;
      #else
      	 tangent=vec4(TransformWorldNormal(inTangent.xyz),inTangent.w);   
      #endif
        position = TransformWorld(vec4(inPosition,1.)).xyz;
        gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    }
#endif

#ifdef FRAGMENT_SHADER

	#if  defined(COMPUTE_TANGENTS_IN_FRAGMENT) && __VERSION__ < 400 
		#extension OES_standard_derivatives : enable
#endif

    varying vec2 texCoord;   
    varying vec4 tangent;
    varying vec3 normal;
    varying vec3 position;

    void main(){
    	vec3 tg=tangent.xyz;
    
		#if defined(COMPUTE_TANGENTS_IN_FRAGMENT) && (defined(OES_standard_derivatives) || __VERSION__ >= 400) 
				// http://www.thetenthplanet.de/archives/1180		
			    // get edge vectors of the pixel triangle
			    vec3 dp1 = dFdx( position );
			    vec3 dp2 = dFdy( position );
			    vec2 duv1 = dFdx( texCoord );
			    vec2 duv2 = dFdy( texCoord );
			 
			    // solve the linear system
			    vec3 dp2perp = cross( dp2, normal );
			    vec3 dp1perp = cross( normal, dp1 );
			    vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
			    vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;
			 
			    // construct a scale-invariant frame 
			    float invmax = inversesqrt( max( dot(T,T), dot(B,B) ) );
			    tg.xyz=T * invmax;		
		#endif
 
        float tangentX=tg.x/2.+.5;
        float tangentY=tg.y/2.+.5;
        float tangentZ=tg.z/2.+.5;

        gl_FragColor = vec4(tangentX, tangentY, tangentZ, 1.0);
    }
#endif