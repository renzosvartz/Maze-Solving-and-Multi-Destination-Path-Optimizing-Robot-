#ifndef STACK_H
#define STACK_H

// Define the Stack structure
typedef struct StackNode {
    // Define the structure of a node in the stack
    // You can modify this structure based on your requirements
    // For example, if you're storing strings in the stack, you might use char* data;
    // If you're storing integers, you might use int data;
    // Feel free to customize it as needed.
    // For now, let's assume we're storing integers.
    int data;
    struct StackNode* next;
} StackNode;

typedef struct {
    StackNode* top;
} Stack;

// Function prototypes
Stack* createStack();
void push(Stack* stack, char* data);
char* pop(Stack* stack);
char* peek(Stack* stack);
int isStackEmpty(Stack* stack);
void clearStack(Stack* stack);
int stackLength(Stack* stack);
void printStack(Stack* stack);

#endif /* STACK_H */
