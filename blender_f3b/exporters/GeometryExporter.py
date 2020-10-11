import f3b
import f3b.datas_pb2
import f3b.custom_params_pb2
import f3b.animations_kf_pb2
import f3b.physics_pb2
from . import Relations
from ..F3bContext import *
from ..Utils import *
from .. import Logger as log
from ..tools import F3bLod

from ..Mesh import *;


def ind(co):
    return (co.x,co.y,co.z)
    

def extract_meshdata(src_mesh, src_geometry, material_index, export_tangents):
    print("Collect mesh data")
    raw_verts=[]
    n_uv_layers=min(9, len(src_mesh.uv_layers)) 

    #Collect vertices
    for  i, poly in enumerate(src_mesh.polygons):        
        is_smooth = poly.use_smooth        
        if (material_index != poly.material_index):
            continue
        for k in poly.loop_indices:
            vl = src_mesh.loops[k]
            vertex = Vertex()
            vertex.from_loop = vl
            vertex.from_vertex = src_mesh.vertices[vl.vertex_index]
            vertex.i=vl.vertex_index
            vertex.n = swizzle_vector(vertex.from_vertex.normal if is_smooth else poly.normal) 
            vertex.p=swizzle_vector(vertex.from_vertex.co)
            vertex.loop_index=k
            if src_mesh.vertex_colors.active:
                c=src_mesh.vertex_colors.active.data[k].color
                vertex.c=[c[0],
                c[1],
                c[2],
                1.0  ] #TODO: support multiple layers (?)
            else:
                vertex.c=[0,0,0,1.0]
            vertex.tx = [[]] * n_uv_layers
            vertex.tg = [[]] * n_uv_layers
            raw_verts.append(vertex)

     #Collect UV and TAN layers for each vert
    for tx_id in range(0,n_uv_layers):
        src_mesh.calc_tangents(uvmap = src_mesh.uv_layers[tx_id].name)            
        for vertex in raw_verts:
            texcoord=src_mesh.uv_layers[tx_id]
            vertex.tx[tx_id].extend(texcoord.data[vertex.loop_index].uv)
            if export_tangents:
                tan = swizzle_vector(vertex.from_loop.tangent)
                btan = swizzle_vector(vertex.from_loop.bitangent)
                vertex.tg[tx_id]=tan
                if dot_vec3(cross_vec3(swizzle_vector(vertex.n),tan),btan) < 0.0:
                    vertex.tg[tx_id].append(-1)
                else:
                    vertex.tg[tx_id].append(1)
 

    #Build mesh and deduplication
    mesh=Mesh()
    dedupli={}
    indexc=0
    for vertex in raw_verts:
        index=None
        h=hash(vertex)
        if h in dedupli:
            index=dedupli[h]
        else:
            index=indexc
            dedupli[h]=index
            indexc=indexc+1
            mesh.verts.append(vertex)
        mesh.indexes.append(index)


    print("Number of points: " + str(len(mesh.indexes)))
    print("Number of unique vertices: " + str(len(mesh.verts)))  

    armature = src_geometry.find_armature()
    if (armature):
        mesh.has_skin=True
        groupToBoneIndex = make_group_to_bone_index(armature, src_geometry)
        for vertex in mesh.verts:
            find_bone_influence(src_mesh.vertices,vertex.i, groupToBoneIndex, mesh.skin.boneCount,mesh.skin.boneIndex, mesh.skin.boneWeight)
    return mesh
    

def find_bone_influence(vertices, index, groupToBoneIndex, boneCount, boneIndexOut,    boneWeightOut):
    nbones=0
    for g in vertices[index].groups:
        groupIndex=g.group
        boneIndex = groupToBoneIndex[groupIndex]
        boneWeight = g.weight
        if ((boneIndex >= 0) and (boneWeight > 0)):
            boneIndexOut.append(boneIndex)
            boneWeightOut.append(boneWeight)                
            nbones+=1
    boneCount.append(nbones)
   
    
