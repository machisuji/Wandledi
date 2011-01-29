package org.wandledi.wandlet;

import org.wandledi.Scroll;

public interface Response {
    /**The (xhtml) file to be rendered as a response through Wandlet.
     */
    public String getFile();
    
    /**The scroll describing the transformations to be applied to
     * to the response file.
     */
    public Scroll getScroll();
}
