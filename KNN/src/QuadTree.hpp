//
//  QuadTree.hpp
//  SpatialPartitioning
//
//  Created by Matt Josse on 7/25/18.
//  Copyright © 2018 Matt Josse. All rights reserved.
//

#pragma once

#include "Point.hpp"
#include <memory>
#include <queue>
#include "Helpers.hpp"
#include "PartitionModel.hpp"

//const int Dimension = 2;
const int x = 0;
const int y = 1;

// TODO: Create enum

template<int Dimension>
class QuadTree: public PartitionModel<Dimension> {

public:

  QuadTree(std::vector<Point<Dimension>> points) {
    auto aabb = getBounds(points);
    root = std::unique_ptr<Node>(new QuadTree::Node(points, aabb));
  }

  std::vector<Point<Dimension>> rangeQuery(const Point<Dimension>& p, float radius) {
    std::vector<Point<Dimension>> query(0);
    rangeQuery(p, radius, query, root);
    return query;
  }

  std::vector<Point<Dimension>> KNN(const Point<Dimension>& p, int k) {
    std::vector<Point<Dimension>> knn(0);
    auto comparator = DistanceComparator<Dimension>(p);
    std::make_heap(knn.begin(), knn.end(), comparator);
    KNN(p, k, knn, comparator, root);
    return knn;
  }

private:

  struct Node {

    std::vector<Point<Dimension>> points;
    AABB<Dimension> aabb;
    std::vector<std::unique_ptr<Node>> children;

    int maxChildren = 4;

    Node(const std::vector<Point<Dimension>>& points_, const AABB<Dimension>& aabb_) {

      points = points_;
      aabb = aabb_;
      children = std::vector<std::unique_ptr<Node>>(4);

      if (points.size() < maxChildren) {
        return;
      }

      std::array<float, Dimension> splitCoords = split(aabb);
      std::vector<std::vector<Point<Dimension>>> subsets(4);

      // TODO: Use iterators to sort points.

      for (auto p: points_) {

        if (p[x] < splitCoords[x] && p[y] >= splitCoords[y]) {
          subsets[0].push_back(p);
        } else if (p[x] >= splitCoords[x] && p[y] >= splitCoords[y]) {
          subsets[1].push_back(p);
        } else if (p[x] < splitCoords[x] && p[y] < splitCoords[y]) {
          subsets[2].push_back(p);
        } else if (p[x] >= splitCoords[x] && p[y] < splitCoords[y]) {
          subsets[3].push_back(p);
        }

      }

      // TODO: Confirm whether or not to check size here. The difference is whether
      // or not a child exists without any points. This is the case here.

      //      if (subsets[0].size() > 0) {
      auto bbox = aabb;
      bbox.mins[y] = splitCoords[y];
      bbox.maxs[x] = splitCoords[x];
      children[0] = std::unique_ptr<Node>(new Node(subsets[0], bbox));
      //      }
      //      if (subsets[1].size() > 0) {
      bbox = aabb;
      bbox.mins = std::array<float, Dimension>{splitCoords[x], splitCoords[y]};
      children[1] = std::unique_ptr<Node>(new Node(subsets[1], bbox));
      //      }
      //      if (subsets[2].size() > 0) {
      bbox = aabb;
      bbox.maxs = std::array<float, Dimension>{splitCoords[x], splitCoords[y]};
      children[2] = std::unique_ptr<Node>(new Node(subsets[2], bbox));
      //      }
      //      if (subsets[3].size() > 0) {
      bbox = aabb;
      bbox.mins[x] = splitCoords[x];
      bbox.maxs[y] = splitCoords[y];
      children[3] = std::unique_ptr<Node>(new Node(subsets[3], bbox));
      //      }

    } // Node constructor

    std::array<float, Dimension> split(const AABB<Dimension>& aabb) {
      float xSplit = (aabb.mins[x] + aabb.maxs[x]) / 2;
      float ySplit = (aabb.mins[y] + aabb.maxs[y]) / 2;
      return {xSplit, ySplit};
    }

    bool isLeaf() {
      return points.size() < 4;
    }

  }; // Node

  std::unique_ptr<Node> root;

  // Recursive helper
  void rangeQuery(const Point<Dimension>& p, float radius, std::vector<Point<Dimension>>& query,
                  std::unique_ptr<Node>& n) const {

    if (n->isLeaf()) {
      // Leaf node. Calculate distance for individual points.
      for (auto point: n->points) { // TODO: Pass by reference here? '&'
        if (distance(point, p) <= radius) {
          query.push_back(point);
        }
      }
      return;
    }

    // For each child, check whether the distance of the nearest point in that child's
    // bounding box is within the radius. If so, recurse that child's subtree (a point
    // within the radius may exist).
    for (auto& child: n->children) {
      if (child != nullptr && isInRange(p, child->aabb, radius)) {
        rangeQuery(p, radius, query, child);
      }
    }
  }

  float isInRange(const Point<Dimension>& p, const AABB<Dimension>& aabb, float radius) const {
    return distance(aabb.closestInBox(p), p) <= radius;
  }

  // Recursive helper
  void KNN(const Point<Dimension>& p, int k, std::vector<Point<Dimension>>& knn,
           DistanceComparator<Dimension> comparator, std::unique_ptr<Node>& n) const {

    if (n->isLeaf()) {
      for (auto point: n->points) { // TODO: Pass by reference? '&'
        if (knn.size() < k) {

          knn.push_back(point);
          std::push_heap(knn.begin(), knn.end(), comparator);

        } else if (distance(point, p) < distance(knn.front(), p)) {

          std::pop_heap(knn.begin(), knn.end(), comparator);
          knn.pop_back();

          knn.push_back(point);
          std::push_heap(knn.begin(), knn.end(), comparator);
        }
      }
    }

    for (auto& child: n->children) {
      if (child != nullptr && (knn.size() < k || distance(child->aabb.closestInBox(p), p) <=
                               distance(knn.front(), p))) {
        KNN(p, k, knn, comparator, child);
      }
    }

  }

}; // KDTree
