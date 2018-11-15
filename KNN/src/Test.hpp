//
//  Test.hpp
//  SpatialPartitioning
//
//  Created by Matt Josse on 7/26/18.
//  Copyright © 2018 Matt Josse. All rights reserved.
//


//// Use this constructor to access methods in a static format (for example, creating
//// custom data to use while playing with other data. Just be aware the models will
//// have no data.
//Test() {
//
//  // There is a problem with no data (address problem in the trees when using
//  // iterators to find the middle).
//  // init({}, 1, 2, 2);
//  init({{0,0},{1,1},{2,2}}, 2, 2, 2);
//  // Also, getBounds will return infinity on no data. This constructor does
//  // not currently initialize with points.
//}

#pragma once

#include "Point.hpp"
#include "Helpers.hpp"
#include "BucketKNN.hpp"
#include "KDTree.cpp"
#include "QuadTree.hpp"
#include <string>
#include "Stopwatch.hpp"
#include <fstream>
#include <iostream>

// TODO: What is the reason for requiring the three data structure models to fit in
// one of the following two categories?
//
// (1) The model must be initialized and then passed to the 'Test' constructor.
// (2) The model must be initialized via the 'Test' constructor's initialization list.
//
// The use of pointers to the three models eliminates the necessity of prior
// initialization. Furthermore, it permits the definition of variables required for
// the model constructors before initializing those models.
//
// Similarly, is it possible to implement delegate constructors wherein the required
// parameters to the three models are computed within the first constructor and then
// passed to the delegate constructor (min. C++11)?
//
// Also, what is happening behind-the-scenes where delegate constructors are not
// used but one constructor calls another? The expected initialization does not occur.

/* This class represents a test module for data of a pre-set dimension (as defined by
 * the template parameter 'Dimension'). It is a wrapper for the three spatial
 * partitioning data structures defined in this project (BucketKNN, KDTree, QuadTree)
 * as well as methods for testing these structures. There exist both accuracy and
 * timing methods to ensure correct queries (KNN and range queries) and compare the
 * relative execution times of those queries.
 */
template<int Dimension>
class Test {

public:

  /*******************************************************************************
   * Constructors                                                                *
   *******************************************************************************/

  Test() {

    // default values
    // Do the generators need destructors for when I replace one with another?
    UniformGenerator<Dimension> ug((int)-10e6, (int)10e6);
    GaussianGenerator<Dimension> gg(0.0, 6.0);
    generators = std::vector<DistributionGenerator<Dimension>>{ug, gg};

  }

  /*******************************************************************************
   * Public Methods                                                              *
   *******************************************************************************/

  // Make this an enum
  DistributionGenerator<Dimension> getDistGen(int gen) {
    return generators.at(gen);
  }

  int getK() {
    return k;
  }

  int getN() {
    return n;
  }

  // Using default generator arguments
  std::vector<Point<Dimension>> getDistribution(int distType, int size) {

    std::vector<Point<Dimension>> dist(0);
    auto gen = generators.at(distType);

    for (int i = 0; i < size; i++) {
      dist.push_back(gen.generatePoint());
    }

    return dist;
  }




  // Use this method to construct data within the components constructor and if there is
  // a custom dataset to use in the searchForErrors method.
  std::vector<Point<Dimension>> customData(const std::vector<std::vector<float>>& components) {

    // Convert individual dimensional components to a vector of points.
    std::vector<Point<Dimension>> data(0);
    for (int i = 0; i < components[0].size(); i++) {
      std::array<float, Dimension> point;
      for (int d = 0; d < Dimension; d++) {
        point[d] = components[d][i];
      }
      data.push_back({point});
    }
    return data;
  }

  std::vector<Point<Dimension>> uniformDistribution(float min, float max, int n) {
    UniformGenerator<Dimension> gen(min, max);
    std::vector<Point<Dimension>> points(0);
    for (int i = 0; i < n; i++) {
      points.push_back(gen.generatePoint());
    }
    return points;
  }

  std::vector<Point<Dimension>> gaussianDistribution(float mean, float stdDev, int n) {

    GaussianGenerator<Dimension> gen(mean, stdDev);
    std::vector<Point<Dimension>> points(0);
    for (int i = 0; i < n; i++) {
      points.push_back(gen.generatePoint());
    }
    return points();
  }

  std::unique_ptr<PartitionModel<Dimension>> getModel(int id) {
    return models.at(id);
  }

private:

