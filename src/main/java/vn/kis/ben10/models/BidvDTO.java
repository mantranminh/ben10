package vn.kis.ben10.models;

import lombok.Data;

@Data
public class BidvDTO<H, B> {
    private BidvMsgDTO<H, B> msg;
}
