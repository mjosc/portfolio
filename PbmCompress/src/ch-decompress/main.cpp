//
//  main.cpp
//  ch-decompress
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
#include <assert.h>

int main(int argc, const char * argv[]) {
  // retrieve decompressed data
  std::tuple<bool, int, int, std::vector<bool> *> data = unrle(argv[1]);
  if (!std::get<0>(data)) {
    std::cout << "Error: incomplete decompression.\n";
    return 1;
  }

  std::ofstream file;
  file.open(argv[2], std::ios::binary); // implicit flag: std::ios::out
  file << 'P' << '1' << "\n";

  int width = std::get<1>(data);
  int height = std::get<2>(data);
  // format output as text with << operator
  file << width << " " << height << "\n";

  std::vector<bool> runs = *std::get<3>(data);
  for (int i = 0; i < height; i++) {
    for (int j = 0; j < width; j++) {
      file << (int) runs[i * width + j]; // convert bool to 1 or 0
    }
    file << "\n";
  }

  delete std::get<3>(data);
  std::get<3>(data) = nullptr;
  file.close();
  return 0;
}
