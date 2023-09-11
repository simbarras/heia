//
// Created by Beat Wolf on 02.07.2021.
//

#ifndef CPPALGO_BINARYTREE_H
#define CPPALGO_BINARYTREE_H

#include <vector>
#include <memory>
#include <numeric>

#include "BinaryTreeNode.h"

template<typename ValueType, typename KeyType=ValueType>
class BinaryTree {

public:
    /**
     * Construct an empty BinaryTree
     */
    BinaryTree() {
    }

    BinaryTree(std::vector<ValueType> values, std::function<KeyType(const ValueType &)> key_function, bool sort = false) {
        if (sort && values.size() > 0) {
            std::sort(values.begin(), values.end(), [&key_function](const ValueType &a, const ValueType &b) {
                return key_function(a) < key_function(b);
            });
        }
        std::vector<KeyType> keys(values.size());
        std::transform(values.begin(), values.end(), keys.begin(), [&key_function](const ValueType &a) { return key_function(a); });

        root = constructOptimalTree(keys, values, 0, keys.size(), nullptr);
    }

    BinaryTree(const std::vector<ValueType> &values) {
        for (auto &value : values) {
            insert(value);
        }
    }

    BinaryTree(const BinaryTree &tree) {
        initFromTree(tree);
    }

    ~BinaryTree() {
        free();
    }

    BinaryTree &operator=(const BinaryTree &tree) {
        free();
        initFromTree(tree);
        return *this;
    }

    void setRoot(const KeyType &key, const ValueType &value){
        free();
        root = new BinaryTreeNode(key, value);
    }

    void setRoot(BinaryTreeNode<ValueType, KeyType> *newRoot){
        free();
        root = newRoot;
    }

    void insert(const ValueType &value) {
        insert(value, value);
    }

    void insert(const KeyType &key, const ValueType &value) {
        if (root == nullptr) {
            root = new BinaryTreeNode(key, value);
            return;
        }
        root->insert(key, value);
    }

    bool search(const KeyType &key) const {
        if (root == nullptr) {
            return false;
        }

        return search(root, key) != nullptr;
    }

    std::vector<ValueType> inRange(const KeyType &start, const KeyType &stop) const {
        std::vector<ValueType> values;

        if (root != nullptr) {
            root->inRange(values, start, stop);
        }

        return values;
    }

    bool isEmpty() const {
        return root == nullptr;
    }

    BinaryTreeNode<ValueType, KeyType> *getRoot() const {
        return root;
    }

    BinaryTreeNode<ValueType, KeyType> *getRootCopy() const {
        if (root != nullptr) {
            return new BinaryTreeNode<ValueType, KeyType>(*root);
        }
        return nullptr;
    }

    size_t size() const{
        return count(root);
    }

private:

    size_t count(const BinaryTreeNode<ValueType, KeyType> *node) const{
        if(node == nullptr){
            return 0;
        }

        return 1 + count(node->left) + count(node->right);
    }

    BinaryTreeNode<ValueType, KeyType> *
    constructOptimalTree(std::vector<KeyType> &keys, std::vector<ValueType> &values, int left, int right,
                         BinaryTreeNode<ValueType, KeyType> *parent) {
        if (left >= right) {
            return nullptr;
        }
        int mid = (left + right) / 2;

        BinaryTreeNode<ValueType, KeyType> *node = new BinaryTreeNode<ValueType, KeyType>(keys[mid], values[mid],
                                                                                          parent);
        node->left = constructOptimalTree(keys, values, left, mid, node);
        node->right = constructOptimalTree(keys, values, mid + 1, right, node);
        return node;
    }

    const BinaryTreeNode<ValueType, KeyType> *
    search(const BinaryTreeNode<ValueType, KeyType> *node, const KeyType &key) const {
        if (node == nullptr) {
            return nullptr;
        }

        if (node->value == key) {
            return node;
        } else if (node->value > key) {
            return search(node->left, key);
        } else {
            return search(node->right, key);
        }
    }

    void free() {
        if (root != nullptr) {
            delete root;
            root = nullptr;
        }
    }

    void initFromTree(const BinaryTree &tree) {
        if (tree.root != nullptr) {
            root = new BinaryTreeNode(*tree.root);
        }
    }

    BinaryTreeNode<ValueType, KeyType> *root = nullptr;
};

#endif //CPPALGO_BINARYTREE_H