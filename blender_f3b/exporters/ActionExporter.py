import bpy_extras
import f3b
import f3b.datas_pb2
import f3b.custom_params_pb2
import f3b.animations_kf_pb2
import f3b.physics_pb2
from . import Relations
from ..F3bContext import *
from ..Utils import *
from .. import Logger as log

def to_time(frame,fps):
    return int((frame * 1000) / fps)


def updateScene():
    bpy.context.view_layer.update()
    for l in bpy.context.scene.view_layers:
        l.update()


objPoseSave={}

def resetToRestPose(scene,obj):
    if obj.pose!=None:
        scene.frame_set(0)
        obj.data.pose_position = 'REST'
        updateScene()
        restPos={}
        for p in obj.pose.bones:
            restPos[p] = p.matrix.copy()
        obj.data.pose_position = 'POSE'
        for p in obj.pose.bones:
            p.matrix=restPos[p]
        updateScene()


def resetObjects(scene,restore=False):
    global objPoseSave
    if restore:
        for obj in scene.objects:
            resetToRestPose(scene,obj)
        for obj in scene.objects:
            if obj.pose!=None:
                for p in obj.pose.bones:
                    m=objPoseSave[obj]["pose"][p]
                    p.matrix=m.copy()
                    updateScene()
    else:
        objPoseSave={}
        for obj in scene.objects:
            assert not obj in objPoseSave
            objPoseSave[obj]={}
            if obj.pose!=None:
                objPoseSave[obj]["pose"]={}
                for p in obj.pose.bones:
                    assert not p in objPoseSave[obj]["pose"]
                    objPoseSave[obj]["pose"][p]=p.matrix.copy()
        for obj in scene.objects:
            resetToRestPose(scene,obj)
    updateScene()






class Sampler:

    # def resetPose(self):
    #     for p in self.obj.pose.bones:
    #         p.rotation_quaternion = mathutils.Quaternion( (0, 0, 0), 0 )
    #         p.scale = mathutils.Vector((1, 1, 1))
    #         p.location = mathutils.Vector((0, 0, 0))
    #     bpy.context.view_layer.update()

    def __init__(self, obj,  pose_bone_idx=None):
        self.track={}
        self.obj = obj
        self.pose_bone_idx = pose_bone_idx
        #self.last_equals = None
        self.track["frames"]=[]
        # print("Attach sampler to ",obj)

    def getName(self):
        if self.pose_bone_idx is not None:
            return self.obj.pose.bones[self.pose_bone_idx].name
        else:
            return "objectAnim"
            
    def start(self):
        if self.pose_bone_idx is not None:
            # self.resetPose()
            self.track["bone_name"] = self.obj.pose.bones[self.pose_bone_idx].name
        #self.initial_mat4=self.previous_mat4 = self.getTrs()
        self.initial_mat4=self.getTrs()
        self.last_mat4=None


    def getTrs(self):
        if self.pose_bone_idx is not None:
            pbone = self.obj.pose.bones[self.pose_bone_idx]
            mat4 = pbone.matrix
            if pbone.parent:
                try:
                    mat4 = pbone.parent.matrix.inverted() @ mat4
                except: 
                    print("Can't invert matrix",mat4,"for",pbone)
        else:
            mat4 = self.obj.matrix_local
        return mat4.copy()

    def capture(self, t):
        mat4 = self.getTrs()
        # print(mat4)        
        if True or  self.last_mat4==None or mat4!=self.last_mat4:
        #if self.previous_mat4 is None or not equals_mat4(mat4, self.previous_mat4, 0.000001):
            #if self.last_equals is not None:
                #self.track["frames"].append([self.last_equals, self.previous_mat4])
                #self.last_equals = None
            #self.previous_mat4 = mat4
            self.last_mat4=mat4.copy()
            self.track["frames"].append((t,mat4))

        # else: 
        #         print("No change skip.")
        # else:
            #self.last_equals = t

    def end(self):
        if self.pose_bone_idx is not None:
            pass
            # pbone = self.obj.pose.bones[self.pose_bone_idx]
            # pbone.matrix=self.initial_mat4
        else:
            self.obj.matrix_local=self.initial_mat4

        noInfluence=True
        for frame in self.track["frames"]:
            if  not equals_mat4(frame[1], self.initial_mat4, 0.000001):
                noInfluence=False
                break

        if noInfluence:
            print(self.getName(),"not influenced by this animation. Skip")
            self.track["frames"]=[]


