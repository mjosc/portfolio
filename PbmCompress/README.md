# PbmCompress

The result of a six-person group effort to design and implement a compression system for pbm files. The focus was modularity and a microservice-esque architecture. My primary contribution is demonstrated within the [MyModule](https://github.com/mjosc/portfolio/tree/master/PbmCompress/MyModule) directory. See [CompleteProject](https://github.com/mjosc/portfolio/tree/master/PbmCompress/CompleteProject) for the entirety of the compressor.
  
Though this program does not run over the network and does not represent a large enterprise application, it does show the importance of modular design and some of the potential benefits of a full-scale microservice architecture. For example, each module was built in isolation of all others and has a well-defined API for any module wishing to communicate with it. Furthermore, making changes to individual modules should not break any other module.
