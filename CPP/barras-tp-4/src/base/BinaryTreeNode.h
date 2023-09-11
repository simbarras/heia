//
// Created by "Beat Wolf" on 01.05.2022.
//

#ifndef CPPALGO_BINARYTREENODE_H
#define CPPALGO_BINARYTREENODE_H

template<typename ValueType, typename KeyType>
class BinaryTreeNode {

public:
    /**
     * Construct BinaryTreeNode using a key and value. Optionally a parent can be specified
     * @param key
     * @param value
     * @param parent
     */
    BinaryTreeNode(const KeyType &key, const ValueType &value, BinaryTreeNode *parent = nullptr) : key(key),
                                                                                                   value(value),
                                                                                                   parent(parent) {
    }

    /**
     * Copy constructor
     * @param node
     */
    BinaryTreeNode(const BinaryTreeNode &node) : key(node.key), value(node.value) {
        initFromNode(node);
    }

    /**
     * Assignment operator
     * @param node
     * @return
     */
    BinaryTreeNode &operator=(const BinaryTreeNode &node) {
        if (this == &node) { //Handle self assignment
            return *this;
        }
        free();
        initFromNode(node);
        key = node.key;
        value = node.value;
    }

    /**
     * Destructor
     */
    ~BinaryTreeNode() {
        free();
    }

    /**
     * Insert a value using a given key
     * @param key
     * @param value
     */
    void insert(const KeyType &key, const ValueType &value) {
        if (key < this->key) {
            if (left == nullptr) {
                left = new BinaryTreeNode(key, value, this);
            } else {
                left->insert(key, value);
            }
        } else {
            if (right == nullptr) {
                right = new BinaryTreeNode(key, value, this);
            } else {
                right->insert(key, value);
            }
        }
    }

    /**
     * Used to query all values having their key between [start and stop].
     * @param values
     * @param start
     * @param stop
     */
    void inRange(std::vector<ValueType> &values, KeyType start, KeyType stop) const {
        if (left != nullptr && key > stop) {
            left->inRange(values, start, stop);
        }

        if (key >= start && key <= stop) {
            if (left != nullptr) {
                left->inRange(values, start, stop);
            }
            values.push_back(value);
            if (right != nullptr) {
                right->inRange(values, start, stop);
            }
        }

        if (right != nullptr && key < start) {
            right->inRange(values, start, stop);
        }
    }
private:
    /**
     * This method initializes the object using another BinaryTreeNode
     * @param node
     */
    void initFromNode(const BinaryTreeNode &node) {
        if (node.left != nullptr) {
            left = new BinaryTreeNode(*node.left);
            left->parent = this;
        }
        if (node.right != nullptr) {
            right = new BinaryTreeNode(*node.right);
            right->parent = this;
        }
    }

    /**
     * Free all resources used in this object
     */
    void free() {
        if (left != nullptr) {
            delete left;
            left = nullptr;
        }
        if (right != nullptr) {
            delete right;
            right = nullptr;
        }
    }

public: //TODO: Make this private
    BinaryTreeNode *left = nullptr;
    BinaryTreeNode *right = nullptr;
    BinaryTreeNode *parent = nullptr;

    KeyType key;
    ValueType value;

};

#endif //CPPALGO_BINARYTREENODE_H
