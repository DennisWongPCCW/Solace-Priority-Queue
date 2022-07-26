package com.example.demo.service;

import com.example.demo.controller.ProducerController;
import com.example.demo.entity.SentRequest;
import com.example.demo.mapper.RecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class RecordService {
    Logger logger = LoggerFactory.getLogger(ProducerController.class);
    @Autowired
    protected RecordMapper recordMapper;

    public void insertSent(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid SentRequest reqParam) {

        String id = reqParam.getId();
        String payload = reqParam.getPayload();
        String status = reqParam.getStatus();
        String createdBy = reqParam.getCreatedBy();
        String remark = reqParam.getRemark();
        recordMapper.insertSentRequest(id, payload, status, createdBy, remark);
        logger.debug("===insertSent=== OK");
    }

    public SentRequest selectSent(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid SentRequest reqParam) throws Exception {
        SentRequest sentreq = recordMapper.getSentRequest();
        logger.debug("===selectSent=== OK", sentreq);

        return sentreq;
    }

    public void updateSent(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid SentRequest reqParam) {

        String id = reqParam.getId();
        String status = reqParam.getStatus();

        recordMapper.updateSentRequest(status, id);
        logger.debug("===updateSent=== OK");
    }

    public String generateID(String d) {
        Date date = new Date(System.currentTimeMillis());

        byte[] bytesOfMessage = new byte[0];
        byte[] theMD5digest = new byte[0];
        String s = "";
        String result = "";
        try {
            bytesOfMessage = d.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            theMD5digest = md.digest(date.toString().getBytes("UTF-8"));

            for (byte bb : theMD5digest) {
                s += (bb + " ");
            }
            for (int i = 0; i < theMD5digest.length; i++) {
                result += Integer.toHexString((0x000000ff & theMD5digest[i]) | 0xffffff00).substring(6);
            }

        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            logger.error("generateID - Exception", e);
            e.printStackTrace();
        }
        return result.substring(8, 24);

    }

}
