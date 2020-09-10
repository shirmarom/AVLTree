
/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {

    private AVLNode min;
    private AVLNode max;
    private AVLNode root = new AVLNode(0, "", true);
    private boolean is_successor = false; // Used in delete function only, to avoid changing sizes twice

    /**
     * An empty construction, so we can initialize the fields and the whole tree
     */

    public AVLTree() {
    }


    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     */

    public boolean empty() {
        AVLNode perm_root = (AVLNode)this.root;
        return perm_root.isNull();
    }

    /**
     * public String search(int k)
     * <p>
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     */

    public String search(int k) {
        if (this.getRoot() == null) {
            return null;
        }
        AVLNode x = (AVLNode)this.getRoot();
        while (!x.isNull() && k != x.getKey()) {
            if (k < x.getKey()) {
                x = (AVLNode)x.getLeft();
            } else {
                x = (AVLNode)x.getRight();
            }
        }
        if (x.isNull() == true) {
            return null;
        }
        return x.getValue();
    }

    /**
     * public int insert(int k, String i)
     * <p>
     * inserts an item with key k and info i to the AVL tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
     * returns -1 if an item with key k already exists in the tree.
     */

    public int insert(int k, String i) {
        if (this.search(k) != null) {
            return -1;
        }
        AVLNode y = new AVLNode(k, i, false);
        if (this.root.isNull()) {
            this.setRoot(y);
            return 0;
        }
        int num_of_rotates = 0;
        AVLNode x = finding_a_node(this.root, k, true);
        if (x.getKey() < k) {
            x.setRight(y);
        } else {
            x.setLeft(y);
        }
        if (k > this.max.getKey()) {
            this.max = y;
        }
        if (k < this.min.getKey()) {
            this.min = y;
        }
        y.setParent(x);
        y.setLeft(new AVLNode(0, "", true));
        y.setRight(new AVLNode(0, "", true));
        y.setHeight(0);
        y.setSize(1);
        while (!x.isNull()) {
            if (Math.abs(x.getLeft().getHeight() - x.getRight().getHeight()) <= 1) {
                x.setSize(((AVLNode)x.getLeft()).getSize() + ((AVLNode)x.getRight()).getSize() + 1); // Going all the way up to the root anyway
                x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
                x = (AVLNode) x.getParent();
            }
            else {
                boolean isDouble = rotate(x);
                if (!isDouble) {
                    x = (AVLNode)x.getParent();
                }
                num_of_rotates += 1;
            }
        }
        return num_of_rotates;
    }

    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
     * returns -1 if an item with key k was not found in the tree.
     */

    public int delete(int k) { // We split it into two different functions because of TreeList
        if (this.search(k) == null) {
            return -1;
        }
        if (!is_successor) {
            if (k == max.getKey()) {
                this.max = (AVLNode) predecessor(max);
            }
            if (k == min.getKey()) {
                this.min = (AVLNode) successor(min);
            }
        }
        AVLNode x = finding_a_node(this.root, k, false);
        int num_of_rotates = delete_node(x);
        return num_of_rotates;
    }

    protected int delete_node(AVLNode x) {
        int num_of_rotates = 0;
        if (x.getSize() == 1) { // x is a leaf
            if (x == this.root) { // x is the only node in the tree
                this.root = new AVLNode(0, "", true);
            }
            if (x.getParent().getRight() == x) {
                x.getParent().setRight(new AVLNode(0, "", true));
            } else {
                x.getParent().setLeft(new AVLNode(0, "", true));
            }
        } else { // x is not a leaf
            AVLNode right_child = (AVLNode)x.getRight();
            AVLNode left_child = (AVLNode)x.getLeft();
            if (right_child.isNull() == true || left_child.isNull() == true) { // x has only one child
                if (x.getParent().getLeft() == x) {
                    if (left_child.isNull() != true) {
                        x.getParent().setLeft(left_child);
                        left_child.setParent(x.getParent());
                    } else {
                        x.getParent().setLeft(right_child);
                        right_child.setParent(x.getParent());
                    }
                    x = (AVLNode)x.getParent();
                } else {
                    if (left_child.isNull() != true) {
                        x.getParent().setRight(left_child);
                        left_child.setParent(x.getParent());
                    } else {
                        x.getParent().setRight(right_child);
                        right_child.setParent(x.getParent());
                    }
                    x = (AVLNode)x.getParent();
                }
            } else {
                // x has two children; need to find x's successor
                AVLNode suc = (AVLNode)successor(x);
                is_successor = true;
                num_of_rotates += this.delete(suc.getKey()); // The successor always has one right son, or no sons at all;
                // Therefore, I will only call this function once
                if (x.getParent().getLeft() == x) {
                    x.getParent().setLeft(suc);
                } else {
                    x.getParent().setRight(suc);
                }
                suc.setLeft(x.getLeft());
                suc.setRight(x.getRight());
                suc.setParent(x.getParent());
                if (!((AVLNode)suc.getRight()).isNull()) {
                    suc.getRight().setParent(suc);
                }
                if (!((AVLNode)suc.getLeft()).isNull()) {
                    suc.getLeft().setParent(suc);
                }
                x = suc;
            }
        }
        while (!x.isNull()) {
            if (Math.abs(x.getLeft().getHeight() - x.getRight().getHeight()) <= 1) {
                if (!is_successor) {
                    // Going all the way up to the root anyway
                    x.setSize(((AVLNode)x.getLeft()).getSize() + ((AVLNode)x.getRight()).getSize() + 1);
                    x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
                }
                if (((AVLNode)x.getParent()).isNull() && this.root != x) {
                    this.root = x;
                }
                x = (AVLNode) x.getParent();
            }
            else {
                boolean isDouble = rotate(x);
                if (!isDouble) {
                    x = (AVLNode)x.getParent();
                }
                num_of_rotates += 1;
            }
        }
        is_successor = false;
        return num_of_rotates;
    }

    /**
     * private AVLNode finding_a_node(AVLNode root, int k, boolean is_insert)
     *
     * This function gets a node we want to begin searching with (A root),
     * a key and a parameter that defines whether we want this node to be the
     * parent of the to-be inserted node (the function returns its parent)
     * if the parameter == false, the function returns the node itself
     * so it can be deleted.
     */

    private AVLNode finding_a_node(AVLNode root, int k, boolean is_insert) {
        if (is_insert == true) {
            AVLNode prev = root;
            while (!root.isNull()) {
                if (k < root.getKey()) {
                    prev = root;
                    root = (AVLNode) root.getLeft();
                } else {
                    prev = root;
                    root = (AVLNode) root.getRight();
                }
            }
            return prev;
        }
        else {
            while (root.getKey() != k) { // Accessing the to-be deleted node, marking it as 'x'
                if (k < root.getKey()) {
                    root = (AVLNode)root.getLeft();
                } else {
                    root = (AVLNode)root.getRight();
                }
            }
            return root;
        }
    }

    /**
     * private void rotate(AVLNode x)
     * <p>
     * This function gets a node that is an AVL-criminal.
     * It rotates the tree in one out of four ways (for rebalancing):
     * Left rotate, right rotate, left-right rotate and right-left rotate.
     * The last two happen by calling this function twice: once for the first rotate (R or L),
     * and second for the final rotate.
     * This function also updates the height and the size of the affected-by-the-rotate nodes.
     */

    public boolean rotate(AVLNode x) {
        if (x.getLeft().getHeight() - x.getRight().getHeight() > 0) { // Right/LR rotate
            if (x.getLeft().getLeft().getHeight() - x.getLeft().getRight().getHeight() >= 0) { // Case 1: Right rotate only
                AVLNode current_root = (AVLNode) x.getLeft();
                AVLNode right_child = (AVLNode)current_root.getRight();
                current_root.setParent(x.getParent());
                x.setLeft(right_child);
                current_root.setRight(x);
                AVLNode parent = (AVLNode)current_root.getParent();
                if (parent.isNull() != true) {
                    if (x.getParent().getRight() == x) {
                        x.getParent().setRight(current_root);
                    } else {
                        x.getParent().setLeft(current_root);
                    }
                }
                x.getLeft().setParent(x);
                x.setParent(current_root);
                if (parent.isNull() == true) {
                    this.root = current_root;
                }
                AVLNode left_son = (AVLNode) current_root.getLeft();
                AVLNode left_grandson = (AVLNode) x.getLeft();
                AVLNode right_grandson = (AVLNode) x.getRight();
                x.setSize(left_grandson.getSize() + right_grandson.getSize() + 1);
                current_root.setSize(x.getSize() + left_son.getSize() + 1);
                x.setHeight(Math.max(left_grandson.getHeight(), right_grandson.getHeight()) + 1);
                current_root.setHeight(Math.max(left_son.getHeight(), x.getHeight()) + 1);
                return false;
            } else { // Left rotate, to get to case 1
                AVLNode current_root = (AVLNode) x.getLeft().getRight();
                x.getLeft().setRight(current_root.getLeft());
                current_root.setLeft(x.getLeft());
                current_root.setParent(x);
                x.getLeft().setParent(current_root);
                x.setLeft(current_root);
                current_root.getLeft().getRight().setParent(current_root.getLeft());
                AVLNode right_son = (AVLNode) current_root.getRight();
                AVLNode left_son = (AVLNode) current_root.getLeft();
                AVLNode left_grandson = (AVLNode) left_son.getLeft();
                AVLNode right_grandson = (AVLNode) left_son.getRight();
                left_son.setSize(right_grandson.getSize() + left_grandson.getSize() + 1);
                current_root.setSize(left_son.getSize() + right_son.getSize() + 1);
                left_son.setHeight(Math.max(left_grandson.getHeight(), right_grandson.getHeight()) + 1);
                current_root.setHeight(Math.max(right_son.getHeight(), x.getHeight()) + 1);
                return true;
            }
        } else { // Left/RL rotate
            if (x.getRight().getLeft().getHeight() - x.getRight().getRight().getHeight() <= 0) { // Case 2: left rotate only
                AVLNode current_root = (AVLNode)x.getRight();
                current_root.setParent(x.getParent());
                x.setRight(current_root.getLeft());
                current_root.setLeft(x);
                AVLNode parent = (AVLNode)x.getParent();
                if (parent.isNull() != true) {
                    if (x.getParent().getRight() == x) {
                        x.getParent().setRight(current_root);
                    } else {
                        x.getParent().setLeft(current_root);
                    }
                }
                x.getRight().setParent(x);
                x.setParent(current_root);
                if (parent.isNull() == true) {
                    this.root = current_root;
                }
                AVLNode right_son = (AVLNode) current_root.getRight();
                AVLNode left_grandson = (AVLNode) x.getLeft();
                AVLNode right_grandson = (AVLNode) x.getRight();
                x.setSize(left_grandson.getSize() + right_grandson.getSize() + 1);
                current_root.setSize(x.getSize() + right_son.getSize() + 1);
                x.setHeight(Math.max(left_grandson.getHeight(), right_grandson.getHeight()) + 1);
                current_root.setHeight(Math.max(right_son.getHeight(), x.getHeight()) + 1);
                return false;
            } else { // Right rotate, to get to case 2
                AVLNode current_root = (AVLNode) x.getRight().getLeft();
                x.getRight().setLeft(current_root.getRight());
                current_root.getRight().setParent(x.getRight());
                current_root.setRight(x.getRight());
                current_root.setParent(x);
                x.getRight().setParent(current_root);
                x.setRight(current_root);
                current_root.getRight().getLeft().setParent(current_root.getRight());
                AVLNode right_son = (AVLNode) current_root.getRight();
                AVLNode left_son = (AVLNode) current_root.getLeft();
                AVLNode left_grandson = (AVLNode) right_son.getLeft();
                AVLNode right_grandson = (AVLNode) right_son.getRight();
                right_son.setSize(right_grandson.getSize() + left_grandson.getSize() + 1);
                current_root.setSize(left_son.getSize() + right_son.getSize() + 1);
                right_son.setHeight(Math.max(left_grandson.getHeight(), right_grandson.getHeight()) + 1);
                current_root.setHeight(Math.max(right_son.getHeight(), x.getHeight()) + 1);
                return true;
            }
        }
    }

    /**
     * private IAVLNode predecessor(IAVLNode x)
     * <p>
     * This function gets a node and returns its predecessor, if exists
     * if it's the minimum - it has no predecessor and a sentinel is returned
     */

    private IAVLNode predecessor(IAVLNode x) {
        AVLNode right_child = (AVLNode)x.getRight();
        AVLNode parent = (AVLNode)x.getParent();
        AVLNode left_child = (AVLNode)x.getLeft();
        if (!left_child.isNull()) {
            x = (AVLNode)x.getLeft();
            right_child = (AVLNode)x.getRight();
            while (!right_child.isNull()) {
                x = right_child;
                right_child = (AVLNode)x.getRight();
            }
            return x;
        } else {
            while (parent.isNull() != true && x.getParent().getRight() != x) {
                x = x.getParent();
            }
            return x.getParent();
        }
    }

    /**
     * private IAVLNode successor(IAVLNode x)
     * <p>
     * This function gets a node and returns its successor, if exists
     * if it's the maximum - it has no successor and null is returned
     */

    private IAVLNode successor(IAVLNode x) {
        AVLNode right_child = (AVLNode)x.getRight();
        AVLNode parent = (AVLNode)x.getParent();
        AVLNode left_child = (AVLNode)x.getLeft();
        if (right_child.isNull() != true) {
            x = (AVLNode)x.getRight();
            left_child = (AVLNode)x.getLeft();
            while (left_child.isNull() != true) {
                x = left_child;
                left_child = (AVLNode)left_child.getLeft();
            }
            return x;
        } else {
            while (parent.isNull() != true && x.getParent().getLeft() != x) {
                x = x.getParent();
            }
        }
        return x.getParent();
    }

    /**
     * public String min()
     * <p>
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty
     */

    public String min() {
        if (min != null) {
            return this.min.getValue();
        }
        return null;
    }

    /**
     * public String max()
     * <p>
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty
     */

    public String max() {
        if (max != null) {
            return this.max.getValue();
        }
        return null;
    }

    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     */

    public int[] keysToArray() {
        int[] arr = new int[this.size()];
        if (this.size() == 0) { // dealing with an empty tree
            return arr;
        }
        keysToArray_rec((AVLNode) this.getRoot(), 0, arr); // 0 is the first index of the array
        return arr;
    }

    private int keysToArray_rec(AVLNode node, int i, int[] arr) {
        AVLNode right_child = (AVLNode)node.getRight();
        AVLNode left_child = (AVLNode)node.getLeft();
        if (node.getHeight() == 0) { // adding leaves to the array, and adding 1 to the index
            if (!node.isNull()) {
                arr[i] = node.getKey();}
            i++;
            return i;
        }
        if (!left_child.isNull()) { // rec will go first to the left son
            i = keysToArray_rec((AVLNode) node.getLeft(), i, arr);
        }
        if (!node.isNull()) {
            arr[i] = node.getKey(); // after we're done with the left son we are adding the node itself
        }
        i++;
        if (!right_child.isNull()) { // and then we will go to the right son
            i = keysToArray_rec((AVLNode) node.getRight(), i, arr);
        }
        return i;
    }

    /**
     * public String[] infoToArray()
     * <p>
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     */

    public String[] infoToArray() // the same as keyToArray but adding the value instead of the key
    {
        String[] arr = new String[this.size()];
        if (this.size() == 0) {
            return arr;
        }
        infoToArray_rec((AVLNode) this.getRoot(), 0, arr);
        return arr;
    }

    private int infoToArray_rec(AVLNode node, int i, String[] arr) {
        AVLNode right_child = (AVLNode)node.getRight();
        AVLNode left_child = (AVLNode)node.getLeft();
        if (node.getHeight()==0) {
            if (!node.isNull()){
                arr[i] = node.getValue();}
            i++;
            return i;
        }
        if (left_child.isNull() != true) {
            i = infoToArray_rec((AVLNode) node.getLeft(), i, arr);
        }
        if (!node.isNull()){
            arr[i] = node.getValue();}
        i++;
        if (right_child.isNull() != true) {
            i = infoToArray_rec((AVLNode) node.getRight(), i, arr);
        }
        return i;
    }

    /**
     * public int size()
     * <p>
     * Returns the number of nodes in the tree.
     */

    public int size() {
        if (this.empty() == true) {
            return 0;
        }
        return this.root.getSize();
    }

    /**
     * public int getRoot()
     *
     * Returns the root AVL node, or null if the tree is empty
     *
     */

    public IAVLNode getRoot()
    {
        if (this.empty() == true) {
            return null;
        }
        return this.root;
    }

    /**
     * This function allows us to access the "real" root (if it's a sentinel), to be used in TreeList only
     */

    public AVLNode getInnerRoot() {
        return this.root;
    }

    /**
     * This function sets the root, to be used only in TreeList and insert
     */

    public void setRoot(IAVLNode x) {
        this.root = (AVLNode)x;
        x.setParent(new AVLNode(0, "", true));
        x.setRight(new AVLNode(0, "", true));
        x.setLeft(new AVLNode(0, "", true));
        ((AVLNode) x).setSize(1);
        x.setHeight(0);
        this.min = (AVLNode)x;
        this.max = (AVLNode)x;
    }

    /**
     * public interface IAVLNode
     * ! Do not delete or modify this - otherwise all tests will fail !
     */

    public interface IAVLNode{
        public int getKey(); // Returns node's key
        public String getValue(); // Returns node's value [info]
        public void setLeft(IAVLNode node); // Sets node's left child
        public IAVLNode getLeft(); // Returns left child (if there is no left child return null)
        public void setRight(IAVLNode node); // Sets node's right child
        public IAVLNode getRight(); // Returns node's right child (if there is no right child, it returns null)
        public void setParent(IAVLNode node); // Sets node's parent
        public IAVLNode getParent(); // Returns the node's parent (if there is no parent, it returns null)
        public void setHeight(int height); // Sets the height of the node
        public int getHeight(); // Returns the height of the node. If the node is a leaf, returns 0
    }

    /**
     * public class AVLNode
     *
     * This class implements IAVLNode interface.
     * Implements a node including a key, a value (info), some extra fields
     * and relevant pointers to other nodes.
     */

    public class AVLNode implements IAVLNode {
        private int key;
        private String value;
        private IAVLNode left;
        private IAVLNode right;
        private IAVLNode parent;
        private int height = 0;
        private int size = 1;
        private boolean isNull = false;

        public AVLNode(int key, String value, boolean isNull) {
            if (isNull == true) {
                this.isNull = true;
                this.key = Integer.MIN_VALUE;
            }
            else {
                this.key = key;
                this.value = value;
            }
        }

        public int getKey()
        {
            return this.key;
        }
        public String getValue()
        {
            return this.value;
        }
        public void setLeft(IAVLNode node)
        {
            this.left = node;
        }
        public IAVLNode getLeft()
        {
            return this.left;
        }
        public void setRight(IAVLNode node)
        {
            this.right = node;
        }
        public IAVLNode getRight()
        {
            return this.right;
        }
        public void setParent(IAVLNode node)
        {
            this.parent = node;
        }
        public IAVLNode getParent()
        {
            return this.parent;
        }
        public void setHeight(int height) {
            this.height = height;
        }
        public int getHeight() {
            if (isNull == true) {
                return -1;
            }
            return this.height;
        }
        public void setSize(int size) {
            this.size = size; } // Sets the node's size

        public int getSize() {
            if (isNull == true) {
                return 0;
            }
            return this.size; } // Returns the node's size. If node is a leaf, returns 1

        public boolean isNull() {
            return this.isNull;
        } // Returns true if this node is "empty" or false otherwise
    }
}
