package wf.frk.f3b;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import org.slf4j.LoggerFactory;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import f3b.Datas.Data;

public class F3bLoader implements AssetLoader {
	public static void init(AssetManager am){
		am.registerLoader(F3bLoader.class,"f3b");
	}

	public F3b buildF3b(AssetInfo assetInfo){
		return  new F3b(assetInfo.getManager());
	}
	
	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		Node root = new Node(assetInfo.getKey().getName());
		InputStream in = null ;
		try {
			F3bKey f3bkey=null;
			AssetKey<?> key=assetInfo.getKey();
			if(key instanceof F3bKey){
				f3bkey=(F3bKey)key;
			}else{
				f3bkey=new F3bKey(key.getName());
			}
			in = assetInfo.openStream();
			F3b f3b = buildF3b(assetInfo);
			Data src = Data.parseFrom(in, f3b.registry);
			F3bContext context=new F3bContext();
			LoggerCollector log = new LoggerCollector("parse:" + assetInfo.getKey().getName());
			context.setSettings(f3bkey);
			f3b.merge(src, root, context, log);
			log.debug("Context:\n{}",context.toString());
			log.dumpTo(LoggerFactory.getLogger(this.getClass()));
		} finally {
			if(in!=null)in.close();
		}
		return root;
	}

}
