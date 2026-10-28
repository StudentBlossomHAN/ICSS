package nl.han.ica.datastructures;

import nl.han.ica.datastructures.Node;

public class HANLinkedList<T> implements IHANLinkedList<T> {


    private Node<T> head = null;
    private final Node<T> header = new Node<>(null);
    /**
     * Adds value to the front of the list
     * @param value generic value to be added
     */
    private int size = 0;

    public void addFirst(T value){
    size++;

    }

    private Node getNode(int position){
        if(position < 0 || position >= size){
            System.out.println("Position out of range");
            Node nullNode = new Node<>(null);
            nullNode.next = null;
            return nullNode;
        } else {
            Node returnNode = head;
            for(int i = 1; i < position; i++){
                returnNode = returnNode.getNext();
            }
            return returnNode;
        }
    }

    /**
     * Clears list. Size equals 0 afterwards
     */
    public  void clear(){
    size =0;
    //ik heb tijdsdruk dus ik weet dat dit mogelijk een memory leak is...
        //TODO: geen memory leak
        head=null;
    }

    /**
     * Adds value to index position
     * @param index the position
     * @param value the value to add at index
     */
    public void insert(int pos, T value){
        Node nodeRefrence = head;
        if(pos!=0){
            nodeRefrence this.getNode(pos-1);
        }
        Node newNode = new Node(value);
        newNode.setNext(nodeRefrence.getNext());
        nodeRefrence.setNext(newNode);
        size++;
    }

    /**
     * Deletes value at position
     * @param pos position where value is deleted
     */
    public void delete(int pos){
        if(pos==0){
        head=head.getNext();
        } else if(pos<size-1){
            this.getNode(pos-1).setNext(this.getNode(pos+1));
        } else {
            this.getNode(pos-1).setNext(null);
        }
        size--;
    }

    /**
     * Returns generic value T at postion
     * @param pos position to look up value
     * @return value at position pos
     */
    public T get(int pos){
        return getNode(pos).getData();

    }

    /**
     * Removes first element
     */
    public void removeFirst(){
        this.delete(size-1);
    }

    /**
     * Returns first element in O(n) time
     * @return first element
     */
    public T getFirst(){
        return this.get(size-1);
    }


    /**
     * Determines size of the list, equals the number of stored items but not the header node
     * @return number of items in list
     */
    int getSize(){
        return size;
    }
}
