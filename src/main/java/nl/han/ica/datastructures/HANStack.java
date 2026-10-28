package nl.han.ica.datastructures;

import nl.han.ica.icss.ast.AST;

public class HANStack<T> implements IHANStack<T>{
    private HANLinkedList list = new HANLinkedList();

    /**
     * pushes value T to the top of the stack
     * @param value value to push
     */
    public void push(T value){
        list.addFirst(value);
    }

    /**
     * Pops (and removes) value at top of stack
     * @return popped value
     */
    public T pop(){
        T returnValue= (T) list.getFirst();
        list.removeFirst();
        return returnValue;
    }

    /**
     * Peeks at the top of the stack. Does not remove anything
     * @return value at the top of the stack
     */
    public T peek(){
        return (T) list.getFirst();
    }
}
