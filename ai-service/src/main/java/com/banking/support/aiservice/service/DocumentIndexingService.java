package com.banking.support.aiservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentIndexingService {

    private final VectorStore vectorStore;

    private static final int BATCH_SIZE = 20;          // ~13k tokens, well under 30k/min
    private static final long PAUSE_MS = 60_000;       // let the per-minute window reset

    public int indexDocument(String text, String sourceName) {
        Document doc = new Document(text, Map.of("source", sourceName));

        // Spring AI 2.0 builder pattern, the old constructor is removed
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .withMinChunkSizeChars(350)
                .withMinChunkLengthToEmbed(5)
                .withMaxNumChunks(10000)
                .withKeepSeparator(true)
                .build();

        List<Document> chunks = splitter.apply(List.of(doc));

        addInBatches(chunks);                            // <-- the missing call

        log.info("Indexed {} chunks from {}", chunks.size(), sourceName);
        return chunks.size();
    }

    private void addInBatches(List<Document> chunks) {
        int totalBatches = (int) Math.ceil((double) chunks.size() / BATCH_SIZE);

        for (int i = 0; i < chunks.size(); i += BATCH_SIZE) {
            List<Document> batch = chunks.subList(i, Math.min(i + BATCH_SIZE, chunks.size()));
            int batchNo = (i / BATCH_SIZE) + 1;

            vectorStore.add(batch);
            log.info("Embedded batch {}/{} ({} chunks)", batchNo, totalBatches, batch.size());

            if (i + BATCH_SIZE < chunks.size()) {
                try {
                    Thread.sleep(PAUSE_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Indexing interrupted", e);
                }
            }
        }
    }   // <-- closes the method
}       // <-- closes the class