#ifndef LOCATION_H
#define LOCATION_H

typedef struct {
    int x;
    int y;
} Location;

Location* createLocation();
int getX(Location* location);
int getY(Location* location);
void printLocation(Location* location);
char* locationToString(Location* location);
void destroyLocation(Location* location);

#endif /* LOCATION_H */
