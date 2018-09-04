#pragma once

#include "Point.hpp"
#include <vector>
#include <cmath> // pow
#include "Helpers.hpp" // ostream overloads

// TODO: Where is this class or an instance of this class defined as const so
// as to require const within the method signatures?

// TODO: Should I move all of the implementation details to a .cpp file?

template<int Dimension>
class BucketKNN: public PartitionModel<Dimension> {

public:

  /* Converts a vector of multi-dimensional points to a 1-dimensional vector
   * of vectors where each of the internal vectors is a representation of a
   * 'bucket' of points.
   * 
   * points: The multi-dimensional dataset to be mapped 1-dimensionally.
   * divisions_: Specifies the number of times to divide each dimension.
   */
  BucketKNN(const std::vector<Point<Dimension>>& points, int divisions_) {

    size = points.size(); // Required in KNN()
    divisions = divisions_; // Required in rangeQuery()

    // Define the axis-aligned bounding box for this dataset. For example,
    // the points (1,2), (3,6), (5,3), (6,4), (7,3) have a bounding box as
    // defined by an x,y min of (1,2) and an x,y max of (7,6).
    AABB<Dimension> aabb = getBounds(points);
    mins = aabb.mins;
    maxs = aabb.maxs;

    // Calculate the tile size for each dimension. In other words, split the
    // bounding box on each dimension by the number of divisions.
    for (int d = 0; d < Dimension; d++) {
      tiles[d] = (maxs[d] - mins[d]) / divisions;
    }

    // A 1-dimensional container of all multi-dimensional points.
    buckets = std::vector<std::vector<Point<Dimension>>>(pow(divisions, Dimension));

    for (Point<Dimension> p: points) {
      // Calculate multi-dimensional indexes. For example, a 2-dimensional
      // point is mapped to both an x- and y-index. In other words, its
      // x-component is indexed separately from its y-component.
      std::array<int, Dimension> coords;
      for (int d = 0; d < Dimension; d++) {
        int idx = (p[d] - mins[d]) / tiles[d];
        coords[d] = std::min(idx, divisions - 1);
      }
      // Map previously computed multi-dimensional indexes to a
      // 1-dimensional vector.
      int bIndex = 0;
      for (int d = 0; d < Dimension; d++) {
        bIndex += coords[d] * pow(divisions, d);
      }
      buckets[bIndex].push_back(p);
    }
  }

  // TODO: Should I move rangeQuery to be a private member? Is it common
  // to use a rangeQuery outside the context of the KNN method?

  // TODO: Should rangeQuery return p as a component of all points within
  // the specified range? For example, where p = (1,2), the query returns
  // something like [(3,4), (5,6), (1,2), (4,3)]. This results in KNN
  // containing the reference point as well.

  /* Returns all points within the radius of a single point.
   *
   * p: The central point from which to query neighboring points.
   * radius: The distance from p wherin to search for all neighboring points.
   */
  std::vector<Point<Dimension>> rangeQuery(const Point<Dimension>& p, float radius) {

    std::array<int, Dimension> minCoords, maxCoords;
    std::array<float, Dimension> minBounds, maxBounds;

    for (int d = 0; d < Dimension; d++) {

      int index;

      minBounds[d] = p[d] - radius;
      maxBounds[d] = p[d] + radius;

      // Calculate multi-dimensional indexes. These are not indexes
      // into the 1-dimensional vector (buckets) but are used in
      // the conversion to a 1-dimensional index.
      index = (minBounds[d] - mins[d]) / tiles[d];
      minCoords[d] = std::max(index, 0); // TODO: Where could this be less than 0?
      index = (maxBounds[d] - mins[d]) / tiles[d];
      maxCoords[d] = std::min(index, divisions - 1);

    }

    // A container for all points within the given range.
    std::vector<Point<Dimension>> points;
    // 'current' refers to the current bucket. For a 2-dimensional
    // space, begin the range query searching the bottom-left bucket
    // (minimum index in the given range).
    std::array<int, Dimension> current = minCoords;

    // Find all points within the given range of the specified point.
    // Searches all buckets from minCoords to maxCoords. See nextBucket
    // for implemenatation iterating through buckets.
    while(true) {

      // Calculate the 1-dimensional index as a combination of the
      // multi-dimensional coordinates.
      int bIndex = 0;
      for (int d = 0; d < Dimension; d++) {
        bIndex += current[d] * pow(divisions, d);
      }

      // Search the bucket at the calculated index.
      for (Point<Dimension> point: buckets[bIndex]) {
        if (distance(point, p) <= radius) {
          points.push_back(point);
        }
      }
      if (current == maxCoords) {
        break; // The last (maxCoords) bucket has been searched.
      }
      current = nextBucket(current, minCoords, maxCoords);
    }

    return points;
  }

