package wf.frk.f3b.jme3;

import com.jme3.scene.Node;

public class DefaultNodeBuilder implements NodeBuilder{

	@Override
	public Node build(String name) {
		return new Node(name);
	}

}
