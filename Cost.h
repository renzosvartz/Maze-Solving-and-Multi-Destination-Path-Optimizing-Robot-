#ifndef COST_H
#define COST_H

typedef struct {
    int cost;
} Cost;

Cost* createCost();
int getCost(Cost* cost);
void addCost(Cost* cost, int additionalCost);
void setCost(Cost* cost, int newCost);
void destroyCost(Cost* cost);

#endif /* COST_H */
