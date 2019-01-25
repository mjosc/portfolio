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

//read_ch_file() function optimization by Irene Yeung.

/** created by adam miles.
 *
 *
 */
std::tuple<bool, int, int, std::vector<uint8_t> *> read_ch_file(std::string filename) {
  std::ifstream stream(filename, std::ios::in | std::ios::binary);
  if (!stream) {
    perror("FAIL: Not a valid ch file. Cannot open :(\n");
    return std::make_tuple(false, 0, 0, (std::vector<uint8_t> *) 0);
  }
  stream.seekg(0, std::ios::end);
  const int buf_len = stream.tellg(); //length of bytestream
  stream.seekg(0, std::ios::beg); //reset position to beginning to start reaidng data
  char buff[buf_len];
  stream.read(buff,buf_len); //Read all binary data in at once instead of using iterator
  if (buff[0] !='c' | buff[1]!='h'){
    perror("FAIL: Not a valid ch file. Cannot open :(\n");
    return std::make_tuple(false, 0, 0, (std::vector<uint8_t> *) 0);
  }
  const int W_START = 2;
  const int H_START = 6;
  uint32_t width = 0x0;
  uint32_t height = 0x0;
  int k = 0;
  int j = H_START;
  for (int i = W_START; i < H_START; i++, j++) {
    width |= (uint8_t)buff[i] << (8 * k);
    height |= (uint8_t)buff[j] << (8 * k);
    k++;
  }
  const int HUFF_DATA_START = 10;
  const int HUFF_DATA_SIZE = buf_len - HUFF_DATA_START;
  j=0;
  std::vector<uint8_t >* compressed = new std::vector<uint8_t >(HUFF_DATA_SIZE);
  for (int i = HUFF_DATA_START; i < buf_len; i++, j++) {
    (*compressed)[j] = buff[i];
  }
  return std::make_tuple(1, (int)width, (int)height, compressed);
}
