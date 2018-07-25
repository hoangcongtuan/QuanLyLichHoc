package com.example.hoangcongtuan.quanlylichhoc.ObserverPattern;

public interface Observables {
    void registerObserver(RepositoryObserver repositoryObserver);
    void removeObserver(RepositoryObserver repositoryObserver);
    void notifyObservers();
}
