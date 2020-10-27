# F3b

### A simple blender2jmonkey exchange format.

This is  a special purpose format that has only one goal: export scenes from blender and import them in [jMonkeyEngine](https://github.com/jMonkeyEngine/jmonkeyengine) as closely as possible with the lowest possible effort.

This is not an alternative to [glTF](https://www.khronos.org/gltf/) or other general purpose formats, since its features are specifically designed for  [jMonkeyEngine](https://github.com/jMonkeyEngine/jmonkeyengine)  and blender, however being based on protobuf allows it to be easily adaptable and extendable.

The  [jMonkeyEngine](https://github.com/jMonkeyEngine/jmonkeyengine)  importer comes with an experimental animation system designed around java8 functional interfaces.

## Features
- RigidBodies 
- Physics Joints  (only generic joints for now)
- Custom materials (mappable with the use of group nodes in blender)
- jME PBR material
- Spatial animations with f3banimations
- Bone animations with f3banimations 
- Optional Automatic DDS conversion and compression on export
- Support for shared mesh/materials (eg. Duplicated-Linked)
- Automatic triangulation
- Tangents export
- Autosmooth normals support
- [VHACD](https://github.com/riccardobl/jme3-bullet-vhacd) decomposition

## Install in blender

### Install addon
1. Go to the release page
2. Download io_scene_f3b.zip
3. Download f3b_nodes.blend.zip
4. In blender Edit -> Preferences -> Addons
5. Click Install
6. Select  io_scene_f3b.zip
7. Enable the addon

### Install material nodes
1. Go to the release page
2. Download f3b_nodes.blend.zip
3. Extract f3b_nodes.blend.zip
4. In blender File -> Append
5. Select f3b_nodes.blend
6. Append: Materials -> Nodes . This will append the custom nodes used in f3b materials.
7. Save the startup file: File -> Defaults -> Save startup file
8. Now everytime you open blender it will have a Nodes material containing all the f3b nodes, skip point 7 if you don't want this 

## Install in jMonkeyEngine
1. Add the repo
```gradle
repositories {
    maven { url  "https://dl.bintray.com/riccardo/f3b"  }
    maven { url "http://dl.bintray.com/riccardo/v-hacd" }
    maven { url "https://jitpack.io" }    
}
```
2. Add the dependencies
```gradle
dependencies{
    implementation "com.github.riccardobl:jme3-bullet-vhacd:1.0.2"
    implementation "wf.frk.f3b:jme_f3b:0.9"
}
```

## Local build

1. Build (skip tests)
```console
gradle build -xtest
```
2. Copy blender_f3b in blender's addon folder (or create a symlink)

3. Optional: install maven artifacts
```console
gradle install -xtest
```

## Usage
```java

// Initialize the loader (call this only once!)
F3bLoader.init(assetManager);

// load a model
Spatial loaded=loadModel(assetManager,bulletAppState,rootNode,"mymodel.f3b");
// loaded.soSomething();

// load another model
Spatial loaded2=loadModel(assetManager,bulletAppState,rootNode,"mysecondNode.f3b");


void loadModel(
        AssetManager assetManager,
        BulletAppState bulletAppState,
        Node rootNode, 
        String myModel
){

    // Define what/how to load
    F3bKey modelKey=new F3bKey("myModel.f3b");
    modelKey.usePhysics(new BulletPhysicsLoader()); // enable physics loader
    modelKey.useEnhancedRigidbodies(true); // improved rigidbody handling
    // ... more settings ...

    // Prepare runtime loader
    F3bRuntimeLoader rloader=F3bRuntimeLoader.instance();
    rloader.attachSceneTo(rootNode); // where to attach the scene
    rloader.attachLightsTo(rootNode); // where to attach the lights
    rloader.attachPhysicsTo(bulletAppState.getPhysicsSpace()); // where to attach the physics

    // Load!
    Spatial loaded=rloader.load(assetManager, modelKey);

    // `loaded` and its lights are already attach to the rootNode by the runtime loader

    return loaded;
}

```

## Development notes
Some IDEs can't load protobuf subprojects, so you might need to run  `gradle build -x test`  once before importing this project, and everytime the f3b format definition is changed, this command will build and generate the missing java files in the protobuf subproject.


## License
BSD 3-Clause License

Copyright (c) 2020, Riccardo Balbo
All rights reserved.

See [LICENSE](LICENSE) for the full license details.

-----

This product uses the following thirdparty libraries, released under the specified licenses:

PROTOBUF2: [LICENSE](https://github.com/protocolbuffers/protobuf/blob/master/LICENSE)

Log4J2: [Apache License, Version 2.0](https://logging.apache.org/log4j/2.x/license.html)

V-HACD: [BSD 3-Clause "New" or "Revised" License](https://github.com/kmammou/v-hacd/blob/master/LICENSE)

---

The code was originally forked from [xbuf](https://github.com/xbuf) (Xbuf is licensed under public domain)