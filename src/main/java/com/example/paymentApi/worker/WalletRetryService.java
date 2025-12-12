package com.example.paymentApi.worker;

import com.example.paymentApi.shared.enums.RetryStatus;
import org.springframework.stereotype.Service;

@Service
public class WalletRetryService {

    private final WalletRetryRepository walletRetryRepository;

    public WalletRetryService(WalletRetryRepository walletRetryRepository){
        this.walletRetryRepository = walletRetryRepository;
    }

    public void createRetryRecord(String userId){
        WalletRetryRecord record = new WalletRetryRecord();
        record.setUserId(userId);
        record.setRetryCount(0);
        record.setStatus(RetryStatus.PENDING);
        walletRetryRepository.save(record);
    }
}
