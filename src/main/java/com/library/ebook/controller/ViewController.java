package com.library.ebook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping({
            "/",
            "/dashboard",
            "/ebooks",
            "/licenses",
            "/patrons",
            "/publishers",
            "/checkouts",
            "/holds",
            "/access-keys",
            "/contacts",
            "/products",
            "/accounts",
            "/purchase-orders",
            "/vendor-bills",
            "/vendor-payments",
            "/sales-orders",
            "/invoices",
            "/customer-payments",
            "/budgets",
            "/journals",
            "/reports"
    })
    public String forwardToDashboard() {
        return "forward:/index.html";
    }
}
