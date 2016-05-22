package ru.nsu.rayTracer.g13201.boyarintsev.controllers;

import ru.nsu.rayTracer.g13201.boyarintsev.views.GUI;


public class ApplicationStarter {
    public static void main(String[] args)
    {
        MainController mainController = new MainController();
        mainController.setGUI(new GUI(mainController));
    }
}
