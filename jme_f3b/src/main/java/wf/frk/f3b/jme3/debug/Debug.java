package wf.frk.f3b.jme3.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class Debug{
	public static AssetManager assetManager;
	public static Node rootNode;

	public static Spatial makeCube(float size,ColorRGBA color){
		Box b=new Box(size,size,size);
		Material m=new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		m.setColor("Color",color);
		Geometry g=new Geometry("Debug box",b);
		g.setMaterial(m);
		rootNode.attachChild(g);
		return g;
	}
}
