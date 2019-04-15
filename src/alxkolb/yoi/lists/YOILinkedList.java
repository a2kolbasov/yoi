package alxkolb.yoi.lists;

public class YOILinkedList implements YOILists {
    private class Node {
        Object data;
        Node prev;
        Node next;

        Node(Object d) {
            setData(d);
        }

        Object getData() {
            return data;
        }

        void setData(Object data) {
            this.data = data;
        }

        Node getPrev() {
            return prev;
        }

        void setPrev(Node prev) {
            this.prev = prev;
        }

        Node getNext() {
            return next;
        }

        void setNext(Node next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }

    private Node first;
    private Node last;
    private int count;

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public void add(Object o) {
        if (first == null) {
            first = new Node(o);
            last = first;
        } else {
            Node node = new Node(o);
            node.setPrev(last);
            last.setNext(node);
            last = node;
        }
        count++;
    }

    private Node getNode(int index) throws IndexOutOfBoundsException {
        if (index >= count) {
            throw new IndexOutOfBoundsException();
        }
        int i;
        Node node;
        if (count == 1) {
            node = first;
        } else if (count == 2) {
            node = index == 0 ? first : last;
        } else if (index < count / 2) {
            i = 0;
            node = first;
            while (i < index) {
                node = node.getNext();
                i++;
            }
        } else {
            i = count - 1;
            node = last;
            while (i > index) {
                node = node.getPrev();
                i--;
            }
        }
        return node;
    }

    public Object get(int index) throws IndexOutOfBoundsException {
        if (index >= count) {
            throw new IndexOutOfBoundsException();
        }
        return getNode(index).getData();
    }

    public void remove(int index) throws IndexOutOfBoundsException {
        if (index >= count) {
            throw new IndexOutOfBoundsException();
        }
        Node node = getNode(index);
        Node prev = node.getPrev();
        Node next = node.getNext();
        if (prev != null) {
            prev.setNext(next);
        } else {
            first = next;
        }
        if (next != null) {
            next.setPrev(prev);
        } else {
            last = prev;
        }
        count--;
    }

    public boolean contains(Object o) {
        for (Node current = first; current != null; current = current.getNext()) {
            if (current.getData().equals(o)) {
                return true;
            }
        }
        return false;
    }

    public Object poll() throws EmptyListException {
        if (count == 0) {
            throw new EmptyListException();
        }
        Object data = last.getData();
        remove(count);
        return data;
    }

    public Object peek() throws EmptyListException {
        if (count == 0) {
            throw new EmptyListException();
        }
        return last.getData();
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("[");
        for (Node current = first; current != last; current = current.getNext()) {
            string.append(current.toString()).append(", ");
        }
        if (last != null) {
            string.append(last.toString()).append("]");
        } else {
            string.append("]");
        }
        return string.toString();
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (int i = 0; i < size(); i++) {
            hashCode += get(i).hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        YOILinkedList list = (YOILinkedList) obj;
        for (int i = 0; i < size(); i++) {
            if (!get(i).equals(list.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void remove(Object o) {
        remove(Integer.valueOf(o.toString()).intValue());
    }
}
