package com.example.paymentApi.wallets;

import com.example.paymentApi.integration.circle.CircleWalletResponse;
import com.example.paymentApi.ledgers.AccountRepository;
import com.example.paymentApi.shared.exception.InsufficientBalanceException;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.shared.exception.ValidationException;
import com.example.paymentApi.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AccountRepository accountRepository;


    public void createWallet(CircleWalletResponse circleWalletResponse, String id) {
        Wallet wallet = new Wallet();
        wallet.setCircleWalletId(circleWalletResponse.getId());
        wallet.setUser(userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found")));
        wallet.setAccount(accountRepository.findById(id).orElseThrow());
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

    public void creditWallet(String circleWalletId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ValidationException("Credit amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByCircleWalletIdForUpdate(circleWalletId);

        BigDecimal currentTotal = wallet.getTotalBalance();
        BigDecimal reserved = wallet.getReservedBalance() != null ? wallet.getReservedBalance() : BigDecimal.ZERO;

        BigDecimal newTotal = currentTotal.add(amount);
        BigDecimal newAvailable = newTotal.subtract(reserved);

        wallet.setTotalBalance(newTotal);
        wallet.setAvailableBalance(newAvailable);

        walletRepository.save(wallet);
    }

    public void debitWallet(String circleWalletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findByCircleWalletIdForUpdate(circleWalletId);

        BigDecimal reservedBalance = wallet.getReservedBalance();
        BigDecimal totalBalance = wallet.getTotalBalance();

        if (reservedBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Reserved funds insufficient for this debit");
        }

        wallet.setReservedBalance(reservedBalance.subtract(amount));
        wallet.setTotalBalance(totalBalance.subtract(amount));

        // Recalculate available balance
        wallet.setAvailableBalance(wallet.getTotalBalance().subtract(wallet.getReservedBalance()));

        walletRepository.save(wallet);
    }

    public WalletBalanceResponse getWalletBalance(String id) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Wallet not found"));
        return modelMapper.map(wallet, WalletBalanceResponse.class);
    }

}
