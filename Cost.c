#include "Cost.h"
#include <stdio.h>
#include <stdlib.h>

Cost* createCost() {
    Cost* newCost = (Cost*)malloc(sizeof(Cost));
    if (newCost == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    newCost->cost = 0;
    return newCost;
}

int getCost(Cost* cost) {
    return cost->cost;
}

void addCost(Cost* cost, int additionalCost) {
    cost->cost += additionalCost;
}

void setCost(Cost* cost, int newCost) {
    cost->cost = newCost;
}

void destroyCost(Cost* cost) {
    free(cost);
}
