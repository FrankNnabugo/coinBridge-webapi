package com.example.paymentApi.transaction;

import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.users.UserRepository;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.webhook.circle.CircleInboundWebhookResponse;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class TransactionService{

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private ModelMapper modelMapper;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository,
                              WalletRepository walletRepository, ModelMapper modelMapper){
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.modelMapper = modelMapper;
    }

    public void createTransactionRecord(CircleInboundWebhookResponse circleInboundWebhookResponse, Wallet wallet){
        Transactions transactions = new Transactions();

        transactions.setWallet(wallet);
        transactions.setUser(wallet.getUser());
//        transactions.setUser(userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User does not exist")));
//        transactions.setWallet(walletRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Wallet does not exist")));
        transactions.setTransferId(circleWebhookResponse.getNotification().getId());
        transactions.setType(circleInboundWebhookResponse.getNotification().getTransactionType());
        transactions.setAmount(circleInboundWebhookResponse.getNotification().getAmounts());
        transactions.setStatus(circleInboundWebhookResponse.getNotification().getState());

        transactionRepository.save(transactions);

    }

    public boolean findTransactionByTransferId(String transferId) {
        return transactionRepository.findTransactionByTransferId(transferId);

    }

    public TransactionResponse findTransactionById(String id){
        Transactions transactions = transactionRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Transaction record does not exist"));
        return modelMapper.map(transactions, TransactionResponse.class);
    }
}
