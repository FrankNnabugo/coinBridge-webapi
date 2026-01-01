package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.enums.LedgerDirection;
import com.example.paymentApi.wallets.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    public LedgerService(LedgerRepository ledgerRepository){
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional
    public void createDoubleEntryLedger(LedgerRequest request, Wallet wallet){
        Ledger credit = new Ledger();
        credit.setWallet(wallet);
        credit.setEntryType(request.getEntryType());
        credit.setAmount(request.getAmount());
        credit.setDirection(LedgerDirection.CREDIT);
        credit.setProvider(request.getProvider());
        credit.setAsset(request.getAsset());
        credit.setStatus(request.getStatus());
        credit.setReferenceId(request.getReferenceId());
        credit.setSourceAddress(request.getSourceAddress());
        credit.setDestinationAddress(request.getDestinationAddress());
        credit.setSourceCurrency(request.getSourceCurrency());
        credit.setDestinationCurrency(request.getDestinationCurrency());

        Ledger debit = new Ledger();
        debit.setWallet(wallet);
        debit.setEntryType(request.getEntryType());
        debit.setAmount(request.getAmount());
        debit.setDirection(LedgerDirection.DEBIT);
        debit.setProvider(request.getProvider());
        debit.setAsset(request.getAsset());
        debit.setStatus(request.getStatus());
        debit.setReferenceId(request.getReferenceId());
        debit.setSourceAddress(request.getSourceAddress());
        debit.setDestinationAddress(request.getDestinationAddress());
        debit.setSourceCurrency(request.getSourceCurrency());
        debit.setDestinationCurrency(request.getDestinationCurrency());

        ledgerRepository.saveAll(List.of(credit, debit));
    }

}