def export_track(ctx: F3bContext,scene, obj, track, dst, fps,track_type,startFrame,endFrame,trackIndex):
    dst.id = ctx.idOf(track)
    dst.name = track.name
    dst.index=trackIndex
    dst.duration = to_time(max(1, float((endFrame +1)- startFrame)),fps)
    samplers = []


    if track_type == 'OBJECT':
        dst.target_kind = f3b.animations_kf_pb2.AnimationKF.tobject
        samplers.append(Sampler(obj))
        if obj.type == 'ARMATURE':
            for i in range(0, len(obj.pose.bones)):
                samplers.append(Sampler(obj,  i))
    elif track_type == 'ARMATURE':  
        dst.target_kind = f3b.animations_kf_pb2.AnimationKF.skeleton
        for i in range(0, len(obj.pose.bones)):
            samplers.append(Sampler(obj, i))
    else:
        log.warning("unsupported id_roor => target_kind : " + track_type)
        return

    for sampler in samplers:
        track.mute=True
        resetObjects(scene)
        sampler.start()
        track.mute=False
        for f in range(startFrame, endFrame+1):     
            # print("Export frame",f)          
            scene.frame_set(f)
            updateScene()
            sampler.capture(to_time(f,fps))
        sampler.end()
        track.mute=True

        if len(sampler.track["frames"])>0:
            print("Export",dst.name,"from",startFrame,"to",endFrame,"duration",dst.duration,"index",trackIndex,"for sampler",sampler.getName(),"with id",dst.id )

            dst_clip = dst.clips.add()
            
            if "bone_name" in sampler.track and sampler.track["bone_name"]  is not None:
                dst_clip.sampled_transform.bone_name = sampler.track["bone_name"] 

            for frame in sampler.track["frames"]:
                t,mat4=frame
                loc, quat, sca = mat4.decompose()
                dst_clip.sampled_transform.at.append(t)
                dst_clip.sampled_transform.translation_x.append(loc.x)
                dst_clip.sampled_transform.translation_y.append(loc.z)
                dst_clip.sampled_transform.translation_z.append(-loc.y)
                dst_clip.sampled_transform.scale_x.append(sca.x)
                dst_clip.sampled_transform.scale_y.append(sca.z)
                dst_clip.sampled_transform.scale_z.append(sca.y)
                dst_clip.sampled_transform.rotation_w.append(quat.w)
                dst_clip.sampled_transform.rotation_x.append(quat.x)
                dst_clip.sampled_transform.rotation_y.append(quat.z)
                dst_clip.sampled_transform.rotation_z.append(-quat.y)
        resetObjects(scene,True)


def export_default_pose(ctx: F3bContext,scene, obj,  dst,fps):
    dst.id = ctx.idOf(obj)+"_pose"
    dst.name = "_pose"
    dst.index=0
    dst.duration = to_time(3,fps)
    dst.target_kind = f3b.animations_kf_pb2.AnimationKF.skeleton

    for i in range(0, len(obj.pose.bones)):
        pbone = obj.pose.bones[i]
  
        dst_clip = dst.clips.add()
            
        dst_clip.sampled_transform.bone_name = obj.pose.bones[i].name
        
        mat4 = pbone.matrix
        if pbone.parent:
            try:
                mat4 = pbone.parent.matrix.inverted() @ mat4
            except: 
                print("Can't invert matrix",mat4,"for",pbone)
        
        loc, quat, sca = mat4.decompose()
        for i in range(0,3):
            dst_clip.sampled_transform.at.append(to_time(i,fps))
            dst_clip.sampled_transform.translation_x.append(loc.x)
            dst_clip.sampled_transform.translation_y.append(loc.z)
            dst_clip.sampled_transform.translation_z.append(-loc.y)
            dst_clip.sampled_transform.scale_x.append(sca.x)
            dst_clip.sampled_transform.scale_y.append(sca.z)
            dst_clip.sampled_transform.scale_z.append(sca.y)
            dst_clip.sampled_transform.rotation_w.append(quat.w)
            dst_clip.sampled_transform.rotation_x.append(quat.x)
            dst_clip.sampled_transform.rotation_y.append(quat.z)
            dst_clip.sampled_transform.rotation_z.append(-quat.y)

        



def export(ctx: F3bContext,dst_data: f3b.datas_pb2.Data,scene: bpy.types.Scene):
    fps = max(1.0, float(scene.render.fps))
    # frame_current = scene.frame_current
    # frame_subframe = scene.frame_subframe

    for obj in scene.objects:

        if not ctx.isExportable(obj):
            continue
        
        if obj.type == 'ARMATURE':
            print("Export base pose")
            dst = dst_data.animations_kf.add()
            export_default_pose(ctx,scene, obj,  dst,fps)
            Relations.add(ctx,dst_data,dst.id ,ctx.idOf(obj))  

        if obj.animation_data:
            st = {}
            
            for track in obj.animation_data.nla_tracks:
                st[track]=track.mute
                track.mute=True
   
            trackI=1
            # maxTrackI=len(obj.animation_data.nla_tracks)-1
            for track in obj.animation_data.nla_tracks:
                print("Export ",track.name)
                if len(track.strips)==0: 
                    print("Error, no stip found for "+str(track)+" skip.")
                    continue
                else:
                    allMuted=True
                    for s in track.strips:
                        if not s.mute: 
                            allMuted=False
                            break
                    if allMuted:
                        print("All strips are muted. skip.")
                        continue

                track_type=track.strips[0].action.id_root
                startFrame=-1
                endFrame=0

                for strip in track.strips:
                    if strip.frame_end>endFrame: endFrame=int(strip.frame_end)
                    if startFrame==-1 or strip.frame_start<startFrame: startFrame=int(strip.frame_start)

                if endFrame-startFrame>0:
                    if ctx.checkUpdateNeededAndClear(track):  
                        dst = dst_data.animations_kf.add()
                        export_track(ctx,scene, obj, track, dst, fps,track_type,startFrame,endFrame,trackI)
                        Relations.add(ctx,dst_data,ctx.idOf(track),ctx.idOf(obj))  
                
                trackI+=1

            for track in obj.animation_data.nla_tracks:
                track.mute=st[track]      
        
    # scene.frame_set(frame_current, subframe=frame_subframe)