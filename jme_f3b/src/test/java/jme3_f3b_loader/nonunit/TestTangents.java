package jme3_f3b_loader.nonunit;

import static org.junit.Assert.assertTrue;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;

import utils.TestHelpers;
import wf.frk.f3b.jme3.F3bKey;

public class TestTangents {
	public static void main(String[] args) {
		boolean headless=false;
		final SimpleApplication app=TestHelpers.buildApp(headless);
		
		
		TestHelpers.hijackUpdateThread(app);
		F3bKey key=new F3bKey("unit_tests/f3b/tangents.f3b");
	
		Spatial scene=app.getAssetManager().loadModel(key);	
		app.getRootNode().attachChild(scene);
		
		final Material mat=new Material(app.getAssetManager(),"MatDefs/TangentsViewer.j3md");
	
		scene.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				if (s.getUserData("tgbn_gen")!=null){
					TangentBinormalGenerator.generate(s);
					s.setMaterial(mat);
				}else if(s.getUserData("mikkt_gen")!=null){
					MikktspaceTangentGenerator.generate(s);
					s.setMaterial(mat);
				}else if(s.getUserData("imported")!=null){
					s.setMaterial(mat);
				}else if(s.getUserData("frag")!=null){
					Material m=mat.clone();
					m.setBoolean("ComputeInFrag",true);
					s.setMaterial(m);
				}else if(s.getUserData("vert")!=null){
					Material m=mat.clone();
					m.setBoolean("ComputeInVert",true);
					s.setMaterial(m);
				}
			}
		});
					
		TestHelpers.releaseUpdateThread(app);
		if(!headless)TestHelpers.waitFor(app);
		TestHelpers.closeApp(app);
	}
}
