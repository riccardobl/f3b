package wf.frk.f3b.jme3.mergers;


import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

import f3b.Datas.Data;
import f3b.Materials.MatProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import wf.frk.f3b.jme3.F3bContext;

@ExtensionMethod({wf.frk.f3b.jme3.ext.f3b.TypesExt.class,wf.frk.f3b.jme3.ext.jme3.Vector4fExt.class})
@Log4j2
public class MaterialsMerger implements Merger{
	protected final AssetManager assetManager;
	protected @Setter  Material defaultMaterial;


	public MaterialsMerger(AssetManager assetManager) {
		this.assetManager = assetManager;
//		defaultTexture = newDefaultTexture();
//		defaultMaterial = newDefaultMaterial();
	}
	
	public Material newDefaultMaterial() {
		if(defaultMaterial!=null)return defaultMaterial.clone();
		else{
			Material m=new Material(assetManager,"f3b_resources/MatCap.j3md");
			m.setTexture("DiffuseMap",assetManager.loadTexture("f3b_resources/generator8.jpg"));
			m.setColor("Multiply_Color",ColorRGBA.Pink);
			m.setFloat("ChessSize",0.5f);
			m.setName("DEFAULT");
			return m;
		}
	}

//	public Material newDefaultMaterial() {
//		Material m=new Material(assetManager,"MatDefs/MatCap.j3md");
//		m.setTexture("DiffuseMap",assetManager.loadTexture("Textures/generator8.jpg"));
//		m.setColor("Multiply_Color",ColorRGBA.Pink);
//		m.setFloat("ChessSize",0.5f);
//		m.setName("DEFAULT");
//		return m;
//	}

//	public Texture newDefaultTexture() {
//		Texture t=assetManager.loadTexture("Textures/debug_8_64.png");
//		t.setWrap(WrapMode.Repeat);
//		t.setMagFilter(MagFilter.Nearest);
//		t.setMinFilter(MinFilter.NearestLinearMipMap);
//		t.setAnisotropicFilter(2);
//		return t;
//	}


	public void apply(Data src, Node root, F3bContext context) {
		for(f3b.Materials.Material m:src.getMaterialsList()){
			Material mat;
			try{
				if(!m.hasMatId()){
					throw new Exception("Material is empty");
				}else{
					mat=new Material(assetManager,m.getMatId());
				}
			}catch(Exception e){
				log.debug("Can't load material",e);
				mat=newDefaultMaterial();
				String id=m.getId();
				mat.setName(m.hasName()?m.getName():m.getId());
				context.put(id,mat);
				continue;
			}
		
			String id=m.getId();
			context.put(id,mat);
			
			mat.setName(m.hasName()?m.getName():m.getId());
			log.debug("Material loaded {}",mat.getName());

			List<MatProperty> properties=m.getPropertiesList();
			
			for(MatProperty p:properties){
				String name=p.getId();
				if (name.equals("RenderBucket")){
					context.put("G~"+id+"~RenderBucket",p.hasVint()?p.getVint():p.getVfloat(),id);
				}else{
					Collection<MatParam> params=mat.getMaterialDef().getMaterialParams();
					MatParam param=null;

					for(MatParam pr:params){
						if(pr.getName().equals(name)){
							param=pr;
							break;
						}
					}			
					if(param==null){
						log.warn("Parameter {}  is not available for material  {}. Skip.",name,m.getMatId());
						StringBuilder sb=new StringBuilder();
						sb.append("Available parameters:\n");
						for(Entry<String,MatParam> e:mat.getParamsMap().entrySet()){
							sb.append(e.getKey()).append(", ");
						}
						log.warn(sb.toString());
						continue;
					}
					switch(param.getVarType()){
						case Float:{
							mat.setFloat(name,(float)(p.hasVfloat()?p.getVfloat():p.hasVint()?p.getVint():p.getVbool()?1:0));
							break;
						}
						case Int:{
							mat.setInt(name,(int)(p.hasVint()?p.getVint():p.hasVfloat()?p.getVfloat():p.getVbool()?1:0));
							break;
						}
						case Boolean:{
							mat.setBoolean(name,p.hasVbool()?p.getVbool():p.hasVint()?p.getVint()==1:p.getVfloat()==1);
							break;
						}
						case Vector4:{
							if(p.hasVcolor()){
								mat.setColor(name,p.getVcolor().toJME().toColorRGBA());
							}else{
								mat.setVector4(name,p.getVvec4().toJME());
							}	
							break;
						}
						case Vector3:{
							mat.setVector3(name,p.getVvec3().toJME());
							break;
						}
						case Vector2:{
							mat.setVector2(name,p.getVvec2().toJME());
							break;
						}
						case Texture2D:{
							mat.setTexture(name,p.getTexture().toJME(assetManager,root));
							break;
						}
						case TextureCubeMap:{
							mat.setTexture(name,p.getTexture().toJME(assetManager,root));
							break;
						}
						default:
					}					
				}
			}
		}
	}


}
