//
//  unrle.cpp
//  pbmCompress
//
//  Created by Eric Williams on 1/29/18.
//  Copyright © 2018 Zander Nickle. All rights reserved.
//
//#include "unhuff.cpp"
#include "pbmcompress-v1.h"
#include <stdio.h>
#include <iostream>
#include <assert.h>
// file format:
//  2-byte magic number: ch
//  4-byte width (little endian)
//  4-byte height (little endian)
//  huffman encoded data

std::tuple<bool, int, int, std::vector<bool> *> unrle(std::string filename){
//    bool debugFlag = false;
    bool valid;
    int height;
    int width;
    std::vector<uint8_t> * dataIn;
    std::vector<bool> * dataOut = new std::vector<bool>;
    //extract tuple data and add it to values to passforward.
    std::tie(valid, width, height, dataIn) = unhuff(filename);
//    if (debugFlag) {
//        std::cout << "The size of the vector read in by unrle is: " << dataIn->size() << std::endl;
//    }
    if (!valid) {
//        if (debugFlag) {
//            std::cout << "Got invalid CH from unhuff" << std::endl;
//        }
        return std::make_tuple(valid, width, height, dataOut);
    }
    
    //identify the value and run length of each compressed value and decompress it.
    for (int i = 0; i < dataIn -> size(); i++) {
        uint8_t type = dataIn -> at(i) >> 7;
        int  runLength = dataIn -> at(i) & 0x7f;
        assert(type <= 1);
        if (type == 1) {
            for (int j = 0; j < runLength + 1; j++) {//add 1 by convention to get max sized runs of 128
                dataOut -> push_back(type);
            }
        } else if (type == 0){
            for (int j = 0; j < runLength + 1; j++) {
                dataOut -> push_back(type);
            }
        }
    }
//    if (debugFlag) {
//        std::cout << "The size of the vector passed out in unrle is: " << dataOut->size() << std::endl;
//    }
    //create output Tuple with <valid, height, width, dataOut *>
    return std::make_tuple(valid, width, height, dataOut);
}
