package de.cleem.tub.tsdbb.commons.app;

import de.cleem.tub.tsdbb.commons.date.DateHelper;
import de.cleem.tub.tsdbb.commons.file.FileException;
import de.cleem.tub.tsdbb.commons.file.FileHelper;
import de.cleem.tub.tsdbb.commons.json.JsonException;
import de.cleem.tub.tsdbb.commons.json.JsonHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;

@Slf4j
public class BaseApp {

    private static final int STACK_TRACE_ELEMENT_INDEX = 3;

    public static void initialize() throws BaseAppException {

        final AppInfo appInfo = AppInfo.builder()
                .appClassName(getExtendingClass())
                .startDate(new Date())
                .build();


        printInfo(appInfo);
        addShutdownHook(appInfo);

    }

    private static void addShutdownHook(final AppInfo appInfo) throws BaseAppException {

        if(appInfo==null){
            throw new BaseAppException("Can add ShutdownHook - appInfo is NULL");
        }
        if(appInfo.getStartDate()==null){
            throw new BaseAppException("Can not ShutdownHook - startDate is NULL");
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @SneakyThrows
            public void run() {

                appInfo.setEndDate(new Date());
                log.info("Stopping: " + appInfo.getAppClassName() + " - " + appInfo.getEndDate());
                log.info("Uptime: " + DateHelper.getDateDifferenceString(appInfo.getEndDate(),appInfo.getStartDate()));


            }
        });
    }

    private static void printInfo(final AppInfo appInfo) throws BaseAppException {

        if(appInfo==null){
            throw new BaseAppException("Can not print AppInfo - appInfo is NULL");
        }

        log.info("Starting: " + appInfo.getAppClassName()+" - "+appInfo.getStartDate());

    }

    private static String getExtendingClass() throws BaseAppException{

        final StackTraceElement[] stackTraceElements =  Thread.currentThread().getStackTrace();

        if(stackTraceElements.length<(STACK_TRACE_ELEMENT_INDEX+1)){
            throw new BaseAppException("Invalid StackTrace element "+STACK_TRACE_ELEMENT_INDEX+ " to get class name");
        }

        return stackTraceElements[STACK_TRACE_ELEMENT_INDEX].getClassName();
    }

    protected static <T> T handleArgs(final String[] args, final Class<T> clazz, final String fallbackConfig) throws FileException, JsonException {

        final String generatorRunConfigFileString = (args.length==1) ? args[0] : fallbackConfig;

        final File generatorRunConfigFile = new File(generatorRunConfigFileString);

        return JsonHelper.objectFromByteArray(FileHelper.read(generatorRunConfigFile), clazz);

    }


}
