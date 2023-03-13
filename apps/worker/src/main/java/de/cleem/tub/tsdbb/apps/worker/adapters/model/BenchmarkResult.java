package de.cleem.tub.tsdbb.apps.worker.adapters.model;

import de.cleem.tub.tsdbb.apps.worker.executor.TaskResult;
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
