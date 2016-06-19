package wf.frk.f3b.ext.jme3;

import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.Vector3f;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LightExt{
	public static void setPosition(Light light, Vector3f pos) {
		if(light instanceof PointLight){
			if(!((PointLight)light).getPosition().equals(pos)){
				((PointLight)light).setPosition(pos);
				log.debug("Set position {} for light {}",pos,light);
			}
		}else if(light instanceof SpotLight){
			if(!((SpotLight)light).getPosition().equals(pos)){
				((SpotLight)light).setPosition(pos);
				log.debug("Set position {} for light {}",pos,light);
			}
		}
	}

	public static void setDirection(Light light, Vector3f dir) {
		if(light instanceof DirectionalLight){
			if(!((DirectionalLight)light).getDirection().equals(dir)){
				((DirectionalLight)light).setDirection(dir);
				log.debug("Set direction {} for light {}",dir,light);
			}
		}else if(light instanceof SpotLight){
			if(!((SpotLight)light).getDirection().equals(dir)){
				((SpotLight)light).setDirection(dir);
				log.debug("Set direction {} for light {}",dir,light);
			}

		}
	}
}
