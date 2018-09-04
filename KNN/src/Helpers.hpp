//
//  Helpers.hpp
//  SpatialPartitioning
//
//  Created by Matt Josse on 7/19/18.
//  Copyright © 2018 Matt Josse. All rights reserved.
//

#pragma once

#include <ostream>
#include "KDTree.hpp"

// TODO: Is there no way to place these helpers inside the data structure
// classes so as to print arrays and vectors for data of dimensions greater
// than 2?

// floats
std::ostream& operator<<(std::ostream& os, const std::array<float, 2>& arr) {
  os << "[";
  for (int d = 0; d < arr.size(); d++) {
    os << arr[d];
    if (d != arr.size() - 1) {
      os << ", ";
    }
  }
  os << "]";
  return os;
}

// integers
std::ostream& operator<<(std::ostream& os, const std::array<int, 2>& arr) {
  os << "[";
  for (int d = 0; d < arr.size(); d++) {
    os << arr[d];
    if (d != arr.size() - 1) {
      os << ", ";
    }
  }
  os << "]";
  return os;
}

// vectors
std::ostream& operator<<(std::ostream& os, const std::vector<Point<2>>& vec) {
  os << "[";
  for (int i = 0; i < vec.size(); i++) {
    os << vec[i];
    if (i != vec.size() - 1) {
      os << ", ";
    }
  }
  os << "]";
  return os;
}

// vectors<int>
std::ostream& operator<<(std::ostream& os, const std::vector<int>& vec) {
  os << "[";
  for (int i = 0; i < vec.size(); i++) {
    os << vec[i];
    if (i != vec.size() - 1) {
      os << ", ";
    }
  }
  os << "]";
  return os;
}

template<int Dimension>
bool operator==(std::vector<Point<Dimension>> lhs, std::vector<Point<Dimension>> rhs) {
  if (lhs.size() != rhs.size()) {
    return false;
  }
  // Compare all dimensions
  for (int i = 0; i < lhs.size(); i++) {
    for (int dim = 0; dim < Dimension; dim++) {
      if (lhs[i][dim] != rhs[i][dim]) {
        return false;
      }
    }
  }
  return true;
}

//std::ostream& operator<<(std::ostream& os, const std::vector<int>& vec) {
//  for (int i = 0; i < vec.size(); i++) {
//    os << vec[i];
//    if (i != vec.size() - 1) {
//      os << ", ";
//    }
//  }
//  return os;
//}

