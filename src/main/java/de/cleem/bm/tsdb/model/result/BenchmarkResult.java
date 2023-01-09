package de.cleem.bm.tsdb.model.result;

import de.cleem.bm.tsdb.model.task.TaskResult;
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
