#include <stdlib.h>
#include "ArrayList.h"

ArrayList *createArrayList() {
    ArrayList *list = (ArrayList *)malloc(sizeof(ArrayList));
    if (list == NULL) {
        return NULL; // Memory allocation failed
    }
    list->array = NULL;
    list->size = 0;
    list->capacity = 0;
    return list;
}

void destroyArrayList(ArrayList *list) {
    free(list->array);
    free(list);
}

void addElement(ArrayList *list, void *element) {
    if (list->size >= list->capacity) {
        int newCapacity = (list->capacity == 0) ? 1 : list->capacity * 2;
        void **newArray = realloc(list->array, newCapacity * sizeof(void *));
        if (newArray == NULL) {
            return; // Memory allocation failed
        }
        list->array = newArray;
        list->capacity = newCapacity;
    }
    list->array[list->size++] = element;
}

char * getElement(ArrayList *list, int index) {
    if (index < 0 || index >= list->size) {
        return -1; // Invalid index
    }
    return list->array[index];
}

int getSize(ArrayList *list) {
    return list->size;
}

int isArrayListEmpty(ArrayList *list) {
    return (list->size == 0);
}
