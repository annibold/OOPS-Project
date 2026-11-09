package com.itsm.demo.service;

import com.itsm.demo.model.Incident;
import com.itsm.demo.model.IncidentStatus;
import com.itsm.demo.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Intelligent Feature 2: Historical Resolution Recommendation.
 * Analyzes past resolved/closed incidents and recommends proven fixes to technicians
 * investigating similar problems.
 */
@Service
public class ResolutionSuggestionService {

    public record ResolutionSuggestion(Long incidentId, String incidentTitle,
                                        String resolution, double confidence) { }

    private final IncidentRepository incidentRepository;
    private final DuplicateDetectionService duplicateDetectionService;

    public ResolutionSuggestionService(IncidentRepository incidentRepository,
                                       DuplicateDetectionService duplicateDetectionService) {
        this.incidentRepository = incidentRepository;
        this.duplicateDetectionService = duplicateDetectionService;
    }

    /**
     * Recommends resolutions from successfully resolved historical incidents.
     */
    public List<ResolutionSuggestion> suggestResolutions(Incident current) {
        Set<String> currentTokens = duplicateDetectionService.tokenize(
                current.getTitle() + " " + current.getDescription());

        List<Incident> resolvedIncidents = incidentRepository.findAll().stream()
                .filter(i -> !i.getId().equals(current.getId()))
                .filter(i -> i.getStatus() == IncidentStatus.RESOLVED ||
                             i.getStatus() == IncidentStatus.VERIFIED ||
                             i.getStatus() == IncidentStatus.CLOSED)
                .filter(i -> i.getResolution() != null && !i.getResolution().isBlank())
                .toList();

        List<ResolutionSuggestion> suggestions = new ArrayList<>();
        for (Incident past : resolvedIncidents) {
            Set<String> pastTokens = duplicateDetectionService.tokenize(
                    past.getTitle() + " " + past.getDescription());
            double score = duplicateDetectionService.jaccardSimilarity(currentTokens, pastTokens);

            if (current.getCategory() != null && current.getCategory() == past.getCategory()) {
                score = Math.min(1.0, score + 0.20);
            }

            if (score >= 0.25) {
                suggestions.add(new ResolutionSuggestion(
                        past.getId(), past.getTitle(), past.getResolution(), score));
            }
        }

        suggestions.sort((a, b) -> Double.compare(b.confidence(), a.confidence()));
        return suggestions;
    }
}
