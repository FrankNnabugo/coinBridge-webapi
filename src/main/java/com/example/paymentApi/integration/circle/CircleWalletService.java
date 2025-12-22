package com.example.paymentApi.integration.circle;

import com.example.paymentApi.shared.utility.EntitySecretCipherTextUtil;
import com.example.paymentApi.shared.utility.RedisUtil;
import com.example.paymentApi.shared.utility.PublicKeyFormatter;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.PublicKey;
import java.util.Map;

@Service
public class CircleWalletService {

    @Value("${circle-api-key}")
    private String apiKey;

    @Value("${wallet-set-url}")
    private String circleWalletSetUrl;

    @Value("${wallet-url}")
    private String circleWalletUrl;

    @Value("${32-byte-secret-key}")
    private String entitySecret;

    @Value("${circle-public-key}")
    private String circlePublicKey;

    private final WebClient webClient;
    private final RedisUtil redisUtil;


    public CircleWalletService(WebClient webClient, RedisUtil redisUtil) {
        this.webClient = webClient;
        this.redisUtil = redisUtil;

    }

    public Mono<CircleWalletResponse> createCircleWallet(String userId) throws Exception {
        try {
            String idempotencyKey = redisUtil.getOrCreateKey(userId);
            String cleanedKey = PublicKeyFormatter.formatPublicKey(circlePublicKey);
            PublicKey publicKey = EntitySecretCipherTextUtil.loadCirclePublicKey(cleanedKey);
            byte[] entitySecretBytes = EntitySecretCipherTextUtil.decodeEntitySecret(entitySecret);
            String entitySecretCipherText = EntitySecretCipherTextUtil.encryptEntitySecret(publicKey, entitySecretBytes);

            CircleWalletSetRequest body = new CircleWalletSetRequest(
                    idempotencyKey,
                    "Entity walletSet A",
                    entitySecretCipherText
            );

            return webClient.post()
                    .uri(circleWalletSetUrl)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(Map.of(
                            "idempotencyKey", body.idempotencyKey(),
                            "name", body.name(),
                            "entitySecretCiphertext", body.entitySecretCiphertext()

                            ))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .flatMap(CircleWalletSetResponse -> {
                        String walletSetId = CircleWalletSetResponse.path("data")
                                .path("walletSet")
                                .path("id")
                                .asText();

                        CircleWalletRequest walletData = new CircleWalletRequest(
                                idempotencyKey,
                                "SCA",
                               new String[]{"MATIC-AMOY"},
                                1,
                                entitySecretCipherText,
                                walletSetId
                        );

                        return webClient.post()
                                .uri(circleWalletUrl)
                                .header("Accept", "application/json")
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Bearer " + apiKey)
                                .bodyValue(Map.of(
                                        "idempotencyKey", walletData.idempotencyKey(),
                                        "accountType", walletData.accountType(),
                                        "blockchains", walletData.blockchains(),
                                        "count", walletData.count(),
                                        "entitySecretCipherText", walletData.entitySecretCiphertext(),
                                        "walletSetId", walletData.walletSetId()

                                ))
                                .retrieve()
                                .bodyToMono(JsonNode.class)
                                .map(json->{
                                    JsonNode walletNode = json.path("data")
                                                    .path("wallets")
                                                            .get(0);

                                    CircleWalletResponse w = new CircleWalletResponse();
                                    w.setId(walletNode.path("id").asText());
                                    w.setBlockchain(walletNode.path("blockchain").asText());
                                    w.setAddress(walletNode.path("address").asText());
                                    w.setWalletSetId(walletNode.path("walletSetId").asText());
                                    w.setAccountType(walletNode.path("accountType").asText());
                                    w.setCustodyType(walletNode.path("custodyType").asText());
                                    w.setState(walletNode.path("state").asText());
                                    return w;
                                });
                    });

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void createOnChainTransfer(String address, Map<String, String> blockchain, String amount){

    }
}

