package com.chaos.library.controller;

import com.chaos.library.common.result.Result;
import com.chaos.library.common.result.Results;
import com.chaos.library.service.ConsultantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    @Autowired
    private ConsultantService consultantService;
    @PostMapping
    public Result<String> chat(@RequestBody Map<String, String> body,
                    HttpServletRequest request) {

        String message = body.get("message");

        String response = consultantService.chat(message);
        return Results.success(response);
    }
}