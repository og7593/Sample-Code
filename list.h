#include <iostream>

template <typename T>
class list {

 public:

    list();                                   // constructor
    list(const list &l);                      // copy constructor
    list &operator=(const list &l);           // assignment operator
    ~list();                                  // destructor

    // Returns number of elements in the list
    unsigned size();

    // Returns true if the list is empty, false otherwise.
    bool isEmpty() const;

    // Inserts element to front of list
    void insertFront(const T &val);
    
    // Inserts element to the end of list
    void insertBack(const T &val);

    // Returns the values of the front element in the list
    T front();

    // Returns the value of the back element of the list.
    T back();

    // Deletes the front element of the list and returns its value
    void removeFront();

    // Deletes the back element of the list and returns its value
    void removeBack();

    // Prints each element of the list in order
    void printForward();

    // Prints each element of the list in reverse order
    void printReverse();

 private:
    struct node {
      node   *next;
      node   *prev;
      T      value;
    };

    node   *first; // The pointer to the first node
    node   *last;  // The pointer to the last node
    unsigned length; // holds number of elements in the list

    // Initializes empty list
    void createEmpty();
    
    // Removes all of the elements in the list
    void removeAll();

    // Makes copy of all of the elements in the list
    void copyAll(const list &l);

};

template <typename T>
void list<T>::createEmpty() {
  first = nullptr;
  last = nullptr;
  length = 0;
}

template <typename T>
void  list<T>::removeAll() {
  while (!isEmpty()) {
    removeFront();
  }
}

template <typename T>
void list<T>::copyAll(const list &l) {
  node *iterator = l.first;
  while (iterator) {
    T obj = iterator->value;
    insertBack(obj);
    iterator = iterator->next;
  }
}

template <typename T>
unsigned list<T>::size() {
  return length;
}

template <typename T>
bool list<T>::isEmpty() const{
  return (first == nullptr && last == nullptr);
}

template <typename T>
void list<T>::insertFront(const T &val) {
  length++;
  node *newNode = new node;
  new (&(newNode->value)) T(val);
  newNode->next = first;
  newNode->prev = nullptr;
  if (isEmpty()) first = last = newNode;
  else {
    first->prev = newNode;
    first = newNode;
  }
}

template <typename T>
void list<T>::insertBack(const T &val) {
  length++;
  node *newNode = new node;
  new (&(newNode->value)) T(val);
  newNode->next = nullptr;
  newNode->prev = last;
  if (isEmpty()) first = last = newNode;
  else {
    last->next = newNode;
    last = newNode;
  }
}

template <typename T>
T list<T>::front() {
  return first->value;
}

template <typename T>
T list<T>::back() {
  return last->value;
}

template <typename T>
void list<T>::removeFront() {
  if (isEmpty()) return;
  length--;
  node *removedNode = first;
  first = first->next;
  if (first) first->prev = nullptr;
  else first = last = nullptr;
  delete removedNode;
  return;
}

template <typename T>
void list<T>::removeBack() {
  if (isEmpty()) return;
  length--;
  node *removedNode = last;
  last = last->prev;
  if (last) last->next = nullptr;
  else first = last = nullptr;
  delete removedNode;
  return;
}

template <typename T>
list<T>::list() {
  createEmpty();
}

template <typename T>
list<T>::list(const list &l) {
  createEmpty();
  copyAll(l);
  return;
}

template <typename T>
list<T>& list<T>::operator= (const list &l)
{
    if (this != &l) {
        removeAll();
        copyAll(l);
    }
    return *this;
}

template <typename T>
list<T>::~list() {
  removeAll();
}

template <typename T>
void list<T>::printForward() {
  if (isEmpty()) {
    std::cout << "List is empty\n"; 
    return;
  }
  node *head = first;
  while (head) {
    std::cout << head->value << " ";
    head = head->next;
  }
  std::cout << "\n";
}

template <typename T>
void list<T>::printReverse() {
  if (isEmpty()) {
    std::cout << "List is empty\n"; 
    return;
  }
  node *tail = last;
  while (tail) {
    std::cout << tail->value << " ";
    tail = tail->prev;
  }
  std::cout << "\n";
}
