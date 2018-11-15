//
//  Timer.hpp
//  SpatialPartitioning
//
//  Created by Matt Josse on 7/30/18.
//  Copyright © 2018 Matt Josse. All rights reserved.
//

#pragma once
#include "Test.hpp"

template<int Dimension>
class Timer {

private:

  Stopwatch sw;
  Test<Dimension> probe;
  std::unique_ptr<PartitionModel<Dimension>> model;
  std::vector<Point<Dimension>> data; // will this be copying? how do I avoid this?

  std::vector<double> result;

  int noise_reduce_num_1 = 3;
  int noise_reduce_num_2 = 100;

public:

  Timer(Test<Dimension> p, int id) {

    probe = p;
    model = probe.getModel(id); // used here to specify which model

  }

  void knn(std::vector<int> k_values, int dist_type) { // default generators

    for (int i = 0; i < k_values.size(); i++) {
      int k = k_values[i];
      for (int j = 0; j < noise_reduce_num_1; j++) {
        auto dist = probe.getDistribution(dist_type, k_values[k_values.size() - 1]); // k 0 -> max
//        model = std::unique_ptr<PartitionModel<Dimension>>()
      }
    }


  }


};
