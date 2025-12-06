package com.example.paymentApi.integration;

import com.example.paymentApi.shared.utility.EntitySecretCipherTextUtil;
import com.example.paymentApi.shared.utility.IdempotencyUtil;
import com.example.paymentApi.shared.utility.PublicKeyFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.PublicKey;
import java.util.Map;

@Service
public class CircleWalletService {

    @Value("${TEST_API_KEY}")
    private String apiKey;

    @Value("${wallet-set-url}")
    private String circleWalletSetUrl;

    @Value("${wallet-url}")
    private String circleWalletUrl;

    @Value("${32-byte-secret-key}")
    private String entitySecret;

    @Value("${public-key}")
    private String circlePublicKey;

    private final WebClient webClient;
    private final IdempotencyUtil idempotencyUtil;
    private final EntitySecretCipherTextUtil entitySecretCipherTextUtil;

    public CircleWalletService(WebClient webClient, IdempotencyUtil idempotencyUtil,
                               EntitySecretCipherTextUtil entitySecretCipherTextUtil) {
        this.webClient = webClient;
        this.idempotencyUtil = idempotencyUtil;
        this.entitySecretCipherTextUtil = entitySecretCipherTextUtil;

    }


    public Mono<CircleWalletSetResponse> createWalletSet(String userId) {
        try {
            String idempotencyKey = idempotencyUtil.getOrCreateKey(userId);
            String cleanedKey = PublicKeyFormatter.formatPublicKey(circlePublicKey);
            PublicKey publicKey = entitySecretCipherTextUtil.loadCirclePublicKey(cleanedKey);
            byte[] entitySecretBytes = entitySecretCipherTextUtil.decodeEntitySecret(entitySecret);
            String entitySecretCipherText = entitySecretCipherTextUtil.encryptEntitySecret(publicKey, entitySecretBytes);

            CircleWalletSetRequest body = new CircleWalletSetRequest(
                    idempotencyKey,
                    "Entity walletSet A",
                    entitySecretCipherText
            );

            return webClient.post()
                    .uri(circleWalletSetUrl)
                    .header("accept: application/json")
                    .header("content-type: application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(Map.of(
                            "idempotencyKey", body.idempotencyKey(),
                            "name", body.name(),
                            "entitySecretCiphertext", body.entitySecretCiphertext()

                            ))
                    .retrieve()
                    .bodyToMono(CircleWalletSetResponse.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


   public Mono<CircleWalletResponse> createWallet(String userId) {
       try {

           String idempotencyKey = idempotencyUtil.getOrCreateKey(userId);
           String cleanedKey = PublicKeyFormatter.formatPublicKey(circlePublicKey);
           PublicKey publicKey = entitySecretCipherTextUtil.loadCirclePublicKey(cleanedKey);
           byte[] entitySecretBytes = entitySecretCipherTextUtil.decodeEntitySecret(entitySecret);
           String entitySecretCipherText = entitySecretCipherTextUtil.encryptEntitySecret(publicKey, entitySecretBytes);

           CircleWalletRequest body = new CircleWalletRequest(
                   idempotencyKey,
                   "SCA",
                  new String[] {"MATIC-MUMBAI"},
                   2,
                   entitySecretCipherText,
                   "id"
           );


           return webClient.post()
                   .uri(circleWalletUrl)
                   .header("accept: application/json")
                   .header("content-type: application/json")
                   .header("Authorization", "Bearer " + apiKey)
                   .bodyValue(Map.of(
                           "idempotency", body.idempotencyKey(),
                           "accountType", body.accountType(),
                           "blockchains", body.blockchains(),
                           "count", body.count(),
                           "entitySecretCipherText", body.entitySecretCiphertext(),
                           "walletSetId", body.walletSetId()

                   ))
                   .retrieve()
                   .bodyToMono(CircleWalletResponse.class);

       }
       catch (Exception e) {
           throw new RuntimeException(e);
       }
   }


}

