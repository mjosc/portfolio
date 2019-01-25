//
//
//
//  Optimized by Irene Yeung
//  Optimization of read_pbm_file() for x-0005.pbm:
//  Preoptimized execution time: 1.69 seconds
//  Postoptimized execution time: 339.0 milliseconds
//

#include <stdio.h>
#include <tuple>
#include <vector>
#include <string>
#include <fstream>
#include <iostream>
#include <sstream>
#include <cassert>
#include "pbmcompress-v1.h"

/** created by adam miles.
 * opens a pbm file, determines whether it is formatted correctly, and
 *
 * @param filename
 * @return
 */
std::tuple<bool, int, int, std::vector<bool> *> read_pbm_file(std::string filename) {
  //Check file open status first before unnecessarily initizializing/assigning local variables
  std::ifstream file(filename);
  if (!file) {
    perror("FAIL: Could not find pbm file :(\n");
    return std::make_tuple(0, 0, 0, (std::vector<bool> *) 0); //if file open failure, no need for dynamic memory allocation of raw data vector, just return empty tuple
  }
  std::string line;
  getline(file, line);
  if (line != "P1") {
    perror("FAIL: Not a valid PBM file. Cannot open :(\n");
    return std::make_tuple(0, 0, 0, (std::vector<bool> *) 0);
  }
  int height = 0;
  int width = 0;
  std::string w,h;
  file >> w >> h;
  width = std::stoi(w);
  height = std::stoi(h);
  std::vector<bool> * raw = new std::vector<bool>(height * width, 0);
  //Initialize vector with known size
  
  //Initialize values to 0 so bits that are 0 dont need to be modified later
  char c;
  int i = 0;
  const size_t BUFF_SIZE = 1000; //Tested different buffer sizes, 1000 optimal
  char buf[BUFF_SIZE]; //Read in file chunks at a time
  do {
    file.read(buf, BUFF_SIZE); //reads in remaining chars if< BUFF_SIZE
    std::streamsize charsread = file.gcount();
    int k = 0;
    
//  while(getline(file,nextline)){ //852 ms using getline
//  while (file.get(c)) { //1.05s using while(file.get(c))
    while (k < charsread){
      c = buf[k++];
      switch(c) { //Use switch/case instead of multiple if loops
        case('1'):
          (*raw)[i++] = 1; //Use [] operator instead of push_back for performance
          break;
        case('0'): //Already initialized elements to 0 in contructor, saves time here
          ++i;
          break;
        case('\n'):
          break;
        default: //Must be invalid character, no need to complete while loop
          printf("Invalid character read %c\n", c);
          return std::make_tuple(0, 0, 0, (std::vector<bool> *) 0); //exit function immediately
      }
    }
  } while(file); //while ifstream has chars to read
  file.close();
//  assert(i == height * width); //check bits read matches dimensions
  return std::make_tuple(1, width, height, raw);
}
