package de.cleem.bm.tsdb.model.result;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenchmarkResult {

    @Getter
    private List<TaskResult> results;
}
