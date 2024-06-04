#include "Location.h"
#include <stdio.h>
#include <stdlib.h>

Location* createLocation() {
    Location* newLocation = (Location*)malloc(sizeof(Location));
    if (newLocation == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    newLocation->x = 0;
    newLocation->y = 0;
    return newLocation;
}

int getX(Location* location) {
    return location->x;
}

int getY(Location* location) {
    return location->y;
}

void printLocation(Location* location) {
    printf("(%d, %d)\n", location->x, location->y);
}

char* locationToString(Location* location) {
    char* str = (char*)malloc(20 * sizeof(char));
    if (str == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    sprintf(str, "(%d, %d)", location->x, location->y);
    return str;
}

void destroyLocation(Location* location) {
    free(location);
}
