package com.example.paymentApi.transaction;

import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.wallets.Wallet;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class TransactionService{

    private final TransactionRepository transactionRepository;
    private ModelMapper modelMapper;

    public TransactionService(TransactionRepository transactionRepository,
                             ModelMapper modelMapper){
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

    public void createTransactionRecord(TransactionRequest request, Wallet wallet){
        Transactions transaction = new Transactions();
        transaction.setWallet(wallet);
        transaction.setUser(wallet.getUser());
        transaction.setTransferId(request.getTransferId());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmounts());
        transaction.setStatus(request.getStatus());
        transaction.setReferenceId(request.getReferenceId());
        transaction.setSourceAddress(request.getSourceAddress());
        transaction.setDestinationAddress(request.getDestinationAddress());

        transactionRepository.save(transaction);

    }

    public boolean findTransactionByTransferId(String transferId) {
        return transactionRepository.findTransactionByTransferId(transferId);

    }

    public TransactionResponse findTransactionById(String id){
        Transactions transactions = transactionRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Transaction record does not exist"));
        return modelMapper.map(transactions, TransactionResponse.class);
    }

    public boolean findTransactionByReferenceId(String referenceId){
        return transactionRepository.findTransactionByReferenceId(referenceId);
    }
}
