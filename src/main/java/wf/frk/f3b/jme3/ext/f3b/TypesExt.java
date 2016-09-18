package wf.frk.f3b.jme3.ext.f3b;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;

import f3b.Meshes;
import f3b.Meshes.VertexArray;
import f3b.Types.qtr;
import f3b.Types.tx2d;
import f3b.Types.vec2;
import f3b.Types.vec3;
import f3b.Types.vec4;

public class TypesExt{

	public static Vector2f toJME(vec2 src) {
		if(src==null)return new Vector2f();
		return new Vector2f(src.getX(),src.getY());
	}

	public static Vector3f toJME(vec3 src) {
		if(src==null)return new Vector3f();
		return new Vector3f(src.getX(),src.getY(),src.getZ());
	}

	public static Vector4f toJME(vec4 src) {
		if(src==null)return new Vector4f();
		return new Vector4f(src.getX(),src.getY(),src.getZ(),src.getW());
	}

	public static Quaternion toJME(qtr src) {
		if(src==null)return new Quaternion();
		return new Quaternion(src.getX(),src.getY(),src.getZ(),src.getW());
	}



	public static Mesh.Mode toJME(Meshes.Mesh.Primitive v) {
		switch(v){
			case line_strip:
				return Mode.LineStrip;
			case lines:
				return Mode.Lines;
			case points:
				return Mode.Points;
			case triangle_strip:
				return Mode.TriangleStrip;
			case triangles:
				return Mode.Triangles;
			default:
				throw new IllegalArgumentException(String.format("doesn't support %s : %s",v==null?"?":v.getClass(),v));
		}
	}

	public static VertexBuffer.Type toJME(VertexArray.Attrib v) {
		switch(v){
			case position:
				return Type.Position;
			case normal:
				return Type.Normal;
			case tangent:
				return Type.Tangent;
			case color:
				return Type.Color;
			case texcoord:
				return Type.TexCoord;
			case texcoord2:
				return Type.TexCoord2;
			case texcoord3:
				return Type.TexCoord3;
			case texcoord4:
				return Type.TexCoord4;
			case texcoord5:
				return Type.TexCoord5;
			case texcoord6:
				return Type.TexCoord6;
			case texcoord7:
				return Type.TexCoord7;
			case texcoord8:
				return Type.TexCoord8;
			default:
				throw new IllegalArgumentException(String.format("doesn't support %s : %s",v==null?"?":v.getClass(),v));
		}
	}
	
	public static Texture toJME(tx2d t,AssetManager assetManager,Node root){
		Texture tex=null;
		String path=root.getName();
		int last_sep_i=path.lastIndexOf("/");
		if(last_sep_i==-1)path="/"+t.getRpath();
		else	path=path.substring(0,last_sep_i)+"/"+t.getRpath();
		try{
			tex=assetManager.loadTexture(path);
		}catch(AssetNotFoundException ex1){
			try{
				tex=assetManager.loadTexture(t.getRpath());

				// TODO: make an option for this in the model key
//				tex.setMagFilter(MagFilter.Bilinear);
//				tex.setMinFilter(MinFilter.Trilinear);
//				tex.setAnisotropicFilter(4);
			}catch(AssetNotFoundException ex){
//				log.warn("failed to load texture:",t.getRpath(),ex);
//				tex=defaultTexture.clone();
			}
		}
		if(tex!=null)tex.setWrap(WrapMode.Repeat);
		return tex;
	}

}
