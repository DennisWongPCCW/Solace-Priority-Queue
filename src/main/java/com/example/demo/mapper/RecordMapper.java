package com.example.demo.mapper;

import com.example.demo.entity.SentRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface RecordMapper {

    void insertSentRequest(
            @Param("id") String id,
            @Param("payload") String payload,
            @Param("status") String status,
            @Param("createdBy") String createdBy,
            @Param("remark") String remark
    );

    SentRequest getSentRequest();

    void updateSentRequest(
            @Param("status") String status,
            @Param("id") String id
    );
}
