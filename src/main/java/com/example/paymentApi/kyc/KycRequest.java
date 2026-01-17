package com.example.paymentApi.kyc;

import com.example.paymentApi.shared.enums.KycDocType;

public class KycRequest {
    private String bvn;

    private KycDocType docType;

    private String docNumber;

    private String docPhotoFrontUrl;

    private String docPhotoBackUrl;
}
