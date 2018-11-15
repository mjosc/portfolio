//
//  main.cpp
//  SpatialPartitioning
//
//  Created by Matt Josse on 6/19/18.
//  Copyright © 2018 Matt Josse. All rights reserved.
//

#include <iostream>
#include "BucketKNN.hpp"
#include "Generators.hpp"
#include "KDTree.hpp"
#include "Helpers.hpp"
#include "QuadTree.hpp"
#include <algorithm>
#include "Test.hpp"
#include "Timer.hpp"
#include <fstream>
#include <iostream>

int main(int argc, const char * argv[]) {

  Stopwatch sw2;
  sw2.start();

  const int dim = 10;

  int max_size = 10000;
  //  int k = max_size / 10; // default K (default N is max_size)
  int n_samples = 10;
  int noise_reduce_num1 = 3;
  int noise_reduce_num2 = 100;

  int min = -1000;
  int max = 1000;
  int mean = 0;
  int stdDev = 6;

  int buckets = 6;

  Stopwatch sw;

  std::array<float, dim> arr;
  for (int i = 0; i < dim; i++) {
    arr[i] = 0;
  }
  Point<dim> target{arr};

  for (int i = 0; i < n_samples; i++) {
    std::cout << "|";
  }
  std::cout << std::endl;

  // K and N
//  std::vector<int> independent_vars(0);
//  for (int i = 100; i < max_size; i += max_size / n_samples) { // k cannot be 0
//    independent_vars.push_back(i);
//  }

  std::vector<double> results(0);

  // Measure variance in k for both distributions
//  for (int i = 0; i < independent_vars.size(); i++) {

  int n = max_size;
    double sum = 0;

    for (int j = 0; j < noise_reduce_num1; j++) {

      // Generate new random points
      UniformGenerator<dim> gg(min, max);
      std::vector<Point<dim>> g_dist(0);

      for (int ii = 0; ii < n; ii++) { // keep k less than num points
        g_dist.push_back(gg.generatePoint());
      }

//      KDTree<dim> kdTree(g_dist);
//      QuadTree<dim> quadTree(g_dist);
      BucketKNN<dim> bucketKNN(g_dist, buckets);

      sw.start();
      for (int jj = 0; jj < noise_reduce_num2; jj++) {
        bucketKNN.KNN(target, 10); // k = 10
      }
      sum += sw.stop() / noise_reduce_num2;
    }

    results.push_back(sum / noise_reduce_num1);
    std::cout << "|";

//  } // first for-loop

  std::cout << std::endl;

  std::ofstream ofs("Bucket_uniform_10D.csv");
  if (ofs.is_open()) {

//    assert(independent_vars.size() == results.size());

    ofs << "d, time" << std::endl;

    for (int i = 0; i < results.size(); i++) {
      ofs << dim << "," << results[i] << std::endl;
    }
    ofs.close();
  } else {
    std::cout << "Failed to open file" << std::endl;
  }




  //  std::vector<std::vector<float>> components(0);
  //  components.push_back({1,6,5,2,3,7,6,3,1,2.5,4,3,3});
  //  components.push_back({2,4,3,7,6,3,7,1,6,5,2,4,2});
  //
  //  Test<dimension> D2Custom(components, 4, 3, 3);
  //
  //  int maxDataSize = 10000000;
  //  std::vector<int> kValues;
  //  // Get 100 data points for time vs. k
  //  for (int i = 1; i <= maxDataSize; i += (maxDataSize / 100)) {
  //    kValues.push_back(i);
  //  }
  //
  //  Stopwatch sw;
  //  std::vector<double> timerResults;
  //
  //  std::vector<Point<dimension>> data;
  //  int k = 0;
  //  std::unique_ptr<KDTree<dimension>> kdTree;
  //
  //  for (int a = 0; a < kValues.size(); a++) {
  //    // Change K
  //    k = kValues[a];
  //
  //    double sum = 0;
  //
  //    for (int b = 0; b < 3; b++) {
  //      // Change Data
  //      data = D2Custom.uniformDistribution(-10000, 10000, maxDataSize);
  //      kdTree = std::unique_ptr<KDTree<dimension>>(new KDTree<dimension>(data));
  //
  //      // I could time the loop but the important thing here is relative time.
  //
  //      // time this portion
  //      sw.start();
  //      for (int c = 0; c < 100; c++) {
  //        // Run the experiment k times
  //        kdTree->KNN(Point<dimension>{0,0}, k);
  //      }
  //      double elapsed = sw.stop();
  //      // time this portion
  //      sum += (elapsed / 100);
  //    }
  //
  //    timerResults.push_back(sum / 3);
  //
  //  }
  //
  //  for (int i = 0; i < timerResults.size(); i++) {
  //    std::cout << timerResults[i] << std::endl;
  //  }

  //  data = D2Custom.uniformDistribution(-1000, 1000, maxDataSize);
  //  std::cout << "finished creating data" << std::endl;
  //  kdTree = std::unique_ptr<KDTree<dimension>>(new KDTree<dimension>(data));
  //  std::cout << "finished creating tree" << std::endl;
  //  sw.start();
  //  kdTree->KNN({0,0}, 1);
  //  std::cout << std::endl << sw.stop() << std::endl;

  std::cout << sw2.stop() / 60;
  std::cout << '\n';
  return 0;
}

// TODO: Discrepancy at {4,4}.


//
//  Test<dimension> D2Custom(components, 4, 10, 3);
//
//  auto data = D2Custom.customData(components);
//
//  D2Custom.printAllKNN({0,0}, true);
//
//  std::cout << D2Custom.KNN(0, {0,0}, true) << std::endl;
//  std::cout << D2Custom.currentK() << std::endl;