def make_group_to_bone_index(armature, src_geometry):
    groupToBoneIndex = []
    bones = armature.data.bones    
    bones_table = [b.name for b in bones]   
    for  group in src_geometry.vertex_groups:
        groupName = group.name
        try:
            index = bones_table.index(group.name)            
        except(ValueError):
            index = -1            
        groupToBoneIndex.append(index)
        if (index < 0):
            print("groupVertex  %s can't be bound to bone %s" % (index,groupName))  
        else:
            print("groupVertex %s bound to bone %s" % (index,groupName))  

      
    return groupToBoneIndex
    

def export_meshes(ctx: F3bContext,src_geometry: bpy.types.Object,scene: bpy.types.Scene, meshes,lodLevel):
    mode =  'RENDER'
    # Set up modifiers whether to apply deformation or not
    # tips from https://code.google.com/p/blender-cod/source/browse/blender_26/export_xmodel.py#185
    mod_armature = []
    # mod_state_attr =  'show_render'
    for mod in src_geometry.modifiers:
        if mod.type == 'ARMATURE':
            mod_armature.append((mod, getattr(mod, 'show_render'),getattr(mod, 'show_viewport' ) ))

    tmp_modifier=[]
    #Add triangulate modifier
    trimod=src_geometry.modifiers.new("TriangulateForF3b","TRIANGULATE")
    trimod.keep_custom_normals=True
    trimod.ngon_method="BEAUTY"
    trimod.quad_method="BEAUTY"    
    tmp_modifier.append(trimod)

    if  hasattr(src_geometry.data, 'use_auto_smooth') and src_geometry.data.use_auto_smooth:
        print("Use edge split")
        edgesplit=src_geometry.modifiers.new("EdgeSplitForF3b","EDGE_SPLIT")
        edgesplit.split_angle = src_geometry.data.auto_smooth_angle
        edgesplit.use_edge_angle = True
        tmp_modifier.append(edgesplit)



    # -- without armature applied
    for mod in mod_armature:
        setattr(mod[0], 'show_render', False)
        setattr(mod[0], 'show_viewport', False)

    bpy.context.view_layer.update()

    # New apis
    depsgraph=bpy.context.evaluated_depsgraph_get()
    mesh_owner=src_geometry.evaluated_get(depsgraph)
    src_mesh =  mesh_owner.to_mesh(preserve_all_data_layers=True, depsgraph=depsgraph)

    # Restore modifier settings
    for mod in mod_armature:
        setattr(mod[0], 'show_render', mod[1])
        setattr(mod[0], 'show_viewport', mod[2])

    # dst.id = cfg.id_of(src_geometry.data)
    # dst.name = src_geometry.name
    dstMap = {}
    for  i, face in enumerate(src_mesh.polygons):        
        material_index = face.material_index
        if material_index not in dstMap:
            dstMap[material_index] = meshes.add()


    for material_index, dst in dstMap.items():
        dst.primitive = f3b.datas_pb2.Mesh.triangles
        dst.id = ctx.idOf(src_mesh) + "_" + str(material_index)
        dst.name = src_geometry.data.name + "_" + str(material_index)
        dst_mesh=dst
        dst.lod=lodLevel

        #Collect mesh data 
        mesh=extract_meshdata(src_mesh,src_geometry,material_index,ctx.cfg.optionExportTangents)   

        positions = dst_mesh.vertexArrays.add()
        positions.attrib = f3b.datas_pb2.VertexArray.position
        positions.floats.step = 3
        
        normals = dst_mesh.vertexArrays.add()
        normals.attrib = f3b.datas_pb2.VertexArray.normal
        normals.floats.step = 3
        
        indexes = dst_mesh.indexArrays.add()
        indexes.ints.step = 3

        texcoords=[]
        texcoords_ids=[f3b.datas_pb2.VertexArray.texcoord,f3b.datas_pb2.VertexArray.texcoord2,f3b.datas_pb2.VertexArray.texcoord3,f3b.datas_pb2.VertexArray.texcoord4,f3b.datas_pb2.VertexArray.texcoord5,f3b.datas_pb2.VertexArray.texcoord6,f3b.datas_pb2.VertexArray.texcoord7,f3b.datas_pb2.VertexArray.texcoord8]

        if mesh.verts[0].tg: 
            tangents_ids=[f3b.datas_pb2.VertexArray.tangent,f3b.datas_pb2.VertexArray.tangent2,f3b.datas_pb2.VertexArray.tangent3,f3b.datas_pb2.VertexArray.tangent4,f3b.datas_pb2.VertexArray.tangent5,f3b.datas_pb2.VertexArray.tangent6,f3b.datas_pb2.VertexArray.tangent7,f3b.datas_pb2.VertexArray.tangent8]
            tangents=[]

        print("Found ",len(mesh.verts[0].tx)," uvs")
        for i in range(0,min(9, len(mesh.verts[0].tx))):
            texcoords.append(dst_mesh.vertexArrays.add())
            texcoords[i].attrib=texcoords_ids[i]
            texcoords[i].floats.step = 2
            if mesh.verts[0].tg: 
                tangents.append(dst_mesh.vertexArrays.add())
                tangents[i].attrib = tangents_ids[i]
                tangents[i].floats.step = 4
            

        if mesh.verts[0].c:
            colors = dst_mesh.vertexArrays.add()
            colors.attrib = f3b.datas_pb2.VertexArray.color
            colors.floats.step = 4



        indexes.ints.values.extend(mesh.indexes)
        for v in mesh.verts:
            positions.floats.values.extend(v.p)
            normals.floats.values.extend(v.n)
            if v.c:
                colors.floats.values.extend(v.c)
            if v.tx:
                for i,tx in enumerate(v.tx):
                    texcoords[i].floats.values.extend(tx)
                    if v.tg:
                        tangents[i].floats.values.extend(v.tg[i])            

        if mesh.has_skin:
            dst_skin=dst_mesh.skin
            dst_skin.boneCount.extend(mesh.skin.boneCount)
            dst_skin.boneIndex.extend(mesh.skin.boneIndex)
            dst_skin.boneWeight.extend(mesh.skin.boneWeight)
    #New apis
    mesh_owner.to_mesh_clear()

    for m in tmp_modifier:
        src_geometry.modifiers.remove(m)

    return dstMap


