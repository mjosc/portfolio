//
//  main.cpp
//  ch-display
//
//  Created by Zander Nickle on 1/30/18.
//  Copyright © 2018 Zander Nickle. All rights reserved.
//

#include <iostream>
#include <string>
#include "CImg.h"

using namespace cimg_library;

int main(int argc, const char * argv[]) {
  CImg<unsigned char> img(argv[1]);
  img.display();
  return 0;
}
