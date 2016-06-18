package wf.frk.f3b.mergers;


import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.shader.VarType;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import wf.frk.f3b.Merger;
import wf.frk.f3b.F3bContext;
import f3b.Datas.Data;
import f3b.Materials;
import f3b.Materials.MatProperty;
import f3b.Primitives;
import f3b.Primitives.Color;

@ExtensionMethod({wf.frk.f3b.ext.PrimitiveExt.class})

@Slf4j
public class MaterialsMerger implements Merger{
	protected final AssetManager assetManager;
	protected @Setter @Getter Texture defaultTexture;
	protected @Setter @Getter Material defaultMaterial;


	public MaterialsMerger(AssetManager assetManager) {
		this.assetManager = assetManager;
		defaultTexture = newDefaultTexture();
		defaultMaterial = newDefaultMaterial();
	}

	public Material newDefaultMaterial() {
		Material m=new Material(assetManager,"MatDefs/MatCap.j3md");
		m.setTexture("DiffuseMap",assetManager.loadTexture("Textures/generator8.jpg"));
		m.setColor("Multiply_Color",ColorRGBA.Pink);
		m.setFloat("ChessSize",0.5f);
		m.setName("DEFAULT");
		return m;
	}

	public Texture newDefaultTexture() {
		Texture t=assetManager.loadTexture("Textures/debug_8_64.png");
		t.setWrap(WrapMode.Repeat);
		t.setMagFilter(MagFilter.Nearest);
		t.setMinFilter(MinFilter.NearestLinearMipMap);
		t.setAnisotropicFilter(2);
		return t;
	}


	public void apply(Data src, Node root, F3bContext context, Logger log) {
		for(f3b.Materials.Material m:src.getMaterialsList()){
			Material mat=new Material(assetManager,m.getMatId());
			String id=m.getId();
			context.put(id,mat);
			mat.setName(m.hasName()?m.getName():m.getId());
			List<MatProperty> properties=m.getPropertiesList();
			
			for(MatProperty p:properties){
				String name=p.getId();
				if (name.equals("RenderBucket")){
					context.put("G~"+id+"~RenderBucket",p.getValue(),id);
				}else if(p.hasValue()){
					Double d=new Double(p.getValue());
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
							mat.setFloat(name,d.floatValue());
							break;
						}
						case Int:{
							mat.setInt(name,d.intValue());
							break;
						}
						case Boolean:{
							mat.setBoolean(name,d.intValue()==1);
							break;
						}
						default:
					}
					
				}else if(p.hasColor()){
					mat.setColor(name,p.getColor().toJME());
				}else if(p.hasTexture()){
					Texture tx=p.getTexture().toJME(assetManager,root);
					if(tx!=null){
						mat.setTexture(name,tx);
					}
				}else if(p.hasVec3()){
					mat.setVector3(name,p.getVec3().toJME());
				}else if(p.hasVec2()){
					mat.setVector2(name,p.getVec2().toJME());
				}
			}
		}
	}


}
