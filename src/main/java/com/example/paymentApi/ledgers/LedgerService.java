package com.example.paymentApi.ledgers;

import com.example.paymentApi.shared.enums.*;
import com.example.paymentApi.wallets.Wallet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService implements LedgerEntry {

    private final LedgerRepository ledgerRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void postExternalInbound(LedgerRequest request) {

        Ledger credit = new Ledger();
        credit.setWallet(request.getDestinationWallet());
        credit.setEntryType(LedgerType.INBOUND_TRANSFER);
        credit.setAmount(request.getAmount());
        credit.setDirection(LedgerDirection.CREDIT);
        credit.setProvider(ProviderType.CIRCLE);
        credit.setAsset(AssetType.USDC);
        credit.setStatus(LedgerStatus.POSTED);
        credit.setReferenceId(request.getReferenceId());
        credit.setSourceAddress(request.getSourceAddress());
        credit.setDestinationAddress(request.getDestinationAddress());
        credit.setSourceCurrency(request.getSourceCurrency());
        credit.setDestinationCurrency(request.getDestinationCurrency());
        credit.setBalanceBefore(request.getBalanceBefore());
        credit.setBalanceAfter(request.getBalanceAfter());

        Ledger debit = new Ledger();
        debit.setWallet(request.getSourceWallet());
        debit.setEntryType(LedgerType.EXTERNAL_CLEARING);
        debit.setAmount(request.getAmount());
        debit.setDirection(LedgerDirection.DEBIT);
        debit.setProvider(ProviderType.CIRCLE);
        debit.setAsset(AssetType.USDC);
        debit.setStatus(LedgerStatus.POSTED);
        debit.setReferenceId(request.getReferenceId());
        debit.setSourceAddress(request.getSourceAddress());
        debit.setDestinationAddress(request.getDestinationAddress());
        debit.setSourceCurrency(request.getSourceCurrency());
        debit.setDestinationCurrency(request.getDestinationCurrency());
        debit.setBalanceBefore(request.getBalanceBefore());
        debit.setBalanceAfter(request.getBalanceAfter());

        ledgerRepository.saveAll(List.of(credit, debit));

    }


    @Transactional
    @Override
    public void postInternalInbound(LedgerRequest request) {

        Wallet source = request.getSourceWallet();
        Wallet destination = request.getDestinationWallet();

        BigDecimal sourceBalanceBefore = source.getAvailableBalance().add(source.getReservedBalance());
        BigDecimal sourceBalanceAfter = sourceBalanceBefore.subtract(request.getAmount());

        Ledger sourceDebit = new Ledger();
        sourceDebit.setWallet(source);
        sourceDebit.setEntryType(LedgerType.OUTBOUND_TRANSFER);
        sourceDebit.setAmount(request.getAmount());
        sourceDebit.setDirection(LedgerDirection.DEBIT);
        sourceDebit.setProvider(ProviderType.CIRCLE);
        sourceDebit.setAsset(AssetType.USDC);
        sourceDebit.setStatus(LedgerStatus.POSTED);
        sourceDebit.setReferenceId(request.getReferenceId());
        sourceDebit.setSourceAddress(request.getSourceAddress());
        sourceDebit.setDestinationAddress(request.getDestinationAddress());
        sourceDebit.setSourceCurrency(request.getSourceCurrency());
        sourceDebit.setDestinationCurrency(request.getDestinationCurrency());
        sourceDebit.setBalanceBefore(sourceBalanceBefore);
        sourceDebit.setBalanceAfter(sourceBalanceAfter);

        Ledger sourceContra = new Ledger();
        sourceContra.setWallet(source);
        sourceContra.setEntryType(LedgerType.INTERNAL_CLEARING);
        sourceContra.setAmount(request.getAmount());
        sourceContra.setDirection(LedgerDirection.CREDIT);
        sourceContra.setProvider(ProviderType.CIRCLE);
        sourceContra.setAsset(AssetType.USDC);
        sourceContra.setStatus(LedgerStatus.POSTED);
        sourceContra.setReferenceId(request.getReferenceId());
        sourceContra.setSourceAddress(request.getSourceAddress());
        sourceContra.setDestinationAddress(request.getDestinationAddress());
        sourceContra.setSourceCurrency(request.getSourceCurrency());
        sourceContra.setDestinationCurrency(request.getDestinationCurrency());
        sourceContra.setBalanceBefore(sourceBalanceAfter);
        sourceContra.setBalanceAfter(sourceBalanceAfter);


        BigDecimal destinationBalanceBefore = destination.getAvailableBalance();

        BigDecimal destinationBalanceAfter = destinationBalanceBefore.add(request.getAmount());

        Ledger destinationCredit = new Ledger();
        destinationCredit.setWallet(destination);
        destinationCredit.setEntryType(LedgerType.INBOUND_TRANSFER);
        destinationCredit.setAmount(request.getAmount());
        destinationCredit.setDirection(LedgerDirection.CREDIT);
        destinationCredit.setProvider(ProviderType.CIRCLE);
        destinationCredit.setAsset(AssetType.USDC);
        destinationCredit.setStatus(LedgerStatus.POSTED);
        destinationCredit.setReferenceId(request.getReferenceId());
        destinationCredit.setSourceAddress(request.getSourceAddress());
        destinationCredit.setDestinationAddress(request.getDestinationAddress());
        destinationCredit.setSourceCurrency(request.getSourceCurrency());
        destinationCredit.setDestinationCurrency(request.getDestinationCurrency());
        destinationCredit.setBalanceBefore(destinationBalanceBefore);
        destinationCredit.setBalanceAfter(destinationBalanceAfter);


        Ledger destinationContra = new Ledger();
        destinationContra.setWallet(destination);
        destinationContra.setEntryType(LedgerType.INTERNAL_CLEARING);
        destinationContra.setAmount(request.getAmount());
        destinationContra.setDirection(LedgerDirection.DEBIT);
        destinationContra.setProvider(ProviderType.CIRCLE);
        destinationContra.setAsset(AssetType.USDC);
        destinationContra.setStatus(LedgerStatus.POSTED);
        destinationContra.setReferenceId(request.getReferenceId());
        destinationContra.setSourceAddress(request.getSourceAddress());
        destinationContra.setDestinationAddress(request.getDestinationAddress());
        destinationContra.setSourceCurrency(request.getSourceCurrency());
        destinationContra.setDestinationCurrency(request.getDestinationCurrency());
        destinationContra.setBalanceBefore(destinationBalanceAfter);
        destinationContra.setBalanceAfter(destinationBalanceAfter);

        ledgerRepository.saveAll(
                List.of(
                        sourceDebit,
                        sourceContra,
                        destinationCredit,
                        destinationContra
                )
        );
    }

    @Transactional
    @Override
    public void postInternalReversal(LedgerRequest request) {


        Ledger sourceDebit = new Ledger();
        sourceDebit.setWallet(request.getSourceWallet());
        sourceDebit.setEntryType(LedgerType.OUTBOUND_TRANSFER);
        sourceDebit.setAmount(request.getAmount());
        sourceDebit.setDirection(LedgerDirection.DEBIT);
        sourceDebit.setProvider(ProviderType.CIRCLE);
        sourceDebit.setAsset(AssetType.USDC);
        sourceDebit.setStatus(LedgerStatus.POSTED);
        sourceDebit.setReferenceId(request.getReferenceId());
        sourceDebit.setSourceAddress(request.getSourceAddress());
        sourceDebit.setDestinationAddress(request.getDestinationAddress());
        sourceDebit.setSourceCurrency(request.getSourceCurrency());
        sourceDebit.setDestinationCurrency(request.getDestinationCurrency());
        sourceDebit.setBalanceBefore(request.getBalanceBefore());
        sourceDebit.setBalanceAfter(request.getBalanceAfter());

        Ledger sourceReversal = new Ledger();
        sourceReversal.setWallet(request.getSourceWallet());
        sourceReversal.setEntryType(LedgerType.REVERSAL);
        sourceReversal.setAmount(request.getAmount());
        sourceReversal.setDirection(LedgerDirection.CREDIT);
        sourceReversal.setProvider(ProviderType.CIRCLE);
        sourceReversal.setAsset(AssetType.USDC);
        sourceReversal.setStatus(LedgerStatus.POSTED);
        sourceReversal.setReferenceId(request.getReferenceId());
        sourceReversal.setSourceAddress(request.getSourceAddress());
        sourceReversal.setDestinationAddress(request.getDestinationAddress());
        sourceReversal.setSourceCurrency(request.getSourceCurrency());
        sourceReversal.setDestinationCurrency(request.getDestinationCurrency());
        sourceReversal.setBalanceBefore(request.getBalanceBefore());
        sourceReversal.setBalanceAfter(request.getBalanceAfter());

        ledgerRepository.saveAll(List.of(sourceDebit, sourceReversal));
    }

}

