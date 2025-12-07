package com.example.paymentApi.wallets;

import com.example.paymentApi.integration.CircleWalletResponse;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.users.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository,
                         ModelMapper modelMapper){
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public void createWallet(CircleWalletResponse circleWalletResponse, String id){
        Wallet wallet = new Wallet();
        wallet.setCircleWalletId(circleWalletResponse.getId());
        wallet.setUser(userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found")));
        wallet.setBlockchain(circleWalletResponse.getBlockchain());
        wallet.setState(circleWalletResponse.getState());
        wallet.setCustodyType(circleWalletResponse.getCustodyType());
        wallet.setAccountType(circleWalletResponse.getAccountType());
        wallet.setAddress(circleWalletResponse.getAddress());
        wallet.setWalletName(circleWalletResponse.getWalletName());
        wallet.setWalletSetId(circleWalletResponse.getWalletSetId());
        wallet.setReferenceId(circleWalletResponse.getReferenceId());

        Wallet savedWallet = walletRepository.save(wallet);
       // return modelMapper.map(savedWallet, WalletResponse.class);

    }

}
