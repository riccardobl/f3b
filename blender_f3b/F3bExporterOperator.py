import bpy,os
from bpy.props import ( BoolProperty )
from bpy_extras.io_utils import ExportHelper

from .F3bContext import *
from . import F3bExporter

def menu_func_export_f3b(self, context):
    self.layout.operator(F3bExporterOperator.bl_idname, text="f3b (.f3b)")


def register():
    bpy.utils.register_class(F3bExporterOperator)
    bpy.types.TOPBAR_MT_file_export.append(menu_func_export_f3b)

def unregister():
    bpy.types.TOPBAR_MT_file_export.remove(menu_func_export_f3b)
    bpy.utils.unregister_class(F3bExporterOperator)

class F3bExporterOperator(bpy.types.Operator, ExportHelper):
    bl_idname = "export_scene.f3b"
    bl_label = "Export f3b"
    filename_ext = ".f3b"

    #Properties
    optionExportSelection: BoolProperty(
        name = "Export Selection", 
        description = "Export only selected objects", 
        default = True
    )

    optionExportTangents: BoolProperty(
        name = "Export Tangents", 
        description = "", 
        default = True
    )


    optionToDDS: BoolProperty(
        name = "Convert textures to dds", 
        description = "", 
        default = True
    )

    def execute(self, context):
        print("Export f3b")
        
        if self.filepath.endswith(".f3h"):
            self.filepath[:len(self.filepath)-len("f3h")]+"f3b"
        
        scene: bpy.types.Scene=context.scene
        topath=os.path.dirname(self.filepath)

        f3bctx=F3bContext(self,self.filepath,topath)
        data,collectionsData=F3bExporter.startExport(f3bctx,scene)

        file = open(self.filepath, "wb")
        file.write(data.SerializeToString())
        file.close()

        headerFile=self.filepath[:len(self.filepath)-len("f3b")]+"f3h"
        print("Write header in"+headerFile)

        file = open(headerFile, "wb")
        file.write(collectionsData.SerializeToString())
        file.close()

        return {'FINISHED'}
