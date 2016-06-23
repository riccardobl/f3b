package wf.frk.f3b.jme3.mergers;


import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;

import f3b.Datas.Data;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import wf.frk.f3b.jme3.F3bContext;
@Log4j2
@ExtensionMethod({wf.frk.f3b.jme3.ext.f3b.TypesExt.class,wf.frk.f3b.jme3.ext.jme3.Vector4fExt.class})
public class LightsMerger implements Merger{

	public void apply(Data src, Node root, F3bContext context) {
		for(f3b.Lights.Light l:src.getLightsList()){
			Light lg=null;
			switch(l.getKind()){
				case spot:{
					lg=new SpotLight();
					SpotLight sl=(SpotLight)lg;
					sl.setSpotRange(1000);
					sl.setSpotInnerAngle(5f*FastMath.DEG_TO_RAD);
					sl.setSpotOuterAngle(10f*FastMath.DEG_TO_RAD);
					if(l.hasSpotAngle()){
						float max=l.getSpotAngle().getMax();
						switch(l.getSpotAngle().getCurveCase()){
							case CURVE_NOT_SET:
								sl.setSpotOuterAngle(max);
								sl.setSpotInnerAngle(max);
								break;
							case LINEAR:
								sl.setSpotOuterAngle(max*l.getSpotAngle().getLinear().getEnd());
								sl.setSpotInnerAngle(max*l.getSpotAngle().getLinear().getBegin());
								break;
							default:{
								sl.setSpotOuterAngle(max);
								sl.setSpotInnerAngle(max);
								log.warn("doesn't support curve like {} for spot_angle",l.getSpotAngle().getCurveCase());
							}
						}
					}
					if(l.hasRadialDistance()){
						sl.setSpotRange(l.getRadialDistance().getMax());
					}
					break;
				}
				case point:{
					lg=new PointLight();
					PointLight pl=(PointLight)lg;
					if(l.hasRadialDistance()){
						float max=l.getRadialDistance().getMax();
						switch(l.getRadialDistance().getCurveCase()){
							case CURVE_NOT_SET:{
								pl.setRadius(max);
								break;
							}
							case LINEAR:{
								pl.setRadius(max*l.getSpotAngle().getLinear().getEnd());
								break;
							}
							case SMOOTH:{
								pl.setRadius(max*l.getSpotAngle().getSmooth().getEnd());
								break;
							}
							default:{
								pl.setRadius(max);
								log.warn("doesn't support curve like {} for spot_angle",l.getSpotAngle().getCurveCase());
							}
						}
					}
					break;
				}
				case directional:{
					lg=new DirectionalLight();
					break;
				}
				case ambient:{
					lg=new AmbientLight();
				}
				default:{
					log.warn("{} light not supported",l.getKind());
				}
			}
			if(lg!=null){
				lg.setName(l.getName());
				lg.setColor(l.getColor().toJME().toColorRGBA().mult(l.getIntensity()));
				context.put(l.getId(),lg);
			}
		}
	}
}
