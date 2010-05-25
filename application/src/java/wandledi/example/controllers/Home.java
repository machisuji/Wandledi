package wandledi.example.controllers;

import java.text.DateFormat;
import java.util.Date;
import wandledi.java.Controller;

public class Home extends Controller {

    public void index() {

        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        model.put("time", time);
    }
}
