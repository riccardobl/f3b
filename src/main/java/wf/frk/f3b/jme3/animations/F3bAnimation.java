package wf.frk.f3b.jme3.animations;

import java.util.LinkedList;
import java.util.List;

import com.jme3.animation.Animation;
import com.jme3.animation.Skeleton;

import lombok.Data;

@Data
public class F3bAnimation{
	protected final String name;
	protected final float duration;
	protected final List<F3bAnimTrack> tracks=new LinkedList<F3bAnimTrack>();
	
	public Animation toJME(Skeleton sk){
		Animation anim=new Animation(getName(),getDuration());
		for(F3bAnimTrack t:tracks)anim.addTrack(t.toJME(sk));
		return anim;
	}
}
