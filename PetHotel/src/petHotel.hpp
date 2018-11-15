//
//  petHotel.hpp
//  PetHotel
//
//  Created by Matt Josse on 3/23/18.
//  Copyright © 2018 Matt Josse. All rights reserved.
//

#ifndef petHotel_h
#define petHotel_h

#include <thread>
#include <mutex>
#include <condition_variable>

class PetHotel {
private:
  // Counts of animals currently in the hotel
  int numBirds = 0;
  int numDogs = 0;
  int numCats = 0;

  std::mutex mutex;
  std::condition_variable noBirdsNorDogs, noCats;
  void play() const;
public:
  void bird();
  void dog();
  void cat();
};

void PetHotel::play() const {
  for(volatile int i = 0; i < 10000; ++i) {} // Use the CPU
  std::this_thread::sleep_for(std::chrono::milliseconds(1));
}

void PetHotel::bird() {
  {
    std::unique_lock<std::mutex> lock(mutex);
    while (numCats > 0) {
      noCats.wait(lock);
    }
    // Critical section
    assert(numCats == 0);
    ++numBirds;
    // Release teh lock
  }
  play();
  {
    std::unique_lock<std::mutex> lock(mutex);
    // Critical section
    --numBirds;
    if (numBirds + numDogs == 0) {
      noBirdsNorDogs.notify_all();
    }
    // Release lock
  }
}

void PetHotel::dog() {
  {
    std::unique_lock<std::mutex> lock(mutex);
    while (numCats > 0) {
      noCats.wait(lock);
    }
    // Critical section
    assert(numCats == 0);
    ++numDogs;
    // Release the lock
  }
  play();
  {
    std::unique_lock<std::mutex> lock(mutex);
    // Critical section
    --numDogs;
    if (numDogs + numBirds == 0) {
      noBirdsNorDogs.notify_all();
    }
    // Release lock
  }
}

void PetHotel::cat() {
  {
    std::unique_lock<std::mutex> lock(mutex);
    while (numDogs + numBirds > 0) {
      noBirdsNorDogs.wait(lock);
    }
    // Critical section
    assert(numDogs + numBirds == 0);
    ++numCats;
    // Release the lock
  }
  play();
  {
    std::unique_lock<std::mutex> lock(mutex);
    // Critical section
    --numCats;
    if (numCats == 0) {
      noCats.notify_all();
    }
    // Release lock
  }
}

#endif /* petHotel_h */
