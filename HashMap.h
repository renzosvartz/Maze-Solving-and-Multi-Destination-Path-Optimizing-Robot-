#ifndef HASHMAP_H
#define HASHMAP_H

typedef struct KeyValuePair {
    char* key;
    void* value;
    struct KeyValuePair* next; // Pointer to the next key-value pair in case of collisions
} KeyValuePair;

typedef struct {
    int capacity; // Capacity of the HashMap
    int size; // Number of key-value pairs currently in the HashMap
    KeyValuePair** buckets; // Array of pointers to KeyValuePair (buckets)
} HashMap;

HashMap* createHashMap(int capacity);
void put(HashMap* map, const char* key, void* value);
void* get(HashMap* map, const char* key);
int containsKey(HashMap* map, const char* key);
void removeKey(HashMap* map, const char* key);
void clearHashMap(HashMap* map);

#endif /* HASHMAP_H */
