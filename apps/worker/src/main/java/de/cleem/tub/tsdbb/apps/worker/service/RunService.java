package de.cleem.tub.tsdbb.apps.worker.service;

import de.cleem.tub.tsdbb.api.model.WorkerPreloadRequest;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.executor.ExecutionException;
import de.cleem.tub.tsdbb.apps.worker.executor.Executor;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import org.springframework.stereotype.Component;

@Component
public class RunService extends BaseSpringComponent {

    final Executor executor;

    public RunService() {
       executor=new Executor();
    }

    public void setup(final WorkerPreloadRequest workerPreloadRequest) throws TSDBAdapterException, ExecutionException {
        executor.setup(workerPreloadRequest);
    }

    public void start() throws ExecutionException {
        executor.start();
    }

    public void stop() throws TSDBAdapterException, ExecutionException {
        executor.stop();
    }

}
