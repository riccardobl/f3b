package wf.frk.f3b.jme3.physicsloader;

public interface PhysicsLoaderSettings{

	/**
	 *   Enable physics with the default physics loader [ default = false ]
	 * @param v
	 * @return
	 */
	public PhysicsLoaderSettings usePhysics(boolean v);

	/**
	 *   Enable physics with the given physics loader [ default = null ]
	 * @param phyProvider null means disabled.
	 */
	public PhysicsLoaderSettings usePhysics(PhysicsLoader<?,?> phyProvider);

	public PhysicsLoader<?,?> getPhysicsLoader();

	/**
	 *   Use VHACD to load dynamic mesh accurate shapes. [ default = null ]
	 * @param factory can be either an instance of VHACDCollisionShapeFactory or a boolean. 
	 * When a boolean is passed, the default implementation with default settings is used.
	 * null means disabled.
	 * @return
	 */
	public PhysicsLoaderSettings useVHACD(Object factory);

		
	/**
	 *   Use VHACD to simplify mesh accurate static collision shapes.
	 * @param v [default=false]
	 * @return
	 */
	public PhysicsLoaderSettings useVHACDForStaticCollisions(boolean v);

	public boolean useVHACDForStaticCollisions();
	
	public Object getVHACDFactory();

	/**	
	 *   [ default = false ]
	 * @param v
	 * @return
	 */
	public PhysicsLoaderSettings useEnhancedRigidbodies(boolean v);

	public PhysicsLoaderSettings useEnhancedGhostbodies(boolean v);


	public boolean useEnhancedRigidbodies();
	public boolean useEnhancedGhostbodies();
	
	

	
	/**
	 *   
	 * @param cacher null means disabled.
	 * @return
	 */
	public PhysicsLoaderSettings useCacher(PhysicsCacher cacher);

	public PhysicsCacher getCacher();
}