  /*******************************************************************************
   * Private Member Variables                                                    *
   *******************************************************************************/

  // Read-only unless re-instantiating the BucketKNN model.
  int divisions = 4;
  // Default variables (mutable).
  int k = 1000;
  int n = (int)10e6;

  float radius = 5;

  std::vector<DistributionGenerator<Dimension>> generators;
  Point<Dimension> target{0,0}; // default point


  // The data
  std::vector<Point<Dimension>> data;

  // The 3 spatial partitioning models for this 'Test' object.
  std::vector<std::unique_ptr<PartitionModel<Dimension>>> models;

  // Required for printing methods.
  std::vector<std::string> structureNames = {"BucketKNN\t", "KDTreeKNN\t", "QuadTreeKNN\t"};

  /*******************************************************************************
   * Private Methods                                                             *
   *******************************************************************************/

  /* Initializes all three spatial partitioning models.
   *
   * The use of pointers circumvents the necessity of using the initialization list
   * when a new 'Test' object is instantiated. This permits the construction of
   * datasets within the 'Test' constructor (since the dataset is a required
   * parameter of the spatial partitioining model constructors).
   *
   * TODO: Is this 'init' method interchangeable with delegating constructors? This
   * is a question specific to the use of the 'components' constructor which
   * constructs a dataset which is in turn used as a parameter to the primary
   * constructor.
   */
  void init(std::vector<Point<Dimension>> data, int divisions, int k, float radius) {

    models = std::vector<std::unique_ptr<PartitionModel<Dimension>>>(0);

    models.emplace_back(std::unique_ptr<PartitionModel<Dimension>>(new BucketKNN(data, divisions)));
    models.emplace_back(std::unique_ptr<PartitionModel<Dimension>>(new KDTree(data)));
    models.emplace_back(std::unique_ptr<PartitionModel<Dimension>>(new QuadTree(data)));

    this->k = k;
    this->radius = radius;
  }

  void sortAll(const Point<Dimension>& p, std::vector<std::vector<Point<Dimension>>>& data) {
    DistanceComparator<Dimension> comparator(p);
    // TODO: Why must 'auto' be a reference in order to sort properly?
    for (auto& points: data) {
      std::sort(points.begin(), points.end(), comparator);
    }
  }

  void sort(const Point<Dimension>& p, std::vector<Point<Dimension>>& points) {
    DistanceComparator<Dimension> comparator(p);
    std::sort(points.begin(), points.end(), comparator);
  }

  void printAll(const std::vector<std::vector<Point<Dimension>>>& results) {
    for (int i = 0; i < results.size(); i++) {
      std::cout << structureNames[i];
      std::cout << results[i] << std::endl;
    }
  }

}; // Test


///* Creates a dataset consisting of the dimensional components provided as the
// * 'components' argument. For example:
// *
// * [[0,3,6,2],[1,7,9,3]] // components (size = 2)
// * [0, 3, 6, 2] // components[0] AKA x-component
// * [1, 7, 9, 3] // components[1] AKA y-component
// *
// * [[0,1],[3,7],[6,9],[2,3]] // result
// */
//Test(const std::vector<std::vector<float>>& components, int divisions, int k, float radius) {
//  auto data = customData(components);
//  this->data = data;
//  init(data, divisions, k, radius);
//} // Constructor: Custom data
//
//Test(const std::vector<Point<Dimension>>& data, int divisions, int k, float radius) {
//  init(data, divisions, k, radius);
//} // Constructor: Primary


// public

