#ifndef ARRAYLIST_H
#define ARRAYLIST_H

// Define the ArrayList struct
typedef struct ArrayList {
    void* *array;
    int size;
    int capacity;
} ArrayList;

// Function prototypes
ArrayList *createArrayList();
void destroyArrayList(ArrayList *list);
void addElement(ArrayList *list, void *element);
int getElement(ArrayList *list, int index);
int getSize(ArrayList *list);
int isArrayListEmpty(ArrayList *list);

#endif /* ARRAYLIST_H */
