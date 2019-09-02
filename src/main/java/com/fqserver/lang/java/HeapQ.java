package com.fqserver.lang.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

// 固定容量的优先队列，模拟大顶堆，用于解决求topN小的问题
public class HeapQ<E extends Comparable<E>> {
    private PriorityQueue<E> queue;
    private int maxSize; // 堆的最大容量

    public HeapQ(int maxSize) {
        this(maxSize, new Comparator<E>() {
            public int compare(E o1, E o2) {
                // 生成最大堆使用o2-o1,生成最小堆使用o1-o2, 并修改 e.compareTo(peek) 比较规则
                return (o1.compareTo(o2));
            }
        });
    }

    public HeapQ(int maxSize, Comparator<E> comparator) {
        if (maxSize <= 0)
            throw new IllegalArgumentException();
        this.maxSize = maxSize;
        this.queue = new PriorityQueue<E>(maxSize, comparator);
    }

    @SuppressWarnings("unchecked")
    public void pushAll(E... all) {
        for (E e : all) {
            push(e);
        }
    }

    public void pushAll(Collection<? extends E> all) {
        for (E e : all) {
            push(e);
        }
    }

    public void push(E e) {
        if (getQueue().size() < maxSize) { // 未达到最大容量，直接添加
            getQueue().add(e);
        } else { // 队列已满
            E peek = getQueue().peek();
            if (e.compareTo(peek) < 0) { // 将新元素与当前堆顶元素比较，保留较小的元素
                getQueue().poll();
                getQueue().add(e);
            }
        }
    }

    public E pop() {
        return getQueue().poll();
    }

    public List<E> sortedList() {
        List<E> list = new ArrayList<E>(getQueue());
        Collections.sort(list); // PriorityQueue本身的遍历是无序的，最终需要对队列中的元素进行排序
        return list;
    }

    public PriorityQueue<E> getQueue() {
        return queue;
    }

}