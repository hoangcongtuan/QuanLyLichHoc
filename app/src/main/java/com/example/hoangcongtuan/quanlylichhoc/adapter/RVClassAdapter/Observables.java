package com.example.hoangcongtuan.quanlylichhoc.adapter.RVClassAdapter;

/**
 * The interface for anything, which wanna be obeserved by RepositoryObserver
 */
public interface Observables {
    void registerObserver(RepositoryObserver repositoryObserver);
    void removeObserver(RepositoryObserver repositoryObserver);
    void notifyObservers();
}