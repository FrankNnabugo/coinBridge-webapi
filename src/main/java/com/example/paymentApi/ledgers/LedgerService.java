package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.transaction.TransactionRepository;
import com.example.paymentApi.wallets.Wallet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void postExternalInbound(
                                    BigDecimal amounts,
                                    String transactionId,
                                    Account accountId) {

        Ledger internal = new Ledger();
        internal.setAccount(accountId);
        internal.setTransaction(transactionRepository.findByProviderTransactionId(transactionId));
        internal.setEntryType(EntryType.CREDIT);
        internal.setAmount(amounts);
        internal.setAsset(AssetType.USDC);
        internal.setStatus(LedgerStatus.POSTED);

        Ledger settlement = new Ledger();
        settlement.setAccount(accountRepository.findByAccountType(AccountType.SETTLEMENT));
        settlement.setTransaction(transactionRepository.findByProviderTransactionId(transactionId));
        settlement.setEntryType(EntryType.DEBIT);
        settlement.setAmount(amounts);
        settlement.setAsset(AssetType.USDC);
        settlement.setStatus(LedgerStatus.POSTED);

        ledgerRepository.saveAll(List.of(internal, settlement));

    }


    @Transactional
    public void postInternalInbound(BigDecimal amounts,
                                    String transactionId,
                                    Account sourceAccount,
                                    Account destinationAccount) {

        Ledger sourceDebit = new Ledger();
        sourceDebit.setAccount(sourceAccount);
        sourceDebit.setTransaction(transactionRepository.findByProviderTransactionId(transactionId));
        sourceDebit.setEntryType(EntryType.DEBIT);
        sourceDebit.setAmount(amounts);
        sourceDebit.setAsset(AssetType.USDC);
        sourceDebit.setStatus(LedgerStatus.POSTED);

        Ledger destinationCredit = new Ledger();
        destinationCredit.setAccount(destinationAccount);
        destinationCredit.setTransaction(transactionRepository.findByProviderTransactionId(transactionId));
        destinationCredit.setEntryType(EntryType.CREDIT);
        destinationCredit.setAmount(amounts);
        destinationCredit.setAsset(AssetType.USDC);
        destinationCredit.setStatus(LedgerStatus.POSTED);

        ledgerRepository.saveAll(
                List.of(
                        sourceDebit, destinationCredit
                )
        );
    }


    @Transactional
    public void postInternalReversal(BigDecimal amounts,
                                     Account accountId,
                                     String providerTransactionId) {

        Ledger sourceDebit = new Ledger();
        sourceDebit.setAccount(accountId);
        sourceDebit.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        sourceDebit.setEntryType(EntryType.DEBIT);
        sourceDebit.setAmount(amounts);
        sourceDebit.setAsset(AssetType.USDC);
        sourceDebit.setStatus(LedgerStatus.POSTED);

        Ledger settlementCredit = new Ledger();
        settlementCredit.setAccount(accountRepository.findByAccountType(AccountType.SETTLEMENT));
        settlementCredit.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        settlementCredit.setEntryType(EntryType.CREDIT);
        settlementCredit.setAmount(amounts);
        settlementCredit.setAsset(AssetType.USDC);
        settlementCredit.setStatus(LedgerStatus.POSTED);

        Ledger sourceCredit = new Ledger();
        sourceCredit.setAccount(accountRepository.findByAccountType(AccountType.SETTLEMENT));
        sourceCredit.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        sourceCredit.setEntryType(EntryType.REVERSAL);
        sourceCredit.setAmount(amounts);
        sourceCredit.setAsset(AssetType.USDC);
        sourceCredit.setStatus(LedgerStatus.POSTED);

        Ledger settlementDebit = new Ledger();
        settlementDebit.setAccount(accountRepository.findByAccountType(AccountType.SETTLEMENT));
        settlementDebit.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        settlementDebit.setEntryType(EntryType.DEBIT);
        settlementDebit.setAmount(amounts);
        settlementDebit.setAsset(AssetType.USDC);
        settlementDebit.setStatus(LedgerStatus.POSTED);

        ledgerRepository.saveAll(List.of(sourceDebit,
                settlementCredit,
                sourceCredit,
                settlementDebit));
    }

    public void postInternalToExternal(BigDecimal amounts,
                                       Account accountId,
                                       String providerTransactionId){

        Ledger sourceDebit = new Ledger();
        sourceDebit.setAccount(accountId);
        sourceDebit.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        sourceDebit.setEntryType(EntryType.DEBIT);
        sourceDebit.setAmount(amounts);
        sourceDebit.setAsset(AssetType.USDC);
        sourceDebit.setStatus(LedgerStatus.POSTED);

        Ledger settlement = new Ledger();
        settlement.setAccount(accountRepository.findByAccountType(AccountType.SETTLEMENT));
        settlement.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        settlement.setEntryType(EntryType.CREDIT);
        settlement.setAmount(amounts);
        settlement.setAsset(AssetType.USDC);
        settlement.setStatus(LedgerStatus.POSTED);

        ledgerRepository.saveAll(List.of(sourceDebit, settlement));

    }

    public void postInternalToExternalFailure(BigDecimal amounts,
                                              Account accountId,
                                              String providerTransactionId){
        Ledger sourceDebit = new Ledger();
        sourceDebit.setAccount(accountId);
        sourceDebit.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        sourceDebit.setEntryType(EntryType.DEBIT);
        sourceDebit.setAmount(amounts);
        sourceDebit.setAsset(AssetType.USDC);
        sourceDebit.setStatus(LedgerStatus.POSTED);

        Ledger settlementCredit = new Ledger();
        settlementCredit.setAccount(accountRepository.findByAccountType(AccountType.SETTLEMENT));
        settlementCredit.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        settlementCredit.setEntryType(EntryType.CREDIT);
        settlementCredit.setAmount(amounts);
        settlementCredit.setAsset(AssetType.USDC);
        settlementCredit.setStatus(LedgerStatus.POSTED);

        Ledger sourceCredit = new Ledger();
        sourceCredit.setAccount(accountRepository.findByAccountType(AccountType.SETTLEMENT));
        sourceCredit.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        sourceCredit.setEntryType(EntryType.REVERSAL);
        sourceCredit.setAmount(amounts);
        sourceCredit.setAsset(AssetType.USDC);
        sourceCredit.setStatus(LedgerStatus.POSTED);

        Ledger settlementDebit = new Ledger();
        settlementDebit.setAccount(accountRepository.findByAccountType(AccountType.SETTLEMENT));
        settlementDebit.setTransaction(transactionRepository.findByProviderTransactionId(providerTransactionId));
        settlementDebit.setEntryType(EntryType.DEBIT);
        settlementDebit.setAmount(amounts);
        settlementDebit.setAsset(AssetType.USDC);
        settlementDebit.setStatus(LedgerStatus.POSTED);

        ledgerRepository.saveAll(List.of(sourceDebit,
                settlementCredit,
                sourceCredit,
                settlementDebit));
    }
}

