//
//  huff.cpp
//  pbmcompress
//
//  Created by Matt Josse on 1/25/18.
//  Copyright © 2018 Matt Josse. All rights reserved.
//

#include <iostream>
#include <tuple>
#include <vector>
#include <string>
#include "huff.h" // Table
#include "pbmcompress-v1.h"

// Returns huffman compressed data. Also returns an error status (bool) as well
// as width and height values. This function is dependent on rle. The rle
// compressed data is the only return data of rle directly manipulated.
std::tuple<bool, int, int, std::vector<uint8_t> *> huff(std::string filename) {
  auto rl = rle(filename);
  if (!std::get<0>(rl)) {
    return rl; // Error forwarding
  }
  std::vector<uint8_t> data = *std::get<3>(rl); // Retrieve rle data
  auto result = new std::vector<uint8_t>();
  int index = 0;
  int count = 0;
  uint8_t byte = 0;
  while (index < data.size()) {
    // Convert matching huffman string to bit pattern
    for (int i = 0; i < table[data[index]].length(); i++) {
      uint8_t mask = 0;
      if (table[data[index]][i] == '1') {
        mask = 1;
      }
      byte <<= 1;
      byte ^= mask;
      count++;
      if (count >= 8) { // Huffman byte is packed
        result->push_back(byte);
        byte = 0;
        count = 0;
      }
    }
    index++; // Next rle byte
  }
  if (count > 0) { // Pad remaining byte
    result->push_back(byte <<= (8 - count));
  }
  delete std::get<3>(rl);
  std::get<3>(rl) = nullptr;
  return std::make_tuple(std::get<0>(rl), std::get<1>(rl), std::get<2>(rl),
                         result);
}


