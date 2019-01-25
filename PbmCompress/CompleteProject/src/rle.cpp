//
//  rle.cpp
//  Image Compression and Decompression
//
//  Created by Jennifer Niang on 2/12/18.
//  Copyright © 2018 Jennifer Niang. All rights reserved.
//
//  chris_rle.cpp
//  Image Compression and Decompression
//
//  Created by Jennifer Niang on 2/12/18.
//  Copyright © 2018 Jennifer Niang. All rights reserved.
//
//  main.cpp
//  rle
//
//  Created by Chris Roper on 1/26/18.
//  Copyright © 2018 Chris Roper. All rights reserved.
//
#include <stdio.h>
#include <iostream>
#include <vector>
#include <bitset>
#include "pbmcompress-v1.h"


std::tuple<bool, int, int, std::vector<uint8_t>* > rle (std::string filename) {
    auto rl = read_pbm_file(filename);//optimizing the code

    std::vector<uint8_t> *rleVector = new std::vector<uint8_t>;
    int width = std::get<1>(rl);//change variable name
    int height = std::get<2>(rl);//change variable name

    if (!std::get<0>(rl)) {
        return std::make_tuple(false, width, height, rleVector); // if bool is false, forward the uncompressed data
    }

    //std::vector<bool>* fileInput = nullptr;
    std::vector<bool>* fileInput = std::get<3>(rl);

    for(int i = 0; i < fileInput->size(); i++) {
        bool temp = fileInput->at(i);
        int counter = 1;
        while(i < fileInput->size()-1 && temp == fileInput->at(i+1) && counter < 128) {
            counter++;
            i++;
        }
        uint8_t packedByte = ((temp << 7) | (counter-1));
        rleVector->push_back(packedByte);
    }
    delete fileInput;//this was free changed to delete
    
//    for(auto rle : *rleVector) { //printing for testing
//        std::cout << "Runs in RLE Vector in RLE: " << std::bitset<8>(rle) << std::endl;
//    }
    
    return std::make_tuple(true, width, height, rleVector);
}

//int main(int argc, const char * argv[]) {
//    // insert code here...
//    std::vector<bool> *fileInput = new std::vector<bool>;
//    std::vector<uint8_t> *rleVector = new std::vector<uint8_t>;
//    bool test0 = true;
//    int test1 = 5;
//    int test2 = 7;
//    for (int i = 0; i < 250; i++) {
//            fileInput->push_back(false);
//    }
//    for (int i = 0; i < 250; i++) {
//        fileInput->push_back(true);
//    }
//    std::tuple<bool, int, int, std::vector<bool>*> testTupleIn{test0, test1, test2, fileInput};
//
//    //fileInput[49] = 0;
////    for(bool b : fileInput) {
////        std::cout << b << "\n";
////    }
//    //std::cout << "size: " << fileInput.size() << "\n";
//    //int size = fileInput.size()-1;
//    for(int i = 0; i < fileInput->size(); i++) {
//        bool temp = fileInput->at(i);
//
//        //int count = i;
//        int counter = 1;
//        while(i < (fileInput->size()-1) && (temp == fileInput->at(i+1)) && (counter < 128)) {
//            // std::cout << fileInput->at(i) << "\n";
//            //std::cout << i << "\n";
//            counter++;
//            i++;
//        }
//        std::cout << "counter: " << counter << "\n";
//        uint8_t packedByte = ((temp << 7) | (counter-1));
//        rleVector->push_back(packedByte);
//    }
//
//    for(uint8_t byte : *rleVector) {
//        std::bitset<8> x(byte);
//        std::cout << x << "\n";
//    }
//    std::cout << "Hello, World!\n";
//    return 0;
//}
