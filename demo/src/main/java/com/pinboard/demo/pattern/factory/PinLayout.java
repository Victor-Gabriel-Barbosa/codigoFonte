package com.pinboard.demo.pattern.factory;

/**
 * Interface de produto para o padrão Factory
 */
public interface PinLayout {
    String render();
    String getName();
    int getColumnsCount();
}