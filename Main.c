#include <stdio.h>
#include "Main.h"
#include "WIPDS4.h"
#include "API.h"

void logg(char* text) {
    fprintf(stderr, "%s\n", text);
    fflush(stderr);
}

int main(int argc, char* argv[]) {
    logg("Running...");
    WIPDS4main();
    return 0;
}
