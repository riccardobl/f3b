package wf.frk.f3b.ext;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;

public class Vector4fExt{
	public static ColorRGBA toColorRGBA(Vector4f src) {
		if(src==null)return new ColorRGBA();
		return new ColorRGBA(src.getX(),src.getY(),src.getZ(),src.getW());
	}
}
