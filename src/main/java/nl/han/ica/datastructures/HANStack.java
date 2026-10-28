package nl.han.ica.datastructures;

import nl.han.ica.icss.ast.AST;

public class HANStack<T> implements IHANStack<T>{
    private IHANStack<T> list;

    public HANStack(){
        list = new HANStack();
    }
    void push(T data) {
        list.addFirst(data);
    }

}
