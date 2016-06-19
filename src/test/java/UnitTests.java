import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.material.TechniqueDef.LightMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture2D;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;

import wf.frk.f3b.F3bKey;
import wf.frk.f3b.debug.Debug;
import wf.frk.f3b.runtime.F3bLightRuntimeLoader;
import wf.frk.f3b.runtime.F3bPhysicsRuntimeLoader;
import wf.frk.f3b.runtime.F3bRuntimeLoader;

public class UnitTests{
	public boolean headless=true;
	@Test
	public void testMatNodes(){
//		boolean headless=false;
		final SimpleApplication app=TestHelpers.buildApp(headless);
		
		
		TestHelpers.hijackUpdateThread(app);
		Debug.assetManager=app.getAssetManager();
		Debug.rootNode=app.getRootNode();
		final AtomicBoolean ok=new AtomicBoolean();
		
		Spatial scene=F3bRuntimeLoader.instance(new F3bKey("unit_tests/f3b/test_matnodes.f3b"))
				.attachSceneTo(app.getRootNode())
				.attachLightsTo(app.getRootNode())
				.load(app.getAssetManager());
		
		scene.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				if(s instanceof Geometry){
					Geometry g=(Geometry)s;
					Material mat=g.getMaterial();
					for(MatParam p:mat.getParams()){
						if(p.getName().equals("ColorMap"))ok.set(true);
					}				
				}
			}
		});
		F3bLightRuntimeLoader.load(app.getRootNode(),scene);
		
		assertTrue("ColorMap not found. Material is not loaded properly",ok.get());	
		for(Light l:app.getRootNode().getLocalLightList()){
			System.out.println("Remove "+l);
			app.getRootNode().removeLight(l);
		}
		
		app.getRootNode().attachChild(scene);
		TestHelpers.releaseUpdateThread(app);
		if(!headless)TestHelpers.waitFor(app);
		TestHelpers.closeApp(app);
	}
	
	
	
	
	@Test
	public void testTangents(){
//		boolean headless=false;
		final SimpleApplication app=TestHelpers.buildApp(headless);
		
		
		TestHelpers.hijackUpdateThread(app);
		F3bKey key=new F3bKey("unit_tests/f3b/tangents.f3b");

		StringBuilder failed=new StringBuilder();
		Spatial scene=app.getAssetManager().loadModel(key);	
		Spatial clone=scene.clone();
		app.getRootNode().attachChild(scene);

		scene.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				if(s instanceof Geometry){
					Material mat=app.getAssetManager().loadMaterial("Common/Materials/VertexColor.j3m");
					mat.getAdditionalRenderState().setDepthTest(false);
					Geometry g=(Geometry)s;
					g.getMaterial().getAdditionalRenderState().setWireframe(true);
					Geometry normals=new  Geometry("Normals",TangentBinormalGenerator.genNormalLines(g.getMesh(),2f));
					normals.setMaterial(mat);
					Geometry tangents=new  Geometry("Tangents",TangentBinormalGenerator.genTbnLines(g.getMesh(),2f));
					tangents.setMaterial(mat);
					app.getRootNode().attachChild(normals);
					normals.setLocalTranslation(g.getWorldTranslation());
					app.getRootNode().attachChild(tangents);
					tangents.setLocalTranslation(g.getWorldTranslation());
				}
			}
		});
		
		app.getRootNode().attachChild(clone);
		clone.setLocalTranslation(16,0,0);
		clone.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				if(s instanceof Geometry){
					Material mat=app.getAssetManager().loadMaterial("Common/Materials/VertexColor.j3m");
					mat.getAdditionalRenderState().setDepthTest(false);
					Geometry g=(Geometry)s;
					g.getMaterial().getAdditionalRenderState().setWireframe(true);

					TangentBinormalGenerator.generate(g);

					Geometry normals=new  Geometry("Normals",TangentBinormalGenerator.genNormalLines(g.getMesh(),2f));
					normals.setMaterial(mat);
					Geometry tangents=new  Geometry("Tangents",TangentBinormalGenerator.genTbnLines(g.getMesh(),2f));
					tangents.setMaterial(mat);
					app.getRootNode().attachChild(normals);
					normals.setLocalTranslation(g.getWorldTranslation());
					app.getRootNode().attachChild(tangents);
					tangents.setLocalTranslation(g.getWorldTranslation());
				}
			}
		});
		
		assertTrue(failed.toString(),failed.toString().isEmpty());	

		
		TestHelpers.releaseUpdateThread(app);
		if(!headless)TestHelpers.waitFor(app);
		TestHelpers.closeApp(app);
	}
	
