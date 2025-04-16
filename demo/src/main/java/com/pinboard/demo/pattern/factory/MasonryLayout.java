package com.pinboard.demo.pattern.factory;

/**
 * Produto concreto para o padrão Factory - Layout em cascata/masonry
 */
public class MasonryLayout implements PinLayout {
    
    @Override
    public String render() {
        return "<div class='container'>" +
               "<div class='row'>" +
               "<div class='masonry-grid' data-masonry='{\"itemSelector\": \".masonry-item\", \"percentPosition\": true, \"gutter\": 10}'>" +
               "<!-- Aqui serão inseridos os pins em formato masonry -->" +
               "</div>" +
               "</div>" +
               "</div>";
    }
    
    @Override
    public String getName() {
        return "Cascata";
    }
    
    @Override
    public int getColumnsCount() {
        return 0; // Auto-ajustável
    }
}