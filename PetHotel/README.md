# Pet Hotel: Multi-Threading and Condition Variables

A simulation of a pet hotel where each thread represents either a bird, a cat, or a dog. If there are any cats checked-in, no birds or dogs may check-in and vice versa. There are two versions: (1) A simple multithreaded simulation and (2) an attempt at achieving maximum fairness while minimizing loss of throughput.

This implementation received 1st place in a class-wide competition for the fastest (and correct) solution.

## Getting Started

This project was compiled on macOS High Sierra using the clang++ 2014 standard as follows:

```
$ clang++ -std=c++14 -fsanitize=undefined,thread -Wall -g hotelTests.cpp -o hotelTests
```

Note that the thread and address sanitizers must be used separately from one another. As is, this program runs cleanly with either sanitizer turned on.

Also note that **the default version is the fair protocol** (this must be changed in hotelTests.cpp source code). To run this version simply provide the name of the executable, the number of birds, the number of cats, and finally, the number of dogs. For example:

```
$ ./hotelTests 2 4 3
```

The expected output of the above command is something like this:

```
OK, waiting for threads to quit
bird counts
570
573
576
576
cat counts
527
527
527
dog counts
703
699
total birds 2295
total cats 1581
total dogs 1402
```

## Disclaimer

This program was completed as an assignment for CS 6013 at the University of Utah. The hotelTests.cpp was provided and only minor adjustments to the code were made. Both *.hpp files were written from scratch.
