package com.sunbird.entity.model;

import lombok.Getter;
import lombok.Setter;

import java.security.PublicKey;

@Getter
@Setter
public class KeyData {
    private String keyId;
    private PublicKey publicKey;

    public KeyData(String keyId, PublicKey publicKey) {
        this.keyId = keyId;
        this.publicKey = publicKey;
    }
}

