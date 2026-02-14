package com.example.paymentApi.integration.circle;

import com.example.paymentApi.shared.enums.TransferBlockchain;
import com.example.paymentApi.shared.exception.ExternalServiceException;
import com.example.paymentApi.shared.utility.EntitySecretCipherTextUtil;
import com.example.paymentApi.shared.utility.RedisUtil;
import com.example.paymentApi.shared.utility.PublicKeyFormatter;
import com.example.paymentApi.webhook.circle.OutboundTransferInitiationResponse;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.net.ssl.SSLHandshakeException;
import java.security.PublicKey;
import java.util.Map;

@Service
@RequiredArgsConstructor
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

    @Value("${polygon-amoy-tokenId}")
    private String tokenId;

    private final WebClient webClient;
    private final RedisUtil redisUtil;


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
                                                                         TransferBlockchain blockchain,
                                                                         String[] amounts, String walletAddress
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
                    blockchain,
                    walletAddress,
                    tokenId
            );

            return webClient.post()
                    .uri(circleTransferUrl)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(Map.of(
                            "idempotencyKey", data.idempotencyKey(),
                            "destinationAddress", data.destinationAddress(),
                            "entitySecretCipherText", data.entitySecretCiphertext(),
                            "amounts", data.amounts(),
                            "feeLevel", data.feeLevel(),
                            "tokenId", data.tokenId(),
                            "blockchain", data.blockchain(),
                            "walletAddress", data.walletAddress()
                    ))
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(json->{
                        JsonNode response = json.path("data");
                        OutboundTransferInitiationResponse out = new OutboundTransferInitiationResponse();
                        out.setId(response.path("id").asText());
                        out.setState(response.path("state").asText());
                        return out;
                    });

        }
        catch (Exception e) {
           throw new ExternalServiceException("Error occurred during api call", e);
        }
    }
}

