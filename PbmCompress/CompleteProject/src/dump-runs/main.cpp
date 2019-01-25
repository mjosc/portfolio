//
//  main.cpp
//  dump-runs
//
//  Created by Zander Nickle on 1/30/18.
//  Copyright © 2018 Zander Nickle. All rights reserved.
//

#include <iostream>
#include <vector>
#include <fstream>
#include <tuple>
#include <bitset>
#include "../pbmcompress-v1.h"

int main(int argc, const char * argv[]) {
  std::tuple<bool, int, int, std::vector<uint8_t> *> data = rle(argv[1]);
  if (std::get<0>(data) == false) {
    std::cout << "Error\n"; // TODO: what type of error?
    return 1;
  }

  std::ofstream file;
  file.open(argv[2], std::ios::binary); // implicit flag: std::ios::out
  file << 'c' << 'h';

  int width = std::get<1>(data);
  int height = std::get<2>(data);

  // TODO: simplify lines 32 - 39 & understand purpose of this function

  file << (unsigned char) (width & 0xff);
  file << (unsigned char) (width >> 8 & 0xff);
  file << (unsigned char) (width >> 16 & 0xff);
  file << (unsigned char) (width >> 24 & 0xff);
  file << (unsigned char) (height & 0xff);
  file << (unsigned char) (height >> 8 & 0xff);
  file << (unsigned char) (height >> 16 & 0xff);
  file << (unsigned char) (height >> 24 & 0xff);

  std::vector<uint8_t> *runs = std::get<3>(data);

  for (auto run: *runs) {
    file << run; // TODO: write vs. << here?
  }
  file.close();
  return 0;
}
