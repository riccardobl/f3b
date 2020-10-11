import f3b
import f3b.datas_pb2
import f3b.custom_params_pb2
import f3b.animations_kf_pb2
import f3b.physics_pb2
from . import Relations
from ..F3bContext import *
from ..Utils import *
from .. import Logger as log

def export_light(ctx: F3bContext,src, dst):
    dst.id = ctx.idOf(src)
    dst.name = src.name
    kind = src.type
    if kind == 'SUN' or kind == 'AREA' or kind == 'HEMI':
        dst.kind = f3b.datas_pb2.Light.directional
        dst.cast_shadow = src.use_shadow
    elif kind == 'POINT':
        dst.kind = f3b.datas_pb2.Light.point     
        dst.cast_shadow = src.use_shadow   
        dst.radial_distance.max =src.cutoff_distance #TODO: rename
    elif kind == 'SPOT':
        dst.kind = f3b.datas_pb2.Light.spot
        dst.spot_angle.max = src.spot_size * 0.5
        dst.spot_angle.linear.begin = (1.0 - src.spot_blend)
        dst.cast_shadow = src.use_shadow
    

    cnv_color(src.color, dst.color)
    dst.intensity = src.energy
    print("Light energy "+str(src.energy))


def export(ctx: F3bContext,data: f3b.datas_pb2.Data,scene: bpy.types.Scene):
    for obj in scene.objects:
        if not ctx.isExportable(obj):
            continue
        if obj.type == 'LAMP' or obj.type == 'LIGHT':
            src_light = obj.data
            if ctx.checkUpdateNeededAndClear(src_light):
                dst_light = data.lights.add()
                export_light(ctx,src_light, dst_light)
            Relations.add(ctx,data,ctx.idOf(src_light),ctx.idOf(obj))
