package com.example.paymentApi.integration.circle;

import com.example.paymentApi.shared.enums.BlockchainType;
import com.example.paymentApi.shared.exception.ResourceNotFoundException;
import com.example.paymentApi.shared.mapper.WebhookMapper;
import com.example.paymentApi.shared.utility.EntitySecretCipherTextUtil;
import com.example.paymentApi.shared.utility.RedisUtil;
import com.example.paymentApi.shared.utility.PublicKeyFormatter;
import com.example.paymentApi.wallets.Wallet;
import com.example.paymentApi.wallets.WalletRepository;
import com.example.paymentApi.webhook.circle.CircleInboundWebhookResponse;
import com.example.paymentApi.webhook.circle.CircleOutBoundWebhookResponse;
import com.example.paymentApi.webhook.circle.OutboundTransferInitiationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;
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

    @Value("${circle-transfer-url}")
    private String circleTransferUrl;

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
                                .map(json -> {
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

    public Mono<OutboundTransferInitiationResponse> createTransferIntent(String userId, String destinationAddress,
                                                                         BlockchainType blockchain,
                                                                         BigDecimal amounts
    ) {
        try {
            String idempotencyKey = redisUtil.getOrCreateKey(userId);
            String cleanedKey = PublicKeyFormatter.formatPublicKey(circlePublicKey);
            PublicKey publicKey = EntitySecretCipherTextUtil.loadCirclePublicKey(cleanedKey);
            byte[] entitySecretBytes = EntitySecretCipherTextUtil.decodeEntitySecret(entitySecret);
            String entitySecretCipherText = EntitySecretCipherTextUtil.encryptEntitySecret(publicKey, entitySecretBytes);

            CircleTransferRequest data = new CircleTransferRequest(
                  idempotencyKey,
                    destinationAddress,
                    amounts,
                    "MEDIUM",
                    entitySecretCipherText,
                    blockchain

            );

            return webClient.post()
                    .uri(circleTransferUrl)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(Map.of(
                            "idempotencyKey", data.idempotencyKey(),
                            "destinationAddress", data.destinationAddress(),
                            "amounts", data.amounts(),
                            "feeLevel", data.feeLevel(),
                            "entitySecretCipherText", data.entitySecretCiphertext(),
                            " blockchain", data.blockchain()
                    ))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(json->{
                        JsonNode response = json.path("data")
                                .path("id")
                                .path("state")
                                .get(0);
                        OutboundTransferInitiationResponse out = new OutboundTransferInitiationResponse();
                        out.setId(response.path("id").asText());
                        out.setState(response.path("state").asText());
                        return out;
                    });

        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

