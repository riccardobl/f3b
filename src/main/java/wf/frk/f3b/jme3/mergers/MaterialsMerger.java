// Generated by delombok at Sat Jul 28 16:45:23 CEST 2018
package wf.frk.f3b.jme3.mergers;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import f3b.Datas.Data;
import f3b.Materials.MatProperty;
import wf.frk.f3b.jme3.F3bContext;

public class MaterialsMerger implements Merger{
	@java.lang.SuppressWarnings("all")
	private static final org.apache.logging.log4j.Logger log=org.apache.logging.log4j.LogManager.getLogger(MaterialsMerger.class);
	protected final AssetManager assetManager;
	protected Material defaultMaterial;

	public MaterialsMerger(AssetManager assetManager){
		this.assetManager=assetManager;
		//		defaultTexture = newDefaultTexture();
		//		defaultMaterial = newDefaultMaterial();
	}

	public Material newDefaultMaterial() {
		if(defaultMaterial!=null) return defaultMaterial.clone();
		else{
			Material m=new Material(assetManager,"f3b_resources/MatCap.j3md");
			m.setTexture("DiffuseMap",assetManager.loadTexture("f3b_resources/generator0.jpg"));
			m.setColor("Multiply_Color",new ColorRGBA(137.0F/255.0F,171.0F/255.0F,249.0F/255.0F,1.0F));
			m.setFloat("ChessSize",1.0F);
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
	public static class JMEMaterialSettings{
		public Material mat;
		public Number renderbucket;
}

	public static JMEMaterialSettings toJME(AssetManager assetManager,Node root, f3b.Materials.Material m) {
		JMEMaterialSettings out=new JMEMaterialSettings();
		Material mat;
		mat=new Material(assetManager,m.getMatId());
		out.mat=mat;
		mat.setName(m.hasName()?m.getName():m.getId());
		log.debug("Material loaded {}",mat.getName());
		List<MatProperty> properties=m.getPropertiesList();
		for(MatProperty p:properties){
			String name=p.getId();
			if(name.equals("FaceCullMode")){
				FaceCullMode mode=FaceCullMode.values()[((Number)(p.hasVint()?p.getVint():p.getVfloat())).intValue()];
				mat.getAdditionalRenderState().setFaceCullMode(mode);					
			}else if(name.equals("RenderBucket")){
				out.renderbucket=p.hasVint()?p.getVint():p.getVfloat();
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
							mat.setColor(name,wf.frk.f3b.jme3.ext.jme3.Vector4fExt.toColorRGBA(wf.frk.f3b.jme3.ext.f3b.TypesExt.toJME(p.getVcolor())));
						}else{
							mat.setVector4(name,wf.frk.f3b.jme3.ext.f3b.TypesExt.toJME(p.getVvec4()));
						}
						break;
					}

					case Vector3:{
						mat.setVector3(name,wf.frk.f3b.jme3.ext.f3b.TypesExt.toJME(p.getVvec3()));
						break;
					}

					case Vector2:{
						mat.setVector2(name,wf.frk.f3b.jme3.ext.f3b.TypesExt.toJME(p.getVvec2()));
						break;
					}

					case Texture2D:{
						mat.setTexture(name,wf.frk.f3b.jme3.ext.f3b.TypesExt.toJME(p.getTexture(),assetManager,root));
						break;
					}

					case TextureCubeMap:{
						mat.setTexture(name,wf.frk.f3b.jme3.ext.f3b.TypesExt.toJME(p.getTexture(),assetManager,root));
						break;
					}

					default:
				}
			}
		}
		return out;
	}

	public void apply(Data src, Node root, F3bContext context) {
		for(f3b.Materials.Material m:src.getMaterialsList()){
			JMEMaterialSettings sett;
			try{
				if(!m.hasMatId()){
					throw new Exception("Material is empty");
				}else{
					sett=toJME(assetManager,root,m);

				}
			}catch(Exception e){
				log.debug("Can\'t load material",e);
				sett=new JMEMaterialSettings();

				sett.mat=newDefaultMaterial();
				String id=m.getId();
				sett.mat.setName(m.hasName()?m.getName():m.getId());
				context.put(id,sett.mat);
				continue;
			}
			String id=m.getId();
			context.put(id,sett.mat);
			if(sett.renderbucket!=null)context.put("G~"+id+"~RenderBucket",sett.renderbucket,id);

		}
	}

	@java.lang.SuppressWarnings("all")
	public void setDefaultMaterial(final Material defaultMaterial) {
		this.defaultMaterial=defaultMaterial;
	}
}
