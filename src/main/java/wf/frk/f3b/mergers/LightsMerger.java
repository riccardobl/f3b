package wf.frk.f3b.mergers;

import org.slf4j.Logger;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;

import lombok.experimental.ExtensionMethod;
import wf.frk.f3b.Merger;
import wf.frk.f3b.F3bContext;
import f3b.Datas.Data;
import f3b.Lights;

@ExtensionMethod({wf.frk.f3b.ext.PrimitiveExt.class})
public class LightsMerger implements Merger{

	public void apply(Data src, Node root, F3bContext context, Logger log) {
		for(f3b.Lights.Light srcl:src.getLightsList()){
			// TODO manage parent hierarchy
			String id=srcl.getId();
			Light light=context.get(id);
			if(light==null){
				light=makeLight(srcl);
				context.put(id,light);
			}

			if(srcl.hasColor()){
				light.setColor(srcl.getColor().toJME());
			}

			// TODO manage attenuation
			// TODO manage conversion of type
			switch(srcl.getKind()){
				case spot:{
					SpotLight l=(SpotLight)light;
					if(srcl.hasSpotAngle()){
						float max=srcl.getSpotAngle().getMax();
						switch(srcl.getSpotAngle().getCurveCase()){
							case CURVE_NOT_SET:
								l.setSpotOuterAngle(max);
								l.setSpotInnerAngle(max);
								break;
							case LINEAR:
								l.setSpotOuterAngle(max*srcl.getSpotAngle().getLinear().getEnd());
								l.setSpotInnerAngle(max*srcl.getSpotAngle().getLinear().getBegin());
								break;
							default:{
								l.setSpotOuterAngle(max);
								l.setSpotInnerAngle(max);
								log.warn("doesn't support curve like {} for spot_angle",srcl.getSpotAngle().getCurveCase());
							}
						}
					}
					if(srcl.hasRadialDistance()){
						l.setSpotRange(srcl.getRadialDistance().getMax());
					}
					break;
				}
				case point:{
					PointLight l=(PointLight)light;
					if(srcl.hasRadialDistance()){
						float max=srcl.getRadialDistance().getMax();
						switch(srcl.getRadialDistance().getCurveCase()){
							case CURVE_NOT_SET:{
								l.setRadius(max);
								break;
							}
							case LINEAR:{
								l.setRadius(max*srcl.getSpotAngle().getLinear().getEnd());
								break;
							}
							case SMOOTH:{
								l.setRadius(max*srcl.getSpotAngle().getSmooth().getEnd());
								break;
							}
							default:{
								l.setRadius(max);
								log.warn("doesn't support curve like {} for spot_angle",srcl.getSpotAngle().getCurveCase());
							}
						}
					}
					break;
				}
				case ambient:{
					break;
				}
				case directional:{
					light.setColor(srcl.getColor().toJME().mult(srcl.getIntensity()/4f)); // Try to make the light behave like in blender.
					break;
				}
			}
		}
	}

	private Light makeLight(Lights.Light srcl) {
		Light l0=null;
		switch(srcl.getKind()){
			case ambient:
				l0=new AmbientLight();
				break;
			case directional:
				l0=new DirectionalLight();
				break;
			case spot:{
				SpotLight l=new SpotLight();
				l.setSpotRange(1000);
				l.setSpotInnerAngle(5f*FastMath.DEG_TO_RAD);
				l.setSpotOuterAngle(10f*FastMath.DEG_TO_RAD);
				l0=l;
				break;
			}
			case point:
				l0=new PointLight();
				break;
		}
		l0.setColor(ColorRGBA.White);
		l0.setName(srcl.hasName()?srcl.getName():srcl.getId());
		return l0;
	}

}
