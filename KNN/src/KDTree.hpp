#pragma once

#include "Point.hpp"
#include <memory>
#include <queue>
#include "Helpers.hpp"
#include "PartitionModel.hpp"

// TODO: The documentation referencing the distance method within Point.hpp refers to the fact that
// we do not care about the distance, but rather the squared distance. Wouldn't simply NOT
// returning the sqrt of the squared distance be equivalent to the suggestion there? Why don't we
// care about the distance?

template<int Dimension>
class KDTree: public PartitionModel<Dimension> {

public:

  KDTree(std::vector<Point<Dimension>> points) {
    // The first splitting dimension is 0. Note that the points vector is
    // modified in place while sorting and is therefore passed by value.
    root = std::unique_ptr<Node<0>>(new Node<0>(points.begin(), points.end()));
  }

  std::vector<Point<Dimension>> rangeQuery(const Point<Dimension>& p, float radius) {
    std::vector<Point<Dimension>> result(0);
    rangeQuery(p, radius, result, root);
    return result;
  }

  std::vector<Point<Dimension>> KNN(const Point<Dimension>& p, int k) {

    std::vector<Point<Dimension>> knn(0); // Closest K points

    auto comparator = DistanceComparator<Dimension>(p);
    std::make_heap(knn.begin(), knn.end(), comparator);

    AABB<Dimension> aabb = AABB<Dimension>(); // Infinite bounds in all dimensions
    KNN(p, k, aabb, knn, comparator, root); // knn passed by reference

    return knn;
  }

private:

  template<int SplitDimension>
  struct Node {

    using Child = Node<(SplitDimension + 1) % Dimension>;

    Point<Dimension> p;
    std::unique_ptr<Child> left, right;

    template<typename Iter>
    Node(Iter begin, Iter end)
    {
      // Find the middle point. Subtracting the beginning from the end is
      // necessary for recursion where only a subset of the original structure
      // is considered.
      auto middle = begin + (end - begin) / 2;

      // Sort points from smallest to largest according to the current
      // splitting dimension.
      CompareBy<SplitDimension> compareByDimension;
      std::nth_element(begin, middle, end, compareByDimension);
      p = *middle; // Median point (as defined by the current dimension).

      // Recursively split any existing subtrees. Where the leftmost iterator
      // (begin) reaches the rightmost iterator (end), no children exist. Note
      // the splitting dimension is implicitly updated as defined by the 'using'
      // statement within the Node struct.
      if (begin != middle) {
        left = std::unique_ptr<Child>(new Child(begin, middle));
      }

      if (middle + 1 != end) {
        right = std::unique_ptr<Child>(new Child(middle + 1, end));
      }
    } // Node constructor

  }; // Node

  std::unique_ptr<Node<0>> root; // Start splitting at x (dimension 0)

  // Recursive helper
  template<int SplitDimension>
  void rangeQuery(const Point<Dimension>& p, float radius, std::vector<Point<Dimension>>& result,
                  std::unique_ptr<Node<SplitDimension>>& n) const {

    if (distance(p, n->p) <= radius) {
      result.push_back(n->p);
    } else {
      return;
    }

    if (n->left != nullptr) {
      rangeQuery(p, radius, result, n->left);
    }

    if (n->right != nullptr) {
      rangeQuery(p, radius, result, n->right);
    }

  }

  // Recursive helper
  template<int SplitDimension>
  void KNN(const Point<Dimension>& p, int k, const AABB<Dimension>& aabb,
           std::vector<Point<Dimension>>& knn, const DistanceComparator<Dimension>& comparator,
           std::unique_ptr<Node<SplitDimension>>& n) const {

    if (knn.size() < k) {

      knn.push_back(n->p);
      std::push_heap(knn.begin(), knn.end(), comparator);

    } else if (distance(n->p, p) < distance(knn.front(), p)) {

      std::pop_heap(knn.begin(), knn.end(), comparator);
      knn.pop_back();

      knn.push_back(n->p);
      std::push_heap(knn.begin(), knn.end(), comparator);

    }

    if (n->left != nullptr) {

      auto box = aabb;
      box.maxs[SplitDimension] = n->p[SplitDimension];
      auto closestInBox = box.closestInBox(p);

      if (distance(closestInBox, p) < distance(knn.front(), p)) {
        KNN(p, k, box, knn, comparator, n->left);
      }
    }

//    std::cout << n->p << std::endl;

    if (n->right != nullptr) {

      auto box = aabb;
      box.mins[SplitDimension] = n->p[SplitDimension];
      auto closestInBox = box.closestInBox(p);

      if (distance(closestInBox, p) < distance(knn.front(), p)) { // TODO <= ?
        KNN(p, k, box, knn, comparator, n->right);
      }
    }
  }

}; // KDTree
