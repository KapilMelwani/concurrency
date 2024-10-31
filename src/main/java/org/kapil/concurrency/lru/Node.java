package org.kapil.concurrency.lru;

class Node {
    int key;
    int value;
    long expiryTime;
    int priority;
    Node prev;
    Node next;
    
    public Node(int key, int value, long expiryTime, int priority) {
        this.key = key;
        this.value = value;
        this.expiryTime = expiryTime;
        this.priority = priority;
    }
}