///* Retrieves the dataset over which the three data structures are built.
// */
//std::vector<Point<Dimension>> dataset() {
//  return data;
//}
//
///* Modifies the default k.
// */
//void setK(int k) {
//  this->k = k;
//}
//
//int currentK() {
//  return k;
//}
//
///* Modifies the default radius.
// */
//void setRadius(float radius) {
//  this->radius = radius;
//}
//
//int currentRadius() {
//  return radius;
//}
//
///* Returns the number of divisions used for the BucketKNN model. This attribute
// * is read-only outside of re-initializing this 'Test' object's BucketKNN model.
// */
//int numDivisions() {
//  return divisions;
//}
//
///* Returns the min and max bounds of the entire dataset.
// */
//AABB<Dimension> bounds() {
//  return getBounds(data);
//}
//
//// TODO: Create an enum where id == 0 or 1 or 2.
//std::vector<Point<Dimension>> KNN(int id, const Point<Dimension>& p, bool sorted = false) {
//  return KNN(id, p, k, sorted);
//}
//
//std::vector<Point<Dimension>> KNN(int id, const Point<Dimension>& p, int k, bool sorted = false) {
//  auto knn = models[id]->KNN(p, k);
//  if (sorted) {
//    sort(p, knn);
//  }
//  return knn;
//}
//
///* Returns the resulting knn for all three spatial partitioning models (BucketKNN,
// * KDTree, and QuadTree). Whether the results are sorted by distance from the
// * target point 'p' is optional. This method uses the current default 'k' value.
// *
// * 'p' represents the target or origin point from which to obtain the k-nearest
// * neighbors. 'sorted' is the optional parameter to specify whether the returned
// * vectors are sorted by increasing distance from 'p'.
// */
//std::vector<std::vector<Point<Dimension>>> KNN(const Point<Dimension>& p, bool sorted = false) {
//  return KNN(p, k, sorted);
//}
//
///* Returns the resulting knn for all three spatial partitioning models (BucketKNN,
// * KDTree, and QuadTree). Whether the results are sorted by distance from the
// * target point 'p' is optional.
// *
// * 'p' represents the target or origin point from which to obtain the k-nearest
// * neighbors. 'k' is the number of nearest neighbors to 'p'. 'sorted' is the optional
// * parameter to specify whether the returned vectors are sorted by increasing
// * distance from 'p'.
// *
// * This method does not modify the current default 'k'. Use 'setK' instead.
// */
//std::vector<std::vector<Point<Dimension>>> KNN(const Point<Dimension>& p, int k, bool sorted = false) {
//  std::vector<std::vector<Point<Dimension>>> knn(0);
//
//  for (auto& model: models) {
//    knn.push_back(model->KNN(p, k));
//  }
//  if (sorted) {
//    sortAll(p, knn);
//  }
//  return knn;
//}
//
///* Returns the resulting range query for all three spatial partitioning models
// * (BucketKNN, KDTree, and QuadTree). Whether the results are sorted by distance
// * from the target point 'p' is optional.
// *
// * 'p' represents the target or origin point from which to obtain all points
// * within the radius. This method uses the current default 'radius' value. 'sorted'
// * is the optional parameter to specify whether the returned vectors are sorted by
// * increasing distance from 'p'.
// */
//std::vector<std::vector<Point<Dimension>>> rangeQuery(const Point<Dimension>& p, bool sorted = false) {
//  return rangeQuery(p, radius, sorted);
//}
//
///* Returns the resulting range query for all three spatial partitioning models
// * (BucketKNN, KDTree, and QuadTree). Whether the results are sorted by distance
// * from the target point 'p' is optional.
// *
// * 'p' represents the target or origin point from which to obtain all points
// * within the 'radius'. 'sorted' is the optional parameter to specify whether the
// * returned vectors are sorted by increasing distance from 'p'.
// *
// * This method does not modify the current default 'radius'.
// */
//std::vector<std::vector<Point<Dimension>>> rangeQuery(const Point<Dimension>& p, float radius, bool sorted = false) {
//  std::vector<std::vector<Point<Dimension>>> query(0);
//
//  for (auto& model: models) {
//    query.push_back(model->KNN(p, k));
//  }
//
//  if (sorted) {
//    sortAll(p, query);
//  }
//
//  return query;
//}
//
//void printAllKNN(const Point<Dimension>& p, bool sorted = false) {
//  printAllKNN(p, k, sorted);
//}
//
//void printAllKNN(const Point<Dimension>& p, int k, bool sorted = false) {
//  auto results = KNN(p, k, sorted);
//  printAll(results);
//}
//
//void printAllRangeQuery(const Point<Dimension>& p) {
//  printAllRangeQuery(p, radius);
//}
//
//void printAllRangeQuery(const Point<Dimension>& p, float radius) {
//  auto results = rangeQuery(p, radius);
//  printAll(results);
//}
//
//bool compareAllKNN(const Point<Dimension>& p) {
//  auto results = KNN(p, true);
//  return compareAll(results);
//}
//
//bool compare(const std::vector<Point<Dimension>>& lhs, const std::vector<Point<Dimension>>& rhs) {
//  return lhs == rhs;
//  }
//// Make public in order to pass already computed data? Must be sorted first.
//// Use the 'sorted' parameter in the query methods.
//bool compareAll(const std::vector<std::vector<Point<Dimension>>>& data) {
//  assert(data.size() == 3);
//  return data[0] == data[1] && data[1] == data[2];
//}
