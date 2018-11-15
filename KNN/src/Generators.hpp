#pragma once

#include "Point.hpp"
#include <random>


/* These generators just have 1 method: generate point.

 They take a template parameter for the dimension and some constructor parameters for other stuff

 */

template<int Dimension>
class DistributionGenerator {

protected:

  std::random_device rd;
  std::mt19937 gen;

public:

  DistributionGenerator()
  :rd{}, gen(rd()) {}

  virtual Point<Dimension> generatePoint() = 0;
};

//Uniformly (evenly) distributed random points
template<int Dimension>
class UniformGenerator: public DistributionGenerator<Dimension> {

private:

  float min, max;
  std::uniform_real_distribution<> dis;

public:

  UniformGenerator(float min_, float max_)
  :min(min_), max(max_), dis(min_, max_) {}

  Point<Dimension> generatePoint() {

    std::array<float, Dimension> data;
    for(int i = 0; i < Dimension; ++i){
      data[i] = dis(this->gen);
    }
    return Point<Dimension>{data};
  }

};

//Normally (Gaussian) distributed random points.  These will be clumped around mean.  Spread is determined by stdDev
template<int Dimension>
class GaussianGenerator: public DistributionGenerator<Dimension> {

private:

  float mean, stdDev;
  std::normal_distribution<> dis;

public:

  GaussianGenerator(float mean_, float stdDev_)
  :mean(mean_), dis(mean_, stdDev_) {}

  Point<Dimension> generatePoint(){

    std::array<float, Dimension> data;
    for(int i = 0; i < Dimension; ++i){
      data[i] = dis(this->gen);
    }
    return Point<Dimension>{data};
  }
};

/*This function is useful for generating points to store/query, and points to use for KNN/Range queries

 The struct just groups the 2 point sets together.
 */

template <int Dimension>
struct TrialData{
  std::vector<Point<Dimension> > training, testing;
};

template<int Dimension, typename Generator>
TrialData<Dimension> getTrialData(int trainingSize, int testingSize, Generator& gen){
  TrialData<Dimension> ret;
  for(int i = 0; i < trainingSize; ++i){
    ret.training.push_back(gen.generatePoint());
  }
  for(int i = 0; i < testingSize; ++i){
    ret.testing.push_back(gen.generatePoint());
  }
  return ret;
}
