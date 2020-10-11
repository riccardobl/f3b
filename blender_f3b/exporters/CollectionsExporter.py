import f3b
import f3b.header_pb2
import f3b.custom_params_pb2
import f3b.animations_kf_pb2
import f3b.physics_pb2
from . import Relations
from ..F3bContext import *
from ..Utils import *
from .. import Logger as log

def export(ctx: F3bContext,headerData: f3b.header_pb2.Header,scene: bpy.types.Scene):
    collectionData={}

    for col in  bpy.data.collections:
        if col.library: continue # Ignore linked collections
        for obj in col.objects:
            if not ctx.isExportable(obj): continue
            if obj.parent!=None: continue # only first level
            cuid=getCollectionUid(col,None)
            oid=ctx.idOf(obj)
            if not cuid in collectionData:  collectionData[cuid]=[]
            if not oid in collectionData[cuid]: collectionData[cuid].append(oid)

    for k,vv in collectionData.items():
        collectionf3b  = headerData.collections.add()
        collectionf3b.id=k
        for v in vv:
            collectionf3b.objects.append(v)

        