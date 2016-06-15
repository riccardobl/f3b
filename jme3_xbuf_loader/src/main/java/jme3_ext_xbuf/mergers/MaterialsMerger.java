package jme3_ext_xbuf.mergers;


import java.util.Collection;
import java.util.List;

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

import jme3_ext_xbuf.Merger;
import jme3_ext_xbuf.XbufContext;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import xbuf.Datas.Data;
import xbuf.Materials;
import xbuf.Materials.MatProperty;
import xbuf.Primitives;
import xbuf.Primitives.Color;
import xbuf.Primitives.Texture2DInline;

@ExtensionMethod({jme3_ext_xbuf.ext.PrimitiveExt.class})

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

	public void apply(Data src, Node root, XbufContext context, Logger log) {
		// TODO: Reimplement inline textures
		for(xbuf.Materials.Material m:src.getMaterialsList()){
			Material mat=new Material(assetManager,m.getMatId());
			String id=m.getId();
			context.put(id,mat);
			mat.setName(m.hasName()?m.getName():m.getId());
			List<MatProperty> properties=m.getPropertiesList();
			
			for(MatProperty p:properties){
				String name=p.getId();
				
				if(p.hasValue()){
					Double d=new Double(p.getValue());
					MatParam param=mat.getParam(name);
					
					switch(param.getVarType()){
						case Float:{
							param.setValue(d.floatValue());
							break;
						}
						case Int:{
							param.setValue(d.intValue());
							break;
						}
						case Boolean:{
							param.setValue(d.intValue()==1);
							break;
						}
						default:
					}
					
				}else if(p.hasColor()){
					mat.setColor(name,p.getColor().toJME());
				}else if(p.hasTexture()){
					Texture tx;
					xbuf.Primitives.Texture t=p.getTexture();
					// Try first to load from asset path
					String path=root.getName();
					path=path.substring(0,path.lastIndexOf("/"))+"/"+t.getRpath();
					try{
						
						tx=assetManager.loadTexture(path);
					}catch(AssetNotFoundException ex1){
						log.debug("failed to load texture:",path,ex1," try with asset root.");

						// If not found load from root
						try{
							tx=assetManager.loadTexture(t.getRpath());
						}catch(AssetNotFoundException ex){
							log.warn("failed to load texture:",t.getRpath(),ex);
							tx=defaultTexture.clone();
						}
					}
					if(tx!=null){
						mat.setTexture(name,tx);
					}
				}
			}
		}
	}


}
