#include <string>
#include <sstream>
using namespace std;

// Checks whether character is a number
bool check_num (char c) {
    if ((c >= '0' && c <= '9')) {
        return true;
    }
    return false;
}

// Converts C-style string to int
// If value is not a digit or '.', it is skipped
// Ex: *c = "129abd", int = 129
//     *c = " 129abd4", int = 1294
int myAtoi( const char *c ) {
    int value = 0;
    while (*c != '\0') {
        if (!check_num(*c)) {
            c++;
            continue;
        }
        value *= 10;
        value += (int) (*c-'0');
        c++;
    }
    return value;
}

// Converts C++ string to numerical type
template <typename T>
T convertToNum (string const &test) {
    stringstream input(test);
    T result;
    return input >> result ? result : 0;
}

// Calculates the length of C-style string
int strlen (char *str) {
    int length = 0;
    while (*str++ != '\0') {
        length++;
    }
    return length;
}

// Reverses a C-style string in place
// Example: "Hello" -> "olleH"
void reverseHelper (char *str, int length) {
    int character, i, j;
    for (i = 0, j = length - 1; i < j; i++, j--) {
        character = str[i];
        str[i] = str[j];
        str[j] = character;
    }
}

// Reverses the words in a sentence (C-style string)
// Example: "This is a sentence!" -> "sentence! a is This"
void reverseString (char *str) {
    reverseHelper (str, strlen(str));
    int i = 0, j = 0;
    while (str[j] != '\0') {
        if (str[j] == ' ') {
            reverseHelper(str+i, j-i);
            i = j+1;
        }
        j++;
    }
    reverseHelper(str+i, j - i);
}

