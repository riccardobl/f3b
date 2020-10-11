
import bpy,bmesh
import time,copy,mathutils,math

from mathutils import noise
    
Context={
    "lasttick":0,
    "running":False,
    "store":{},
    "starttime":-1
}


def timeNow():
    t=time.time()
    return t    
    
def calcFrameTime():
    fps = max(1.0, float(bpy.context.scene.render.fps))
    return 1.0/fps


def wind(v,co,col,noiseAmmount,timeN):
    pos=list(co)
    distorsion=1.0
    
    
    noisec=[pos[0]+timeN,
    pos[2]+timeN,
    0]

    
    windNoise=[0,0,0]
    windNoise[0]=mathutils.noise.noise(noisec)
    noisec[2]=1000
    windNoise[1]=mathutils.noise.noise(noisec)
    noisec[2]=9000
    windNoise[2]=mathutils.noise.noise(noisec)
    


    vcolor=(1,1,1,1)
    if col:
        vcolor=col    

    
    pos[2] += math.sin(timeN*20) * vcolor[3] * noiseAmmount[2] * vcolor[0] * windNoise[1]
    pos[2] += math.sin(timeN*15) * vcolor[3] * noiseAmmount[2] * vcolor[1] * windNoise[1]
    pos[2] += math.sin(timeN*25) * vcolor[3] * noiseAmmount[2] * vcolor[2] * windNoise[1]

    pos[0] += math.sin(timeN*20) * vcolor[3] * noiseAmmount[0] * vcolor[0] * windNoise[0]
    pos[0] += math.sin(timeN*15) * vcolor[3] * noiseAmmount[0] * vcolor[1] * windNoise[0]
    pos[0] += math.sin(timeN*25) * vcolor[3] * noiseAmmount[0] * vcolor[2] * windNoise[0]

    pos[1] += windNoise[0] * noiseAmmount[1] * vcolor[3] * vcolor[0]
    pos[1] += windNoise[1] * noiseAmmount[1] * vcolor[3] * vcolor[1]
    pos[1] += windNoise[2] * noiseAmmount[1] * vcolor[3] * vcolor[2]
    
    return pos
    

def preFrameChange(scene):
    
    
    global Context
    
    timeN=timeNow()
    
    Context["running"]=True
    Context["lasttick"]=timeNow()
    
    if Context["starttime"]==-1:
        Context["starttime"]=Context["lasttick"]

    noiseAmmount=(1,1,1)
    timeN=Context["lasttick"]-Context["starttime"]
    
    if  not "cols" in Context:
        Context["cols"]={}
            

    if bpy.ops.object.mode_set.poll():
        for  obj in bpy.context.scene.objects:
            if obj.select_get():                
                if not obj in Context["store"]:
                    Context["store"][obj]={}      
                    
                if not obj in Context["cols"]:
                    Context["cols"][obj]={}
                    src_mesh=obj.data
                    for i, poly in enumerate(src_mesh.polygons):        
                        for k in poly.loop_indices:
                            vl = src_mesh.loops[k]
                            index=vl.vertex_index
                            if src_mesh.vertex_colors.active:
                                c=[]
                                c.extend(src_mesh.vertex_colors.active.data[k].color)
                                c.append(1.0)   
                                Context["cols"][obj][index]=c                            
                         
                bpy.ops.object.mode_set(mode='EDIT')
                mesh = bmesh.from_edit_mesh(obj.data)                
                for vert in mesh.verts: 
                    if not vert.index in Context["store"][obj]:
                        v=[]
                        v.append(vert.co[0])
                        v.append(vert.co[1])
                        v.append(vert.co[2])                        
                        Context["store"][obj][vert.index]=v                        
                        #print("Store "+str(vert.index)+str(v))    
                    col=Context["cols"][obj][vert.index] if vert.index in Context["cols"][obj] else None
                    vert.co=wind(vert,tuple(Context["store"][obj][vert.index]),col,noiseAmmount,timeN)                    
                bmesh.update_edit_mesh(obj.data) 
                bpy.ops.object.mode_set(mode='OBJECT')


def resetAnim():
    global Context
    if Context["running"]: 
        dtime=timeNow()-Context["lasttick"]
        frametime2=calcFrameTime()*2
        if dtime > frametime2:
            print("Reset Now")
           
            for  obj in bpy.context.scene.objects:
                if obj in Context["store"]:
                    bpy.ops.object.mode_set(mode='EDIT')
                    mesh = bmesh.from_edit_mesh(obj.data)          
                    for vert in mesh.verts:                       
                        if vert.index in Context["store"][obj]:
                            v=Context["store"][obj][vert.index]  
                            #print("Reset "+str(vert.index)+" to "+str(v))
                            vert.co=v                
                    bmesh.update_edit_mesh(obj.data) 
                    bpy.ops.object.mode_set(mode='OBJECT')
                    del Context["store"][obj]                        
            Context["running"]=False
            del Context["cols"]
    delay=calcFrameTime()   
    return delay
        
    

class ModalTimerOperator(bpy.types.Operator):
    bl_idname = "wm.modal_timer_operator"
    bl_label = "Modal Timer Operator"

    _timer = None

    def modal(self, context, event):
        if event.type in {'RIGHTMOUSE', 'ESC'}:
            self.cancel(context)
            return {'CANCELLED'}

        if event.type == 'TIMER':
            resetAnim()

        return {'PASS_THROUGH'}

    def execute(self, context):
        wm = context.window_manager
        self._timer = wm.event_timer_add(calcFrameTime(), window=context.window)
        wm.modal_handler_add(self)
        return {'RUNNING_MODAL'}

    def cancel(self, context):
        wm = context.window_manager
        wm.event_timer_remove(self._timer)


def register():
    bpy.app.handlers.frame_change_pre.append(preFrameChange)
    bpy.utils.register_class(ModalTimerOperator)


def unregister():
    bpy.app.handlers.frame_change_pre.remove(preFrameChange)

register()
bpy.ops.wm.modal_timer_operator()



    
    

