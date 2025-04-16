package com.pinboard.demo.pattern.factory;

/**
 * Produto concreto para o padrão Factory - Layout em grade
 */
public class GridLayout implements PinLayout {
    
    @Override
    public String render() {
        return "<div class='container'>" +
               "<div class='row'>" +
               "<div class='pin-grid' data-masonry='{\"itemSelector\": \".pin-item\", \"columnWidth\": 200}'>" +
               "<!-- Aqui serão inseridos os pins -->" +
               "</div>" +
               "</div>" +
               "</div>";
    }
    
    @Override
    public String getName() {
        return "Grade";
    }
    
    @Override
    public int getColumnsCount() {
        return 4;
    }
}