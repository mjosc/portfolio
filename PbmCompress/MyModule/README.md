# pbmcompress (huff.cpp)
**Version 3.0 | Feb. 07, 2018**

This program is the result of a group effort to design and implement a compression system for pbm files. The focus was API development. Each member of a six-person team worked separately on each of the source files. The huffman compression was implemented using a provided huffman table included in huff.h. The program has two independent pieces which may be executed on their own. The first is the compression program. The second is the decompression program. The input is a pbm file and the output, depending on which of the two halves is being run, is either a .ch (compressed huffman) or a decompressed pbm file. The compression and decompression are each two-tier. Run-length and Huffman compression/decompression are both implemented one after the other.

**Personal contribution to this project's source code is huff.cpp.** I did NOT write the tree-builder to generate the huff.h table.

Version 3.0: Implemented memory deallocation for the run-length compression vector passed to the huff function. Minor stylistic changes.
