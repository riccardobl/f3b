import mathutils,math
import bpy

def cnv_vec3(src, dst):
    dst.x = src[0]
    dst.y = src[1]
    dst.z = src[2]
    return dst
    

def cnv_vec4(src, dst):
    dst.x = src[0]
    dst.y = src[1]
    dst.z = src[2]
    dst.w = src[3]
    return dst

def cnv_vec2(src, dst):
    dst.x = src[0]
    dst.y = src[1]
    return dst


def rotToQuat(obj):
    """ return the rotation of the object as quaternion"""
    if obj.rotation_mode == 'QUATERNION' or obj.rotation_mode == 'AXIS_ANGLE':
        return obj.rotation_quaternion
    else:
        # eurler
        return obj.rotation_euler.to_quaternion()



def cnv_color(src, dst):
    dst.x = src[0]
    dst.y = src[1]
    dst.z = src[2]
    dst.w = 1.0 if len(src) < 4 else src[3]
    return dst
       
def cnv_qtr(src, dst):
    dst.w = src[0]
    dst.x = src[1]
    dst.y = src[2]
    dst.z = src[3]
   

def swizzle_vector(src):
    return [src[0],src[2],-src[1]]


def swizzle_rotation(src):
    return [src[0],src[1],src[3],-src[2]]

def swizzle_scale(src):
    return [src[0],src[2],src[1]]

def swizzle_tangent(src):
    return [src[0],src[2],-src[1],1.0]


    


    


    


def cross_vec3(a, b):
    return [
    a[1] * b[2] - a[2] * b[1],
    a[2] * b[0] - a[0] * b[2],
    a[0] * b[1] - a[1] * b[0]
    ]
    

def dot_vec3(a, b):
    return a[0] * b[0] + a[1] * b[1] + a[2] * b[2]; 

def isset(v, k = None):
    try:
        if (k == None):
            if (v != None  and  v):
                return True
                
            return False
            
        if (v[k] != None  and  v[k]):
            return True
            
        
    except: pass
    return False
    


def fixLightRot(quat):

    qr0 = mathutils.Quaternion((0, 0, 1, 0))  # z forward
    qr0.normalize()
    qr0.rotate(quat)
    qr0.normalize()
    return qr0

def cnv_quatZupToYup(src, dst):
    src0 = src.copy()
    q = mathutils.Quaternion((-1, 1, 0, 0))
    q.normalize()
    src0.rotate(q)
    
    dst.w = src0.w
    dst.x = src0.x
    dst.y = src0.y
    dst.z = src0.z
    return dst
    







def equals_mat4(m0, m1, max_cell_delta):
    for i in range(0, 4):
        for j in range(0, 4):
            d = m0[i][j] - m1[i][j]
            if d<0: d=-d
            if d > max_cell_delta:
                return False
    return True

uid_key="f3b_collection_uid$"

# def getTopObjectId(ctx: F3bContext,obj:bpy.types.Object ):
#     if obj.parent:
#         return getTopObjectId(ctx,obj.parent)
#     else:
#         return ctx.idOf(obj)
        

def getCollectionUid(col,defaultv):
    uuidObj =None

    for o in col.objects:
        if o.name.startswith(uid_key):
            uuidObj=o
            break
    
    if not uuidObj and defaultv: 
        uuidObj = bpy.data.objects.new(uid_key, None)
        col.objects.link(uuidObj)    
        uuidObj.hide_viewport=True
        uuidObj.hide_render=True
        uuidObj[uid_key]=defaultv
    
    return uuidObj[uid_key] if uuidObj and uid_key in uuidObj else None

def getLinkedCollection(obj):
    linkedCollections={}
    for c in  bpy.data.collections:
        if c.library:
            linkedCollections[c.name]=c
    if obj.is_instancer and obj.instance_type=="COLLECTION":
        linkedCollection=linkedCollections[obj.instance_collection.name]
        if linkedCollection:
            f3bUid=None
            for o in linkedCollection.objects:
                if o.name.startswith(uid_key) and uid_key in o:
                    f3bUid=o[uid_key]
                    break
            if f3bUid:
                print(obj.name+" linked from "+linkedCollection.name+" with uuid "+f3bUid)
                return f3bUid
    return None