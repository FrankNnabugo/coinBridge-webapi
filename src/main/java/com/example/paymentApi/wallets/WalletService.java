package com.example.paymentApi.wallets;

import com.example.paymentApi.integration.circle.CircleWalletResponse;
import com.example.paymentApi.shared.exception.InsufficientBalanceException;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.shared.exception.ValidationException;
import com.example.paymentApi.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    public void createWallet(CircleWalletResponse circleWalletResponse, String id) {
        Wallet wallet = new Wallet();
        wallet.setCircleWalletId(circleWalletResponse.getId());
        wallet.setUser(userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
        wallet.setBlockchain(circleWalletResponse.getBlockchain());
        wallet.setState(circleWalletResponse.getState());
        wallet.setCustodyType(circleWalletResponse.getCustodyType());
        wallet.setAccountType(circleWalletResponse.getAccountType());
        wallet.setAddress(circleWalletResponse.getAddress());
        wallet.setWalletSetId(circleWalletResponse.getWalletSetId());

        walletRepository.save(wallet);
    }

    public WalletResponse getWallet(String id) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Wallet does not exist"));
        return modelMapper.map(wallet, WalletResponse.class);
    }

    public WalletListResponse getAllWallets(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("created_at").descending());
        Page<Wallet> walletPage = walletRepository.findAll(pageable);

        List<WalletResponse> walletResponses = walletPage.getContent().stream().map(element ->
                modelMapper.map(element, WalletResponse.class)).toList();

        WalletListResponse response = new WalletListResponse();
        response.setWallets(walletResponses);

        return response;

    }

    public void creditWallet(String id, BigDecimal amount){
        if (amount == null || amount.signum() <= 0) {
            throw new ValidationException("Credit amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Wallet does not exist"));


        BigDecimal currentTotal = wallet.getTotalBalance();
        BigDecimal reserved = wallet.getReservedBalance() != null
                ? wallet.getReservedBalance()
                : BigDecimal.ZERO;

        BigDecimal newTotal = currentTotal.add(amount);
        BigDecimal newAvailable = newTotal.subtract(reserved);

        wallet.setTotalBalance(newTotal);
        wallet.setAvailableBalance(newAvailable);

        walletRepository.save(wallet);
    }

    public void debitWallet(String id, BigDecimal amount ){

        if(amount == null){
            throw new ValidationException("Amount cannot be null or empty");
        }

        if(amount.compareTo(BigDecimal.ZERO)<= 0){
            throw new ValidationException("Amount must be greater than zero");
        }
        Wallet wallet = walletRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Wallet does not exist"));

        if (wallet.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient fund");
        }

        BigDecimal totalBalance = wallet.getTotalBalance();
        BigDecimal reservedBalance = wallet.getReservedBalance();
        BigDecimal availableBalance = wallet.getAvailableBalance();

        if (totalBalance.compareTo(BigDecimal.ZERO) < 0 ||
                availableBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Wallet balances are corrupted");
        }

        if (reservedBalance.compareTo(totalBalance) > 0) {
            throw new IllegalStateException("Reserved balance exceeds total balance");
        }
        BigDecimal newTotalBalance = totalBalance.subtract(amount);
        BigDecimal newAvailableBalance = newTotalBalance.subtract(reservedBalance);
        
        wallet.setTotalBalance(newTotalBalance);
        wallet.setAvailableBalance(newAvailableBalance);
        walletRepository.save(wallet);
    }

}
