package com.example.paymentApi.wallets;

import com.example.paymentApi.integration.circle.CircleWalletResponse;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.users.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository,
                         ModelMapper modelMapper) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

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
        wallet.setReferenceId(circleWalletResponse.getReferenceId());

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

}
