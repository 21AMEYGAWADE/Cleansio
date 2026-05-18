package com.datacleaner.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @PostMapping("/ask")
    public String askAI(@RequestBody Map<String, String> body) {

        String question = body.get("question");

        if(question == null || question.isEmpty()) {
            return "Please ask a valid question.";
        }

        question = question.toLowerCase();

        // DUPLICATES
        if (question.contains("duplicate")) {
            return "Duplicate rows are repeated rows with identical values. Removing duplicates improves dataset quality.";
        }

        // QUALITY
        if (question.contains("quality")) {
            return "Quality score is calculated based on duplicates, empty rows, invalid values and formatting issues.";
        }

        // EMPTY ROWS/COLUMNS
        if (question.contains("empty")) {
            return "Empty rows and columns can be automatically removed during cleaning.";
        }

        // CSV
        if (question.contains("csv")) {
            return "CSV files are comma-separated files used for storing tabular data.";
        }

        // EXCEL
        if (question.contains("excel")) {
            return "Excel files support sheets, formulas, charts and advanced formatting.";
        }

        // JSON
        if (question.contains("json")) {
            return "JSON stores data in key-value format and is widely used in APIs.";
        }

        // XML
        if (question.contains("xml")) {
            return "XML is a markup language used for storing and transporting structured data.";
        }

        // CLEANING
        if (question.contains("clean")) {
            return "Data cleaning removes duplicates, trims spaces, fixes formatting and improves consistency.";
        }

        // TRIM SPACES
        if (question.contains("trim")) {
            return "Trim spaces removes unnecessary spaces before and after text values.";
        }

        // PROPER CASE
        if (question.contains("proper case")) {
            return "Proper case converts text like 'john doe' into 'John Doe'.";
        }

        // ANALYTICS
        if (question.contains("analytics")) {
            return "Analytics helps visualize dataset trends using charts and summaries.";
        }

        // BAR CHART
        if (question.contains("bar chart")) {
            return "Bar charts compare category values using rectangular bars.";
        }

        // PIE CHART
        if (question.contains("pie chart")) {
            return "Pie charts show percentage distribution of categories.";
        }

        // ROWS
        if (question.contains("row")) {
            return "Rows represent individual records in a dataset.";
        }

        // COLUMNS
        if (question.contains("column")) {
            return "Columns represent attributes or fields in a dataset.";
        }

        // NULL VALUES
        if (question.contains("null")) {
            return "Null values indicate missing or unavailable data.";
        }

        // DATABASE
        if (question.contains("database")) {
            return "The application stores upload history and user data in MySQL database.";
        }

        // MYSQL
        if (question.contains("mysql")) {
            return "MySQL is a relational database management system used for storing structured data.";
        }

        // SPRING BOOT
        if (question.contains("spring")) {
            return "Spring Boot simplifies Java backend development with embedded servers and dependency management.";
        }

        // THYMELEAF
        if (question.contains("thymeleaf")) {
            return "Thymeleaf is a Java template engine used for dynamic HTML rendering.";
        }

        // SECURITY
        if (question.contains("security")) {
            return "Spring Security protects login, authentication and application routes.";
        }

        // LOGIN
        if (question.contains("login")) {
            return "Users can securely login using username and encrypted passwords.";
        }

        // HISTORY
        if (question.contains("history")) {
            return "Upload history stores previously cleaned files with quality scores and upload time.";
        }

        // FILE TYPES
        if (question.contains("supported")) {
            return "Supported file formats include Excel, CSV, JSON and XML.";
        }

        // DOWNLOAD
        if (question.contains("download")) {
            return "You can download the cleaned dataset after processing.";
        }

        // AI
        if (question.contains("ai")) {
            return "AI Assistant helps explain dataset concepts and cleaning operations.";
        }

        // HELP
        if (question.contains("help")) {
            return "You can ask about cleaning, analytics, charts, datasets, formats or quality score.";
        }

        // DEFAULT
        return "AI Assistant: I can help with dataset cleaning, analytics, file formats, charts, quality score and database operations.";
    }
}