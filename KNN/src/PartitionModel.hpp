//
//  Model.hpp
//  SpatialPartitioning
//
//  Created by Matt Josse on 7/27/18.
//  Copyright © 2018 Matt Josse. All rights reserved.
//

#pragma once

template<int Dimension>
class PartitionModel {

public:

  virtual std::vector<Point<Dimension>> rangeQuery(const Point<Dimension>& p, float radius) = 0;
  virtual std::vector<Point<Dimension>> KNN(const Point<Dimension>& p, int k) = 0;

protected:

  // TODO: Anything possible to extract from the individual models?

};