  /* Returns the k-nearest neighbors to the given point.
   *
   * p: The origin point from which to search for its k-nearest neighbors.
   * k: The closest number of neighbors for which to query.
   */
  std::vector<Point<Dimension> > KNN(const Point<Dimension>& p, int k) {

    // The number of neighbors cannot exceed the number points within the
    // current dataset.
    if (k > size) {
      k = (int)size;
    }

    // TODO: Why the first dimension tile size? Is this an arbitrary value?
    // I had considered using std::min(tiles) to start the query with the
    // smallest possible radius but this would likely take too long for large
    // dimensions. It may also not be effective to start too small. What is
    // the most effective initial range?

    // An initial query range. This will be incremented as needed.
    float radius = tiles[0];
    std::vector<Point<Dimension>> pointsInRange(0);

    // Increase the search range so long as the number of points returned
    // by the query is less than the requested number of neighboring points.
    do {
      pointsInRange = rangeQuery(p, radius);
      radius += radius;
    } while (pointsInRange.size() < k);

    // TODO: It would be an interesting experiment to compare execution time of
    // sorting from within the rangeQuery method (using a linked list for example)
    // vs. sorting all the points at once (as implemented below).

    // The number of points returned by the range query will likely be greater
    // than the desired number of neighboring points. In this case, the points
    // must be sorted in order to return only the k-nearest neighbors.
    if (pointsInRange.size() != k) {
      auto comparator = DistanceComparator<Dimension>(p);
      std::sort(pointsInRange.begin(), pointsInRange.end(), comparator);
    }

    // TODO: What is better practice: (1) Using the assert statement, as
    // implemented below? (2) Adding a condition to the for-loop wherein
    // numPoints < k && numPoints < pointsInRange.size()? The second option
    // obviously does not provide any feedback if something goes wrong.

    // Push the appropriate number of neighboring points to the return vector.
    // For the common case where the points have been sorted (see the compartor
    // above) only the closest k neighbors will be pushed. For the uncommon
    // case where pointsInRange.size() == k, all queried points will be pushed.
    std::vector<Point<Dimension>> knn;
    for (int numPoints = 0; numPoints < k; numPoints++) {
      assert(numPoints < pointsInRange.size());
      knn.push_back(pointsInRange.at(numPoints));
    }

    return knn;
  }

private:

  long size; // The number of points in the dataset.
  int divisions;
  // Min and max coordinates of the axis-aligned bounding box.
  std::array<float, Dimension> mins;
  std::array<float, Dimension> maxs;
  // The tile size for each dimension.
  std::array<float, Dimension> tiles;
  std::vector<std::vector<Point<Dimension>>> buckets;

  /* Returns the next array of dimensional indexes. In other words, iterates
   * through the buckets by updating the stored dimensional indexes. For
   * example, in a 2-dimensional space, updates [2,1] to [2,2] and [2,2] to
   * [2,3]. In this 2-dimensional example, the next bucket is the bucket above
   * the previous. When the y-index reaches the y-max, the x-index will be
   * incremented and the y-index restored to y-min. For example, [2,3] might
   * be updated to [3, 1].
   *
   * current: The current bucket index.
   * minCoords: The indexes for the minimum bucket.
   * maxCoords: The indexes for the maximum bucket.
   *
   * Note that in a 2-dimensional space, minCoords is the bottom-leftmost
   * bucket and maxCoords is the top-rightmost bucket.
   */
  std::array<int, Dimension> nextBucket(std::array<int, Dimension> current,
      std::array<int, Dimension> minCoords, std::array<int, Dimension> maxCoords) const {

    // For a 2-dimensional space, increments the y-index. For a 3-dimensional
    // space, increments the z-axis. Etc...
    current[Dimension - 1]++;

    // Increment the next dimension when all values in the last dimension
    // have been incremented. The 'last dimension' is the last dimension in
    // the array (Dimension - 1). For example, in a 2-dimensional space,
    // x is incremented after y has been incremented beyond the max y-index.
    // Print the current coordinates from inside the first 'if' clause to
    // view the increments step-by-step.
    for (int d = Dimension - 1; d > 0; d--) {
      if (current[d] > maxCoords[d]) {
        current[d] = minCoords[d]; // Reset last dimension (Dimension - 1).
        current[d - 1]++; // Increment the next-to-last dimension (Dimension - 2, etc.).
      } else {
        break;
      }
    }

    return current;
  }

}; // End BucketKNN class

