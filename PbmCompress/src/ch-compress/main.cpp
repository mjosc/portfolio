//
//  main.cpp
//  ch-compress
//
//  Created by Zander Nickle on 1/30/18.
//  Copyright © 2018 Zander Nickle. All rights reserved.
//

#include "../pbmcompress-v1.h"
#include <bitset>
#include <fstream>
#include <iostream>
#include <tuple>
#include <vector>

int main(int argc, const char *argv[]) {
  // retrieve compressed data
  std::tuple<bool, int, int, std::vector<uint8_t> *> data = huff(argv[1]);
  if (!std::get<0>(data)) {
    std::cout << "Error: incomplete compression.\n";
    return 1;
  }

  std::ofstream file;
  file.open(argv[2], std::ios::binary); // implicit flag: std::ios::out
  file << 'c' << 'h';

  int width = std::get<1>(data);
  int height = std::get<2>(data);
  file.write(reinterpret_cast<char *>(&width), sizeof(width));
  file.write(reinterpret_cast<char *>(&height), sizeof(height));

  // use write for binary file (as opposed to << operator)
  std::vector<uint8_t> bytes = *std::get<3>(data);
  for (uint8_t byte : bytes) {
    file.write(reinterpret_cast<char *>(&byte), sizeof(byte));
  }

  delete std::get<3>(data);
  std::get<3>(data) = nullptr;
  file.close();
  return 0;
}
