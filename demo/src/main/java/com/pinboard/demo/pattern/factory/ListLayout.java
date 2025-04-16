package com.pinboard.demo.pattern.factory;

/**
 * Produto concreto para o padrão Factory - Layout em lista
 */
public class ListLayout implements PinLayout {
    
    @Override
    public String render() {
        return "<div class='container'>" +
               "<div class='row'>" +
               "<div class='pin-list'>" +
               "<!-- Aqui serão inseridos os pins em formato de lista -->" +
               "</div>" +
               "</div>" +
               "</div>";
    }
    
    @Override
    public String getName() {
        return "Lista";
    }
    
    @Override
    public int getColumnsCount() {
        return 1;
    }
}