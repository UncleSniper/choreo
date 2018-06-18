package org.unclesniper.choreo;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;

public class UniqueList<ElementT> implements Set<ElementT> {

	public static class Node<ElementT> {

		public ElementT element;

		public Node<ElementT> previous;

		public Node<ElementT> next;

		public Node(ElementT element) {
			this.element = element;
		}

	}

	private class ULIterator implements Iterator<ElementT> {

		private Node<ElementT> node;

		private boolean removed;

		public ULIterator() {
			node = head;
		}

		public boolean hasNext() {
			return node != null;
		}

		public ElementT next() {
			if(node == null)
				throw new NoSuchElementException();
			ElementT element = node.element;
			node = node.next;
			removed = false;
			return element;
		}

		public void remove() {
			if(removed)
				throw new IllegalStateException();
			if(node == null) {
				if(tail == null)
					throw new IllegalStateException();
				elements.remove(tail.element);
				tail = tail.previous;
				if(tail == null)
					head = null;
			}
			else {
				Node<ElementT> kill = node.previous;
				if(kill == null)
					throw new IllegalStateException();
				elements.remove(kill.element);
				if(kill.previous != null)
					kill.previous.next = node;
				else
					head = node;
				node.previous = kill.previous;
			}
			removed = true;
		}

	}

	private static final Object[] OBJECT_ARRAY_TEMPLATE = new Object[0];

	private final Map<ElementT, Node<ElementT>> elements;

	private Node<ElementT> head;

	private Node<ElementT> tail;

	private boolean overwrite;

	public UniqueList() {
		this(null);
	}

	public UniqueList(Map<ElementT, Node<ElementT>> elements) {
		if(elements == null)
			this.elements = new HashMap<ElementT, Node<ElementT>>();
		else {
			this.elements = elements;
			elements.clear();
		}
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public boolean add(ElementT element) {
		Node<ElementT> node = elements.get(element);
		if(node == null) {
			node = new Node<ElementT>(element);
			elements.put(element, node);
			node.previous = tail;
			tail = node;
			if(head == null)
				head = node;
			return true;
		}
		if(overwrite) {
			node.element = element;
			elements.put(element, node);
		}
		return false;
	}

	public boolean addAll(Collection<? extends ElementT> collection) {
		boolean changed = false;
		for(ElementT element : collection)
			changed = add(element) || changed;
		return changed;
	}

	public void clear() {
		elements.clear();
		head = tail = null;
	}

	public boolean contains(Object object) {
		return elements.containsKey(object);
	}

	public boolean containsAll(Collection<?> collection) {
		for(Object element : collection) {
			if(!elements.containsKey(element))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if(!(other instanceof UniqueList))
			return false;
		UniqueList ul = (UniqueList)other;
		if(elements.size() != ul.elements.size())
			return false;
		Node mine = head, theirs = ul.head;
		while(mine != null) {
			if(mine.element == null) {
				if(theirs.element != null)
					return false;
			}
			else {
				if(theirs.element == null)
					return false;
				if(!mine.element.equals(theirs.element))
					return false;
			}
			mine = mine.next;
			theirs = theirs.next;
		}
		return true;
	}

	public int hashCode() {
		int code = 0;
		for(Node<ElementT> node = head; node != null; node = node.next)
			code = ((code << 7) | (code >>> 25)) ^ (node.element == null ? 0 : node.element.hashCode());
		return code;
	}

	public boolean isEmpty() {
		return head == null;
	}

	public Iterator<ElementT> iterator() {
		return new ULIterator();
	}

	public boolean remove(Object object) {
		Node<ElementT> node = elements.get(object);
		if(node == null)
			return false;
		elements.remove(object);
		removeNode(node);
		return true;
	}

	private void removeNode(Node<ElementT> node) {
		if(node.previous != null)
			node.previous.next = node.next;
		else
			head = node.next;
		if(node.next != null)
			node.next.previous = node.previous;
		else
			tail = node.previous;
	}

	public boolean removeAll(Collection<?> collection) {
		boolean changed = false;
		for(Object element : collection) {
			Node<ElementT> node = elements.get(element);
			if(node != null) {
				elements.remove(element);
				removeNode(node);
				changed = true;
			}
		}
		return changed;
	}

	public boolean retainAll(Collection<?> collection) {
		boolean changed = false;
		Iterator<Map.Entry<ElementT, Node<ElementT>>> it = elements.entrySet().iterator();
		while(it.hasNext()) {
			Node<ElementT> node = it.next().getValue();
			if(!collection.contains(node.element)) {
				it.remove();
				removeNode(node);
				changed = true;
			}
		}
		return changed;
	}

	public int size() {
		return elements.size();
	}

	public Object[] toArray() {
		return toArray(UniqueList.OBJECT_ARRAY_TEMPLATE);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] arrayTemplate) {
		T[] array = (T[])Array.newInstance(arrayTemplate.getClass().getComponentType(), elements.size());
		int index = -1;
		for(Node<ElementT> node = head; node != null; node = node.next)
			array[++index] = (T)node.element;
		return array;
	}

}