def export(ctx: F3bContext,data: f3b.datas_pb2.Data,scene: bpy.types.Scene):
    for obj in scene.objects: # type: bpy.types.Object
        if not ctx.isExportable(obj) or obj.holdout_get():
            #sprint("Skip ",obj,"not selected/render disabled")
            continue
        if obj.type == 'MESH'  or obj.type=="CURVE":
            if (obj.type != 'MESH'  or len(obj.data.polygons) != 0) and ctx.checkUpdateNeededAndClear(obj.data):
                matXmeshLods={}

                # Collect meshes and their lods
                for lodLevel in range(0,4):
                    
                    if not F3bLod.selectLod(obj,lodLevel): #Lod doesnt exist
                        log.debug("Lod level "+str(lodLevel)+" not available. Break")
                        break
                    log.debug("Export lod "+str(lodLevel))
                    meshes = export_meshes(ctx, obj, scene, data.meshes,lodLevel)

                    for material_index, mesh in meshes.items(): 
                        msh=matXmeshLods[material_index] if material_index in matXmeshLods else None
                        if msh==None:
                            msh=matXmeshLods[material_index]=[None]*4
                            
                        msh[lodLevel]=mesh
                
                F3bLod.selectLod(obj,0)

                for material_index,meshes in matXmeshLods.items():       
                    lodZero=None               
                    for lodLevel in range(0,4):
                        mesh=meshes[lodLevel]
                        if not mesh: continue

                        #first lod is attached to the object and have materials
                        if lodZero==None:
                            lodZero=mesh.id

                            # several object can share the same mesh
                            for obj2 in scene.objects:
                                if obj2.data == obj.data: 
                                        Relations.add(ctx,data,mesh.id,ctx.idOf(obj2))

                            if material_index > -1 and material_index < len(obj.material_slots):
                                src_mat = obj.material_slots[material_index].material
                                Relations.add(ctx,data,ctx.idOf(src_mat),mesh.id)

                        else: #Other lods are attached to the first lod and are plain meshes
                            Relations.add(ctx,data,mesh.id,lodZero)

            else:
                print("Skip ",obj,"already exported")