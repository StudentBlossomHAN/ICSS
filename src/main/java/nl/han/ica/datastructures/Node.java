package nl.han.ica.datastructures;

public class Node<T> {
    T data;
    Node<T> next = null;
    public Node(T data){
        this.data = data;
    };
    public Node getNext(){
        return next;
    }
    public void setNext(Node next){
        this.next = next;
    }
    public T getData(){
        return this.data;
    }
}
