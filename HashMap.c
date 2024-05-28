#include "HashMap.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Define a hash function to map keys to bucket indices
int hash(const char* key, int capacity) {
    // This is a simple hash function for demonstration purposes
    int sum = 0;
    for (int i = 0; key[i] != '\0'; i++) {
        sum += key[i];
    }
    return sum % capacity;
}

// Function to create a new HashMap
HashMap* createHashMap(int capacity) {
    HashMap* map = (HashMap*)malloc(sizeof(HashMap));
    if (map == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    map->capacity = capacity;
    map->size = 0;
    map->buckets = (KeyValuePair**)calloc(capacity, sizeof(KeyValuePair*));
    if (map->buckets == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    return map;
}

// Function to put a key-value pair into the HashMap
void put(HashMap* map, const char* key, int value) {
    int index = hash(key, map->capacity);
    KeyValuePair* pair = (KeyValuePair*)malloc(sizeof(KeyValuePair));
    if (pair == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    pair->key = strdup(key); // Allocate memory for key and copy it
    pair->value = value;
    // Insert at the beginning of the bucket (no collision resolution for simplicity)
    pair->next = map->buckets[index];
    map->buckets[index] = pair;
    map->size++;
}

// Function to get the value associated with a key from the HashMap
int get(HashMap* map, const char* key) {
    int index = hash(key, map->capacity);
    KeyValuePair* current = map->buckets[index];
    while (current != NULL) {
        if (strcmp(current->key, key) == 0) {
            return current->value;
        }
        current = current->next;
    }
    fprintf(stderr, "Error: Key '%s' not found in HashMap\n", key);
    exit(EXIT_FAILURE);
}

// Function to check if a key exists in the HashMap
int containsKey(HashMap* map, const char* key) {
    int index = hash(key, map->capacity);
    KeyValuePair* current = map->buckets[index];
    while (current != NULL) {
        if (strcmp(current->key, key) == 0) {
            return 1; // Key found
        }
        current = current->next;
    }
    return 0; // Key not found
}

// Function to remove a key from the HashMap
void removeKey(HashMap* map, const char* key) {
    int index = hash(key, map->capacity);
    KeyValuePair* current = map->buckets[index];
    KeyValuePair* prev = NULL;
    while (current != NULL) {
        if (strcmp(current->key, key) == 0) {
            if (prev == NULL) {
                map->buckets[index] = current->next;
            } else {
                prev->next = current->next;
            }
            free(current->key);
            free(current);
            map->size--;
            return;
        }
        prev = current;
        current = current->next;
    }
    fprintf(stderr, "Error: Key '%s' not found in HashMap\n", key);
    exit(EXIT_FAILURE);
}

// Function to clear the HashMap
void clearHashMap(HashMap* map) {
    for (int i = 0; i < map->capacity; i++) {
        KeyValuePair* current = map->buckets[i];
        while (current != NULL) {
            KeyValuePair* temp = current;
            current = current->next;
            free(temp->key);
            free(temp);
        }
        map->buckets[i] = NULL;
    }
    map->size = 0;
}
