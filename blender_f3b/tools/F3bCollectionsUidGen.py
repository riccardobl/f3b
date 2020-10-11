import bpy
import uuid
from ..Utils import *
from bpy.app.handlers import persistent

@persistent
def computeCollectionsUids(dummy):
    for c in  bpy.data.collections:
        if not uid_key in c or not c[uid_key]:  c[uid_key]=str(uuid.uuid4())
        print(c.name+" has assigned f3b_uid "+c[uid_key])
        getCollectionUid(c,c[uid_key])
        

def register():
    print("Register collection uid generator")
    bpy.app.handlers.save_pre.append(computeCollectionsUids)

def unregister():
    bpy.app.handlers.save_pre.remove(computeCollectionsUids)