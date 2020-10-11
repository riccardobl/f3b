import f3b,bpy
from .F3bContext import *
from . import Logger as log

# Exporters #
from .exporters import TObjectExporter
from .exporters import SpeakersExporter
from .exporters import GeometryExporter
from .exporters import MaterialExporter
from .exporters import LightExporter
from .exporters import SkeletonExporter
from .exporters import ActionExporter
from .exporters import PhysicsExporter
# from .exporters import CollisionPlanes
# from .exporters import EmittersExporter
# from .exporters import ForceFieldExporter
from .exporters import CollectionsExporter
#############
from .tools import F3bCollectionsUidGen


def startExport(ctx: F3bContext ,scene: bpy.types.Scene):
    F3bCollectionsUidGen.computeCollectionsUids(None)

    log.info("Export to "+ ctx.topath)
    data = f3b.datas_pb2.Data()
    # CollisionPlanes.export(ctx,data,scene)
    TObjectExporter.export(ctx,data,scene)
    # EmittersExporter.export(ctx,data,scene)
    SpeakersExporter.export(ctx,data,scene)
    GeometryExporter.export(ctx,data,scene)
    MaterialExporter.export(ctx,data,scene)
    LightExporter.export(ctx,data,scene)
    SkeletonExporter.export(ctx,data,scene)
    ActionExporter.export(ctx,data,scene)
    PhysicsExporter.export(ctx,data,scene)
    # ForceFieldExporter.export(ctx,data,scene)


    sceneRelPath=ctx.tofile[len(ctx.topath)+1:]
    print("Found scene relPath "+sceneRelPath)
    headerData = f3b.header_pb2.Header()
    headerData.sceneRelPath=sceneRelPath
    CollectionsExporter.export(ctx,headerData,scene)

    return [data,headerData]
    
