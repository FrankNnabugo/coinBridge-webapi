package com.example.paymentApi.worker.paymentInitiation;

import com.example.paymentApi.shared.enums.RetryStatus;
import com.example.paymentApi.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OutboundRetryService {

    private final OutboundRetryRepository outboundRetryRepository;
    private final UserRepository userRepository;

    public void createPaymentRetryRecord(String userId){
        OutboundRetryRecord record = new OutboundRetryRecord();
        record.setUserId(userId);
        record.setStatus(RetryStatus.PENDING);
        record.setRetryCount(0);
        outboundRetryRepository.save(record);
    }
}
