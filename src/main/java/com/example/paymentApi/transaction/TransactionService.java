package com.example.paymentApi.transaction;

import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.wallets.Wallet;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService{

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    @CacheEvict(value = "allTransactions", allEntries = true)
    public Transactions createTransactionRecord(TransactionRequest request){
        Transactions transaction = new Transactions();
        transaction.setWallet(request.getWallet());
        transaction.setUser(request.getUser());
        transaction.setProviderTransactionId(request.getProviderTransactionId());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmounts());
        transaction.setStatus(request.getStatus());
        transaction.setReferenceId(request.getReferenceId());
        transaction.setSourceAddress(request.getSourceAddress());
        transaction.setDestinationAddress(request.getDestinationAddress());
        transaction.setSourceCurrency(request.getSourceCurrency());
        transaction.setDestinationCurrency(request.getDestinationCurrency());
        transaction.setDirection(request.getDirection());
        transaction.setBalanceAfter(request.getBalanceAfter());

        return transactionRepository.save(transaction);

    }

    @Cacheable(value = "transaction", key = "#id")
    public TransactionResponse getTransaction(String id){
        Transactions transactions = transactionRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Transaction record does not exist"));
        return modelMapper.map(transactions, TransactionResponse.class);
    }

    @Cacheable(
            value = "allTransactions",
            key = "'offset=' + #offset + ':pageSize=' + #pageSize + ':field=' + (#field == null ? 'createdAt' : #field)"
    )
    public TransactionResponse getAllTransactions(int offset, int pageSize, String field ){

        String sortField = (field == null || field.isBlank())
                ? "createdAt"
                : field;

        Sort sort = Sort.by(Sort.Direction.DESC, sortField);

        Pageable pageable = PageRequest.of(offset, pageSize, sort);

        Page<Transactions> transactions = transactionRepository.findAll(pageable);

        return modelMapper.map(transactions, TransactionResponse.class);

    }

}
