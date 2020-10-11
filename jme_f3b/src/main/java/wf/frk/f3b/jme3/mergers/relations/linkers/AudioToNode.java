package wf.frk.f3b.jme3.mergers.relations.linkers;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef1;
import static wf.frk.f3b.jme3.mergers.relations.LinkerHelpers.getRef2;

import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;

import wf.frk.f3b.jme3.mergers.RelationsMerger;
import wf.frk.f3b.jme3.mergers.relations.Linker;
import wf.frk.f3b.jme3.mergers.relations.RefData;

public class AudioToNode implements Linker{

    @Override
    public boolean doLink(RelationsMerger loader, RefData data) {
        AudioNode op1=getRef1(data,AudioNode.class);
        Node op2=getRef2(data,Node.class);
        if(op1==null||op2==null)return false;
        op1=(AudioNode)op1.clone();
        op1.setUserData("F3b::Speaker",true);
        op2.attachChild(op1);
        return true;
    }
}