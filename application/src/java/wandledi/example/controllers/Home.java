package wandledi.example.controllers;

import wandledi.java.Controller;

public class Home extends Controller {

    public void index() {
    
        getWriter().println("Hello world!");
    }
}
