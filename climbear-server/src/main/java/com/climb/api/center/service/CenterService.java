package com.climb.api.center.service;

import com.climb.api.center.domain.dto.response.CenterRecordResponseDTO;
import com.climb.api.center.domain.entity.Center;
import com.climb.api.center.domain.type.DailyProblem;
import com.climb.api.center.domain.type.LevelCount;
import com.climb.api.center.repository.CenterRepository;
import com.climb.api.record.domain.entity.SolvedRoute;
import com.climb.api.record.repository.SolvedRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CenterService {

    private final CenterRepository centerRepository;
    private final SolvedRouteRepository solvedRouteRepository;

    public List<Center> getCenterList() {
        return centerRepository.findAll();
    }

    public List<CenterRecordResponseDTO> getMyCenterRecord(Integer userId) {
        List<SolvedRoute> solvedRoutes = solvedRouteRepository.findByUserId(userId);

        Map<String, Map<LocalDate, Map<String, Integer>>> recordMap = new HashMap<>();

        // SolvedRoute의 createdAt을 LocalDate로 변환하고, 3개의 키로 그룹화하여 카운트
        for (SolvedRoute route : solvedRoutes) {
            LocalDate solvedDate = route.getCreatedAt().toLocalDateTime().toLocalDate();
            String centerName = route.getCenterName();
            String level = route.getLevel();

            recordMap.computeIfAbsent(centerName, c -> new HashMap<>())
                    .computeIfAbsent(solvedDate, d -> new HashMap<>())
                    .merge(level, 1, Integer::sum);
        }

        List<CenterRecordResponseDTO> result = new ArrayList<>();

        for (Map.Entry<String, Map<LocalDate, Map<String, Integer>>> centerEntry : recordMap.entrySet()) {
            String centerName = centerEntry.getKey();
            Map<LocalDate, Map<String, Integer>> dateMap = centerEntry.getValue();

            List<DailyProblem> dailyProblems = new ArrayList<>();
            for (Map.Entry<LocalDate, Map<String, Integer>> dateEntry : dateMap.entrySet()) {
                LocalDate date = dateEntry.getKey();
                Map<String, Integer> levelMap = dateEntry.getValue();

                List<LevelCount> levelCounts = levelMap.entrySet().stream()
                        .map(e -> LevelCount.builder()
                                .level(e.getKey())
                                .count(e.getValue())
                                .build())
                        .toList();

                dailyProblems.add(DailyProblem.builder()
                        .date(date)
                        .levelCount(levelCounts)
                        .build());
            }

            result.add(CenterRecordResponseDTO.builder()
                    .centerName(centerName)
                    .dailyProblem(dailyProblems)
                    .build());
        }

        return result;
    }
}
