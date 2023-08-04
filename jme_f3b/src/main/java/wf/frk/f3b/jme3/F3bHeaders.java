package wf.frk.f3b.jme3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.CodedInputStream;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetManager;
import com.jme3.asset.CloneableSmartAsset;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import f3b.Datas.Data;
import f3b.HeaderOuterClass.Header;
import wf.frk.f3b.jme3.mergers.Merger;
import wf.frk.f3b.jme3.mergers.relations.Linker;

public class F3bHeaders {


	public static class F3bHeader implements CloneableSmartAsset{
		public String path;
		public String scene;
		public Map<String,Collection<String>> collections=new HashMap<String,Collection<String>>();
		private transient AssetKey key;
		
		@Override
		public CloneableSmartAsset clone(){
			try{
				return (CloneableSmartAsset)super.clone();
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}

		public void setKey(AssetKey key) {
			this.key = key;
		}
	
		public AssetKey getKey() {
			return key;
		}
	
	}

	public final Map<String,F3bHeader> headers=new HashMap<String,F3bHeader>();
	public class CollectionData {
		public String scene;
		public String id;
		public Collection<String> objects=new ArrayList<String>();


		public Collection<Spatial> filter(Spatial root){
			ArrayList<Spatial> spatials=new ArrayList<Spatial>();
			root.depthFirstTraversal((s)->{
				Object f3bid=s.getUserData(Const.f3b_id);
				for(String o:objects){
					if(f3bid!=null&&f3bid.equals(o)){
						spatials.add(s);
						break;
					}
				}
			});
			return spatials;
		}
	}


	public CollectionData getCollectionFromId(String collectionId) {
		for(F3bHeader h:headers.values()){
			for(Entry<String,Collection<String>> e:h.collections.entrySet()){
					if(e.getKey().equals(collectionId)){
						CollectionData out=new CollectionData();
						out.id=collectionId;
						out.scene=h.scene;
						out.objects=e.getValue();
						return out;	
					}
			}
		}
		return null;
	}







}
