package com.example.hoangcongtuan.quanlylichhoc.adapter.RVClassAdapter;

/**
 * The interface for anyone, who want to observe RVClassAdapter
 */
public interface RepositoryObserver {
    void onDataStateChange(boolean isEmpty);
}