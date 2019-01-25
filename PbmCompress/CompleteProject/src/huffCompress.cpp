//
//  huffCompress.cpp
//  huffmanEncoding
//
//  Created by Madeline Luke on 1/30/18.
//  Copyright © 2018 Madeline Luke. All rights reserved.
//
#include <stdio.h>
#include <iostream>
#include <tuple>
#include <string>
#include <bitset>
#include <assert.h>

#include "huff.h"
#include "pbmcompress-v1.h"


std::tuple<bool, int, int, std::vector<uint8_t> *> huff(std::string filename) {
    //bool debug = true; //debug flag for testing
    
//    if(debug) { //printing for debugging
//        std::cout << "-----STARTING HUFF-----\n";
//    }
    
    bool valid;
    int width;
    int height;
    std::vector<uint8_t>* rleEncoded; //vector holding input rle runs
    std::tie(valid, width, height, rleEncoded) = rle(filename); //make rle tuple
    
//    if(debug) { //add assertions for debugging
//        assert(width > 0);
//        assert(height > 0);
//    }
    
    if (!valid) { //check for a valid tuple
        return std::make_tuple(valid, width, height, rleEncoded);
    }
    
//    if(debug) { //printing for debugging
//        std::cout << "TUPLE FROM RLE:\n";
//        std::cout << "Status: " << valid << " Width: " << width << " Height: " << height << "\n";
//    }
    
    std::vector<uint8_t>* huffedData = new std::vector<uint8_t>; //vector to store huffed data
  
    int bit = 0; //used as a counter for individual bits
    int byte = -1; //used as a counter for each individual byte within vector
    
    int rleEncodedSize = rleEncoded->size();
    
//    if(debug) { //add assertion for debugging
//        assert(rleEncodedSize >= 0);
//    }

    for (int i = 0; i < rleEncodedSize; i++) { //loop through entire input vector
        std::string code = huff_table[rleEncoded->at(i)] ; //code depending on the number value in the huff table
        for (int k = 0; k < code.length(); k++) { //loop thru string

            if (bit == 0) { //preps a byte to be filled
                huffedData->push_back(0); //fills byte with default 0s
                byte++; //increments to next byte in vector
            }

            if (code[k] == '1') { //converts char '1' to an actual bit '1'
                uint8_t temp = 1;
                temp = temp << (7 - bit);
                huffedData->at(byte) |= temp;
            }

            if (bit == 7) { //resets to 1st bit in byte
                bit = -1;
            }
            bit++; //move to next bit
        }
    }
    delete rleEncoded; //delete input vector from rle
    
//    if(debug) { //printing for debugging
//        for(auto bytes : *huffedData) {
//            std::cout << "Huffed data bits in HUFF: " << std::bitset<8>(bytes) << std::endl;
//        }
//        std::cout << "-----FINISHING HUFF-----\n";
//    }

    return std::make_tuple(valid, width, height, huffedData) ;
}
