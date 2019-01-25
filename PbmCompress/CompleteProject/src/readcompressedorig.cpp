#include <tuple>
#include <vector>
#include <string>
#include <fstream>
#include <iostream>
#include <sstream>
#include <map>
#include <queue>
#include <deque>
#include <cassert>

#include "pbmcompress-v1.h"

/** created by adam miles.
 *
 * reads a compressed pbm file, validates the type with the magic number, and reads in the bytes
 * to a tuple for decompression.
 *
 * @param filename a string representing the path and name of the compressed pbm file.
 * @return a tuple with the status, height width, and raw data (byte array) of the compressed
 * image.
 */
std::tuple<bool, int, int, std::vector<uint8_t> *> read_ch_file(std::string filename) {
  
    std::basic_ifstream<char> stream(filename, std::ios::in | std::ios::binary);
    if (!stream.is_open()) {
        perror("FAIL: Not a valid ch file. Cannot open :(\n");
      return std::make_tuple(false, 0, 0, (std::vector<uint8_t> *) 0);
    }

    /** read all bytes into a vector, for partitioning into variables.
     *
     */
    const std::vector<char> raw((std::istreambuf_iterator<char>(stream)),
                                std::istreambuf_iterator<char>() );

    /** check the magic number, make sure it is "ch"
     *
     */
    std::string magic = std::string() + raw[0] + raw[1];
    if (magic != "ch") {
         perror("FAIL: Not a valid ch file. Cannot open :(\n");
      return std::make_tuple(false, 0, 0, (std::vector<uint8_t> *) 0);
    }

    std::vector<uint8_t >* compressed = new std::vector<uint8_t >;   // compressed image byte set.
    uint32_t h = 0;
    uint32_t w = 0;
    const uint32_t temp = 0;
  
  
  /** some bit shifting to reconstruct the height and width values.
   *
   */
    h = (((uint8_t)(raw[5]) | temp) << 24) | (h & ~(0xff << 24));
    h = (((uint8_t)raw[4] | temp) << 16) | (h & ~(0xff << 16));
    h = (((uint8_t)raw[3] | temp) << 8) | (h & ~(0xff << 8));
    h = ((uint8_t)raw[2] | temp) | (h & ~0xff);

    w = (((uint8_t)raw[9] | temp) << 24) | (w & ~(0xff << 24));
    w = (((uint8_t)raw[8] | temp) << 16) | (w & ~(0xff << 16));
    w = (((uint8_t)raw[7] | temp) << 8) | (w & ~(0xff << 8));
    w = ((uint8_t)raw[6] | temp) | (w & ~0xff);

    for (int i = 10; i < raw.size(); i++) {
        compressed->push_back((uint8_t)raw[i]);
    }
    return std::make_tuple(true, (int)w, (int)h, compressed);
}
//
//int main(){
//  std::string file = "CS6015/cs6015-team3/test/chtest/x-s0005.ch";
//  read_ch_file(file);
//}
