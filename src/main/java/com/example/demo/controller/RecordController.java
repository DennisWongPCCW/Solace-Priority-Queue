package com.example.demo.controller;

import com.example.demo.entity.SentRequest;
import com.example.demo.service.RecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/")
public class RecordController {
    Logger logger = LoggerFactory.getLogger(ProducerController.class);

    @Autowired
    private RecordService recordService;

    @PostMapping("/internal/insertSentRequest")
    public void insertSent(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid SentRequest reqParam) {
        try {
            recordService.insertSent(request, response, reqParam);
        } catch (Exception e) {
            logger.error("insertSent - Exception", e);
        }
    }

    @PostMapping("/internal/getSentRequest")
    public SentRequest selectSent(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid SentRequest reqParam) throws Exception {

        return recordService.selectSent(request, response, reqParam);

    }

    @PostMapping("/internal/updateSentRequest")
    public void updateSent(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid SentRequest reqParam) throws Exception {
        try {
            recordService.updateSent(request, response, reqParam);
        } catch (Exception e) {
            logger.error("updateSent - Exception", e);
        }
    }
}
