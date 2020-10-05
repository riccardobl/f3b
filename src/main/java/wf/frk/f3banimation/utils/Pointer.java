package wf.frk.f3banimation.utils;

public class Pointer <T>{
	public T ref;
	public Pointer(T t){
		ref=t;
	}
	public void destroy(){
		ref=null;
	}
	
	@Override
	public String toString(){
		return "P!["+ref.toString()+"]";
	}
}
