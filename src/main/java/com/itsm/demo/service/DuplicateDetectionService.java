package com.itsm.demo.service;

import com.itsm.demo.model.Incident;
import com.itsm.demo.model.IncidentStatus;
import com.itsm.demo.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Intelligent Feature 1: Duplicate & Correlated Incident Detection.
 * Uses tokenization, stop-word filtering, and Jaccard similarity to identify
 * related incidents that likely stem from the same root problem.
 */
@Service
public class DuplicateDetectionService {

    public record DuplicateMatch(Incident incident, double similarityScore, String reason) { }

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "the", "in", "on", "at", "for", "to", "of", "is", "are",
            "was", "were", "and", "or", "be", "with", "from", "by", "it",
            "this", "that", "unable", "not", "cannot", "issue", "problem"
    );

    private static final Pattern WORD_PATTERN = Pattern.compile("[^a-zA-Z0-9]+");
    private static final double SIMILARITY_THRESHOLD = 0.30;

    private final IncidentRepository incidentRepository;

    public DuplicateDetectionService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    /**
     * Finds active incidents that potentially duplicate or correlate with the given incident.
     */
    public List<DuplicateMatch> findDuplicates(Incident target) {
        Set<String> targetTokens = tokenize(target.getTitle() + " " + target.getDescription());
        List<Incident> candidates = incidentRepository.findAll().stream()
                .filter(i -> !i.getId().equals(target.getId()))
                .filter(i -> i.getStatus() != IncidentStatus.CLOSED)
                .toList();

        List<DuplicateMatch> matches = new ArrayList<>();
        for (Incident other : candidates) {
            Set<String> otherTokens = tokenize(other.getTitle() + " " + other.getDescription());
            double score = jaccardSimilarity(targetTokens, otherTokens);

            // Boost score if same category
            if (target.getCategory() != null && target.getCategory() == other.getCategory()) {
                score = Math.min(1.0, score + 0.15);
            }

            if (score >= SIMILARITY_THRESHOLD) {
                String reason = String.format("High textual & category similarity (%.0f%% match with #%d '%s')",
                        score * 100, other.getId(), other.getTitle());
                matches.add(new DuplicateMatch(other, score, reason));
            }
        }

        matches.sort((a, b) -> Double.compare(b.similarityScore(), a.similarityScore()));
        return matches;
    }

    /** Tokenizes text into normalized keywords, discarding common stopwords. */
    public Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) return Collections.emptySet();
        return Arrays.stream(WORD_PATTERN.split(text.toLowerCase()))
                .filter(w -> w.length() > 2)
                .filter(w -> !STOP_WORDS.contains(w))
                .collect(Collectors.toSet());
    }

    /** Calculates Jaccard similarity index between two token sets: |A intersect B| / |A union B| */
    public double jaccardSimilarity(Set<String> setA, Set<String> setB) {
        if (setA.isEmpty() || setB.isEmpty()) return 0.0;
        Set<String> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);

        Set<String> union = new HashSet<>(setA);
        union.addAll(setB);

        if (union.isEmpty()) return 0.0;
        return (double) intersection.size() / union.size();
    }
}