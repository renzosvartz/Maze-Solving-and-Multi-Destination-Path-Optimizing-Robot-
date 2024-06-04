#include "Stack.h"
#include <stdio.h>
#include <stdlib.h>

// Function to create a new stack
Stack* createStack() {
    Stack* stack = (Stack*)malloc(sizeof(Stack));
    if (stack == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    stack->top = NULL;
    return stack;
}

// Function to push an element onto the stack
void push(Stack* stack, char* data) {
    StackNode* newNode = (StackNode*)malloc(sizeof(StackNode));
    if (newNode == NULL) {
        fprintf(stderr, "Error: Memory allocation failed\n");
        exit(EXIT_FAILURE);
    }
    newNode->data = data;
    newNode->next = stack->top;
    stack->top = newNode;
}

// Function to pop an element from the stack
char* pop(Stack* stack) {
    if (isEmpty(stack)) {
        fprintf(stderr, "Error: Stack is empty, cannot pop\n");
        exit(EXIT_FAILURE);
    }
    int data = stack->top->data;
    StackNode* temp = stack->top;
    stack->top = stack->top->next;
    free(temp);
    return data;
}

// Function to peek at the top element of the stack
char* peek(Stack* stack) {
    if (isEmpty(stack)) {
        fprintf(stderr, "Error: Stack is empty, cannot peek\n");
        exit(EXIT_FAILURE);
    }
    return stack->top->data;
}

// Function to check if the stack is empty
int isStackEmpty(Stack* stack) {
    return (stack->top == NULL);
}

// Function to clear the stack
void clearStack(Stack* stack) {
    while (!isEmpty(stack)) {
        pop(stack);
    }
}

int stackLength(Stack* stack) {
    int length = 0;
    StackNode* current = stack->top;
    while (current != NULL) {
        length++;
        current = current->next;
    }
    return length;
}

// Function to print the stack
void printStack(Stack* stack) {
    printf("Stack: ");
    StackNode* current = stack->top;
    while (current != NULL) {
        printf("%d ", current->data);
        current = current->next;
    }
    printf("\n");
}
