package com.banking.support.aiservice.service;

import lombok.RequiredArgsConstructor;
import okhttp3.Cache;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class RagQueryService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private  final CacheService cacheService;

    public String query(String question) {
        // 1. Build cache key (hashed for safety - avoids special chars)
        String cacheKey = "ai:query:" + hashQuestion(question);

        // 2. Cache check - HIT? return instantly
        var cached = cacheService.get(cacheKey);
        if (cached.isPresent()) {

            return cached.get();

        }

        // 3. Cache MISS - run full RAG
        long start = System.currentTimeMillis();
        String answer = runRagQuery(question);
        long elapsed = System.currentTimeMillis() - start;

        // 4. Save to cache
        cacheService.put(cacheKey, answer);
        System.out.println("LLM call took: " + elapsed + "ms");

        return answer;

    }

    private String runRagQuery(String question) {

        var documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                //.similarityThreshold(0.7)
                .topK(5)
                .build();

        var ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();

        return chatClient.prompt()
                .advisors(ragAdvisor)
                .user(question)
                .call()
                .content();
    }

    private String hashQuestion(String question) {

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(question.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.substring(0, 32); // first 32 chars is enough
        }
        catch (Exception e) {
            return String.valueOf(question.hashCode());
        }
    }
}