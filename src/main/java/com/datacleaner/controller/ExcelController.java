package com.datacleaner.controller;

import com.datacleaner.model.PreviewResponse;
import com.datacleaner.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Controller
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/api/excel/preview")
    @ResponseBody
    public PreviewResponse preview(@RequestParam("file") MultipartFile file) {
        return excelService.preview(file);
    }

    @PostMapping("/api/excel/clean")
    public ResponseEntity<InputStreamResource> clean(

            @RequestParam("file") MultipartFile file,

            @RequestParam(defaultValue = "true")
            boolean properCase,

            @RequestParam(defaultValue = "true")
            boolean removeDuplicates,

            @RequestParam(defaultValue = "true")
            boolean removeEmptyRows,

            @RequestParam(defaultValue = "true")
            boolean removeEmptyColumns,

            @RequestParam(defaultValue = "true")
            boolean trimSpaces
    ) {

        ByteArrayInputStream stream = excelService.clean(
                file,
                properCase,
                removeDuplicates,
                removeEmptyRows,
                removeEmptyColumns,
                trimSpaces
        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=cleaned.xlsx"
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(stream));
    }
}