//	private ArrayList<Float> toArray(VertexBuffer buffer) {
//		ArrayList<Float> f=new ArrayList<Float>();
//		for(int i=0;i<buffer.getNumElements();i++){
//			for(int j=0;j<buffer.getNumComponents();j++){
//				f.add((Float)buffer.getElementComponent(i,j));
//			}
//		}
//		return f;
//	}

	@Test
	public void testConstraints(){
//		boolean headless=false;
		SimpleApplication app=TestHelpers.buildApp(headless);
		BulletAppState bullet=TestHelpers.buildBullet(app,true);
		
		TestHelpers.hijackUpdateThread(app);
		F3bKey key=new F3bKey("unit_tests/f3b/constraints.f3b").usePhysics(true).useEnhancedRigidbodies(true);
		Spatial scene=app.getAssetManager().loadModel(key);
		app.getRootNode().attachChild(scene);
		scene.setLocalTranslation(0,-10,0);
		F3bPhysicsRuntimeLoader.load(key,scene,bullet.getPhysicsSpace());
		
		int i=0;
		Collection<PhysicsJoint> joints=bullet.getPhysicsSpace().getJointList();
		for(PhysicsJoint joint:joints){
			System.out.println(joint);
			i++;
		}
		assertTrue("Found "+i+" constraints, 1 expected",i==1);	
		
		TestHelpers.releaseUpdateThread(app);
		if(!headless)TestHelpers.waitFor(app);
		TestHelpers.closeApp(app);
	}
	
	@Test
	public void testMultiMat() {
//		boolean headless=false;
		SimpleApplication app=TestHelpers.buildApp(headless);
		TestHelpers.hijackUpdateThread(app);
		Spatial scene=app.getAssetManager().loadModel(new F3bKey("unit_tests/f3b/multi_mat.f3b"));	
		app.getRootNode().attachChild(scene);
		F3bRuntimeLoader.instance(new F3bKey("unit_tests/f3b/test_matnodes.f3b"))
		.attachLightsTo(app.getRootNode())
		.load(scene);
		
		// All material instances
		final LinkedList<Material> materials_instances=new LinkedList<Material>();
		scene.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				if(s instanceof Geometry){
					Geometry geom=(Geometry)s;
					Material mat=geom.getMaterial();
					materials_instances.add(mat);
				}
			}
		});

		
		assertTrue("Found "+materials_instances.size()+" materials, 2 expected",materials_instances.size()==2);	

		TestHelpers.releaseUpdateThread(app);
		if(!headless)TestHelpers.waitFor(app);
		TestHelpers.closeApp(app);

	}

	@Test
	public void testHwSkinning() {
//		boolean headless=false;
		final SimpleApplication app=TestHelpers.buildApp(headless);
		TestHelpers.hijackUpdateThread(app);
		
		boolean created=false;
		try{
			Spatial scene=app.getAssetManager().loadModel("unit_tests/f3b/hw_skinning.f3b");
			app.getRootNode().attachChild(scene);
			scene.depthFirstTraversal(new SceneGraphVisitor(){
				@Override
				public void visit(Spatial s) {
					SkeletonControl sk=s.getControl(SkeletonControl.class);
					if(sk!=null){
						System.out.println("Found skeletoncontrol: "+sk+" on "+s);

						System.out.println("Set "+sk+".hwSkinning=true");
						sk.setHardwareSkinningPreferred(true);

						SkeletonDebugger skeletonDebug=new SkeletonDebugger("skeleton",sk.getSkeleton());
						Material mat=new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
						mat.setColor("Color",ColorRGBA.Green);
						mat.getAdditionalRenderState().setDepthTest(false);
						skeletonDebug.setMaterial(mat);
					    app.getRootNode().attachChild(skeletonDebug);
					    skeletonDebug.setLocalTranslation(s.getWorldTranslation());
					}
					
					AnimControl ac=s.getControl(AnimControl.class);

					if(ac!=null){
					
						System.out.println("Found animcontrol: "+ac+" on "+s);

						Collection<String> anims=ac.getAnimationNames();
						for(String a:anims){
							AnimChannel channel = ac.createChannel();
							channel.setAnim(a);
							channel.setLoopMode(LoopMode.Cycle);
							System.out.println("Set "+a+" to "+s);
						}
					}
				}
			});
			created=true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		TestHelpers.releaseUpdateThread(app);
		if(!headless)TestHelpers.waitFor(app);
		TestHelpers.closeApp(app);

		assertTrue("Hardware skinning cannot be used.",created);

	}

	@Test
	public void testMeshSharing() {
		SimpleApplication app=TestHelpers.buildApp(headless);
		TestHelpers.hijackUpdateThread(app);

		Spatial scene=app.getAssetManager().loadModel("unit_tests/f3b/shared_mesh.f3b");
		app.getRootNode().attachChild(scene);

		// All mesh instances
		final LinkedList<Mesh> meshes=new LinkedList<Mesh>();
		scene.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				if(s instanceof Geometry){
					Geometry geom=(Geometry)s;
					Mesh mesh=geom.getMesh();
					if(!meshes.contains(mesh)) meshes.add(mesh);
				}
			}
		});
		TestHelpers.releaseUpdateThread(app);
		if(!headless)TestHelpers.waitFor(app);
		TestHelpers.closeApp(app);

		assertTrue("Two different meshes are used, but loaded "+meshes.size(),meshes.size()==2);
	}

	
	
	@Test
	public void testMatSharing() {
		SimpleApplication app=TestHelpers.buildApp(headless);
		TestHelpers.hijackUpdateThread(app);

		Spatial scene=app.getAssetManager().loadModel("unit_tests/f3b/shared_mat.f3b");
		app.getRootNode().attachChild(scene);

		// All material instances
		final LinkedList<Material> materials_instances=new LinkedList<Material>();
		scene.depthFirstTraversal(new SceneGraphVisitor(){
			@Override
			public void visit(Spatial s) {
				if(s instanceof Geometry){
					Geometry geom=(Geometry)s;
					Material mat=geom.getMaterial();
					materials_instances.add(mat);
				}
			}
		});

		// The materials with name "shared" should be used 2 times.
		int n_shared=0;
		// The materials with name "not_shared" should be used 1 time.
		int n_not_shared=0;

		// Unique materials
		LinkedList<Material> materials=new LinkedList<Material>();
		for(Material m:materials_instances){
			if(!materials.contains(m)) materials.add(m);
			if(m.getName().equals("shared")) n_shared++;
			if(m.getName().equals("not_shared")) n_not_shared++;

		}
		
		TestHelpers.releaseUpdateThread(app);
		if(!headless)TestHelpers.waitFor(app);
		TestHelpers.closeApp(app);
		assertTrue("'shared' material is used twice, but loaded "+n_shared,n_shared==2);
		assertTrue("'not_shared' material is used once, but loaded "+n_not_shared,n_not_shared==1);
		assertTrue("Two unique materials are used, but "+materials.size()+"  loaded",materials.size()==2);
		assertTrue("Three materials instance are used, but "+materials.size()+"  loaded",materials_instances.size()==3);
	}
}
