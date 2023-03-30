package de.cleem.tub.tsdbb.apps.worker.service.run;

import de.cleem.tub.tsdbb.api.model.WorkerSetupRequest;
import de.cleem.tub.tsdbb.apps.worker.adapters.TSDBAdapterException;
import de.cleem.tub.tsdbb.apps.worker.executor.ExecutionException;
import de.cleem.tub.tsdbb.apps.worker.executor.Executor;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import de.cleem.tub.tsdbb.commons.spring.remotecontrol.RemoteControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;



@Component
@ApplicationScope
public class RunService extends BaseSpringComponent {

    private Executor executor;

    @Autowired
    private RemoteControlService remoteControlService;

    @Override
    public void afterPropertiesSet() throws Exception {

       super.afterPropertiesSet();
        executor=new Executor(remoteControlService,getServerUrl());

    }

    public void setup(final WorkerSetupRequest workerSetupRequest) throws TSDBAdapterException, ExecutionException {
        executor.setup(workerSetupRequest);
    }

    public void start() throws ExecutionException {
        executor.start();
    }

    public void stop()  {
        executor.stop();
    }

    public void cleanup() throws TSDBAdapterException {
        executor.cleanup();
    }

    public void close() {
        executor.close();
    }


}
