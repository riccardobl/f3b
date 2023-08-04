package wf.frk.f3b.jme3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;

import com.google.protobuf.CodedInputStream;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import f3b.Datas.Data;
import f3b.HeaderOuterClass.Header;
import wf.frk.f3b.jme3.mergers.Merger;
import wf.frk.f3b.jme3.mergers.relations.Linker;

public class F3bLoader implements AssetLoader{
	public static LinkedList<Merger> mergers=new LinkedList<Merger>();
	public static LinkedList<Linker> linkers=new LinkedList<Linker>();
	private static final java.util.logging.Logger LOGGER=java.util.logging.Logger.getLogger(F3bLoader.class.getName());

	
	public static void init(AssetManager am){
		am.registerLoader(F3bLoader.class,"f3b","f3h");
	}
	
	public F3b buildF3b(AssetInfo assetInfo){
		F3b f3b=new F3b(assetInfo.getManager());
		f3b.relations.getLinkers().addAll(linkers);
		// F3bKey f3bkey=(F3bKey)assetInfo.getKey();
		f3b.mergers.addAll(mergers);
		return f3b;
	}

	protected void reloadAllHeaders(AssetManager am,F3bKey key){
		key.getHeaders().headers.clear();
		// This is disabled for now since it needs changes in the core.
		// Collection<AssetEntry> entries=am.listAssets();
		// for(AssetEntry e:entries){
		// 	if(e.path.endsWith(".f3h")){
		// 		loadHeader(am,  key, e.path);
		// 	}
		// }
	}

	protected void loadHeader(AssetManager am,F3bKey key,String path){
		LOGGER.log(Level.FINE,"Load header {0}",path);
		System.out.println("Load header {}"+path);

		F3bKey k=(F3bKey)key.clone();
		k.setName(path);
		am.loadAsset(k);
	}
	
	
	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		F3bKey f3bkey=null;
		AssetKey<?> key=assetInfo.getKey();
		if(key instanceof F3bKey){
			f3bkey=(F3bKey)key;
		}else{
			f3bkey=new F3bKey(key.getName());
		}

		F3b f3b=f3bkey.getLoaderInstance();
		if(f3b==null)f3bkey.setLoaderInstance(f3b=buildF3b(assetInfo));
				
		F3bContext context=f3bkey.getContext();
		if(context==null){
			f3bkey.setContext(context=new F3bContext());
			context.setSettings(f3bkey);
		}

		F3bHeaders headers=f3bkey.getHeaders();
		if(headers==null){
			f3bkey.setHeaders(headers=new F3bHeaders());
		}

		f3bkey.setAssetManager(assetInfo.getManager());
		
		Object out;

		InputStream in = null ;
		try {
			in=assetInfo.openStream();

			CodedInputStream cin=CodedInputStream.newInstance(in);
			cin.setSizeLimit(Integer.MAX_VALUE);

			if(key.getName().endsWith(".f3b")){

				if(f3bkey.resolveAllHeaders){
					reloadAllHeaders(assetInfo.getManager(),f3bkey);
				}else{
					String headerF=key.getName().substring(0,key.getName().length()-"f3b".length())+"f3h";
					loadHeader(assetInfo.getManager(),  f3bkey, headerF);
				}

				Data src=Data.parseFrom(cin,f3b.extensions);
				Node root = new Node(assetInfo.getKey().getName());
				f3b.merge(src, root, f3bkey);	
				out=root;
			}else{
				Header src=Header.parseFrom(cin);
				out=f3b.merge(src, key.getName(), f3bkey);
			}
		} finally {
			if(in!=null)in.close();
		}
		return out;
	}}
