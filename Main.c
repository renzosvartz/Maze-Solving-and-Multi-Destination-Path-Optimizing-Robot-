#include <stdio.h>
#include "Main.h"
#include "WIPDS4.h"
#include "API.h"

void log(char* text) {
    fprintf(stderr, "%s\n", text);
    fflush(stderr);
}

int main(int argc, char* argv[]) {
    log("Running...");
    WIPDS4 djikstras_solver;
    djikstras_solver.main();
    return 0;
}
