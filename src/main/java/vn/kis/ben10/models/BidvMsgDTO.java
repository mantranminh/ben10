package vn.kis.ben10.models;

import lombok.Data;

@Data
public class BidvMsgDTO<H, B> {
    private H header;
    private B body;
}
