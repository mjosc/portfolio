#include <iostream>
#include <string>
#include <cassert>

#include "pbmcompress-v1.h"
/** reads through a series of pbm files with various error cases, and confirms
 * that they are handled correctly.
 *
 */

void readBadPbmFileTests (std::string filepath) {

    /** test a file with the wrong magic number.
     *
     */
    std::cout << "wrong-magicnumber.pbm ";
    std::tuple<bool, int, int, std::vector<bool> *> wrong_number = read_pbm_file(filepath + "wrong-magicnumber.pbm");
    assert(!std::get<0>(wrong_number));

    /** test a file with empty contents.
     *
     */
    std::cout << "empty-file.pbm ";
    std::tuple<bool, int, int, std::vector<bool> *> empty = read_pbm_file(filepath + "empty-file.pbm");
    assert(!std::get<0>(empty));

    /** test a file that does not exist.
     *
     */
    std::cout << "in-my-mind.pbm ";
    std::tuple<bool, int, int, std::vector<bool> *> imaginary = read_pbm_file(filepath + "in-my-mind.pbm");
    assert(!std::get<0>(empty));


}

void readPbmFileTests(std::string filepath) {

    /** read through all files in the small-pbm files folder.
     *
     */
    std::string filename = "";

    bool status = false;
    int height = 0;
    int width = 0;
    std::vector<bool> raw;

    for (int i = 1; i < 15; i++) {

        filename = "x-";

        if (i < 10) {
            filename += "000" + std::to_string(i);
        }

        else if ( i > 9) {
            filename += "00" + std::to_string(i);
        }

        filename += ".pbm";

        std::cout << "FILENAME: " << filename << "\n";

        std::tuple<bool, int, int, std::vector<bool> *> readResult = read_pbm_file(filepath + filename);
        assert(std::get<0>(readResult));

        std::vector<bool>* original = std::get<3>(readResult);

        std::cout << "SUCCESS: opened and read :)\n";

        std::tuple<bool, int, int, std::vector<uint8_t > *> rleResult = rle(filepath + filename);
        assert(std::get<0>(rleResult));

        std::cout << "SUCCESS: file -> RLE :)\n";

        std::tuple<bool, int, int, std::vector<bool > *> unrleResult = unrle(filepath + filename);
        assert(std::get<0>(unrleResult));

        std::vector<bool>* unrle = std::get<3>(unrleResult);

        bool result = false;

        for (int j = 0; j < original->size() ; j++ ) {

            if (original->at(i) != unrle->at(i)) {
                result = true;
            }
        }

        if (result) {
            std::cout << "SUCCESS: file -> RLE -> file == file :)\n";
        }
        else {
            std::cout << "FAIL: file -> RLE -> file != file :(\n";
            std::cout << "original height: " << std::get<1>(readResult)
                      << "unrle height: " << std::get<1>(unrleResult) << "\n";
            std::cout << "original width: " << std::get<2>(readResult)
                      << "unrle width: " << std::get<2>(unrleResult) <<"\n";
        }

    }
}

void runAllTests () {

    /** update this explicit file path to the directory where the /pbm-test-files/ folder is located
     * on your local computer to perform tests for full coverage of the read_pbm_file and read_ch_file
     * methods.
     *
     */

    std::string filepath = "/Users/znickle/CS 6015-001 Spring 2018/cs6015-team3/pbm-test-files/";
    readPbmFileTests(filepath);
    readBadPbmFileTests(filepath);
}
