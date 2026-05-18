package com.datacleaner.controller;

import com.datacleaner.repository.UploadHistoryRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HistoryController {

    @Autowired
    private UploadHistoryRepository repository;

    @GetMapping("/history")
    public String history(Model model) {

        model.addAttribute(
                "historyList",
                repository.findAll()
        );

        return "history";
    }
}