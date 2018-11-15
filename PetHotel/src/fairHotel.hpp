//
//  fairHotel.hpp
//  PetHotel
//
//  Created by Matt Josse on 3/28/18.
//  Copyright © 2018 Matt Josse. All rights reserved.
//

#ifndef fairHotel_h
#define fairHotel_h

#include <thread>
#include <mutex>
#include <condition_variable>

class FairHotel {
private:
  // Counts of animals currently in the hotel
  int numBirds = 0;
  int numDogs = 0;
  int numCats = 0;
  // Counts of animals who have previously checked-in
  int totalBirds = 0;
  int totalDogs = 0;
  int totalCats = 0;

  bool catTurn = false;
  std::mutex mutex;
  std::condition_variable noBirdsNorDogs, noCats;
  void play() const;
public:
  void bird();
  void dog();
  void cat();
};

void FairHotel::play() const {
  for(volatile int i = 0; i < 10000; ++i) {} // Use the CPU
  std::this_thread::sleep_for(std::chrono::milliseconds(1));
}

void FairHotel::bird() {
  {
    std::unique_lock<std::mutex> lock(mutex);
    // Even if there are no cats, if it is the cats' turn, wait
    while (numCats > 0 || catTurn) {
      noCats.wait(lock);
    }
    // Critical section
    assert(numCats == 0);
    ++numBirds;
    ++totalBirds;
    // Release the lock
  }
  play();
  {
    std::unique_lock<std::mutex> lock(mutex);
    // Critical section
    --numBirds;
    if (numBirds + numDogs == 0 || totalCats < totalBirds) {
      // There are either no more birds or dogs waiting to check in OR more
      // dogs and birds than cats have been checked in.
      catTurn = true;
      noBirdsNorDogs.notify_all();
    } else {
      catTurn = false;
    }
    // Release lock
  }
}

void FairHotel::dog() {
  {
    std::unique_lock<std::mutex> lock(mutex);
    // Even if there are no cats, if it is the cats' turn, wait
    while (numCats > 0 || catTurn) {
      noCats.wait(lock);
    }
    // Critical section
    assert(numCats == 0);
    ++numDogs;
    ++totalDogs;
    // Release the lock
  }
  play();
  {
    std::unique_lock<std::mutex> lock(mutex);
    // Critical section
    --numDogs;
    if (numDogs + numBirds == 0 || totalCats < totalDogs) {
      // There are either no more birds or dogs waiting to check in OR more
      // dogs and birds than cats have been checked in.
      catTurn = true;
      noBirdsNorDogs.notify_all();
    } else {
      catTurn = false;
    }
    // Release lock
  }
}

void FairHotel::cat() {
  {
    std::unique_lock<std::mutex> lock(mutex);
    // Even if there are no dogs or birds, if it is not the cats' turn, wait
    while (numDogs + numBirds > 0 || !catTurn) {
      noBirdsNorDogs.wait(lock);
    }
    // Critical section
    assert(numDogs + numBirds == 0);
    ++numCats;
    ++totalCats;
    // Release the lock
  }
  play();
  {
    std::unique_lock<std::mutex> lock(mutex);
    // Critical section
    --numCats;
    if (numCats == 0 || totalCats > totalDogs || totalCats > totalBirds) {
      // There are either no more cats waiting to check in OR more cats than
      // dogs have been checked in.
      catTurn = false;
      noCats.notify_all();
    } else {
      catTurn = true;
    }
    // Release lock
  }
}

#endif /* fairHotel_h */
