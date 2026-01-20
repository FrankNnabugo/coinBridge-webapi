package com.example.paymentApi.worker.walletCreation;

import com.example.paymentApi.shared.enums.RetryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletRetryService {

    private final WalletRetryRepository walletRetryRepository;


    public void createRetryRecord(String userId){
        WalletRetryRecord record = new WalletRetryRecord();
        record.setUserId(userId);
        record.setRetryCount(0);
        record.setStatus(RetryStatus.PENDING);
        walletRetryRepository.save(record);
    }
}
