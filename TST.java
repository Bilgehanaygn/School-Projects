import java.util.ArrayList;
import java.util.List;

public class TST<Value> {
    public Node<Value> root;

    public static class Node<Value> {
        public char c;
        public Node<Value> left, mid, right;
        public Value val;
    }

    // Inserts the key value pair into ternary search tree
    public void put(String key, Value val) {
        if(key==null){
            throw new IllegalArgumentException("sorun var 4");
        }


        root = put(root, key, val, 0);
    }

    private Node<Value> put(Node<Value> x, String key, Value val, int index){

        char c = key.charAt(index);
        if(x == null){
            x = new Node<>();
            x.c = c;
        }
        if(c < x.c){
            x.left = put(x.left, key, val, index);
        }
        else if (c > x.c){
            x.right = put(x.right, key, val, index);
        }
        else if(index < key.length()-1){
            x.mid = put(x.mid, key, val, index+1);
        }
        else{x.val = val;}
        return x;
    }


    // Returns a list of values using the given prefix
    public List<Value> valuesWithPrefix(String prefix) {
        /* Code here */
        ArrayList<Value> valuesPrefix = new ArrayList<>();
        StringBuilder stringB = new StringBuilder(prefix);
        if(prefix == null){
            throw new IllegalArgumentException("sorun var 5-6");
        }
        Node<Value> x = get(root, prefix, 0);
        if(x == null){
            return valuesPrefix;
        }
        if(x.val!=null){
            valuesPrefix.add(x.val);
        }
        collect(x.mid, stringB, valuesPrefix);

        return valuesPrefix;
    }

    private void collect(Node<Value> x, StringBuilder prefix, ArrayList<Value> valuesArray){
        if(x==null){return;}
        collect(x.left, prefix, valuesArray);
        if(x.val!=null){valuesArray.add(x.val);}
        collect(x.mid, prefix, valuesArray);
        collect(x.right, prefix.append(x.c), valuesArray);
    }




    public boolean contains(String key){
        return get(key) != null;
    }

    public Value get(String key){
        if(key==null){
            throw new IllegalArgumentException("sorun var1");
        }
        if(key.length()==0){
            throw new IllegalArgumentException("sorun var2");
        }

        Node<Value> x = get(root, key, 0);
        if(x==null){
            return null;
        }
        return x.val;

    }

    public Node<Value> get(Node<Value> x, String key, int index){
        if(x==null){return null;}
        if(key.length()==0){
            throw new IllegalArgumentException("sorun var3");
        }
        char c = key.charAt(index);
        if(c<x.c){
            return get(x.left, key, index);
        }
        else if(c > x.c){
            return get(x.right, key, index);
        }
        else if(index < key.length()-1){
            return get(x.mid, key, index+1);
        }
        else{
            return x;
        }
    }





}