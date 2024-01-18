package vn.kis.ben10.models;

import lombok.Data;

import java.util.List;

@Data
public class TtlGetOperatorTokenResDTO {
    private String errorCode;
    private String errorMessage;
    private String tokenID;
    private String operatorID;
    private String sessionBO;
    private String sessionFO;
    private String channelID;
    private String ita;
    private List<String> listService;
}