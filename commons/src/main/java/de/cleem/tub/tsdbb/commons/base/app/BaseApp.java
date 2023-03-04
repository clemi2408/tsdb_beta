package de.cleem.tub.tsdbb.commons.base.app;

import de.cleem.tub.tsdbb.commons.base.clazz.BaseClass;
import de.cleem.tub.tsdbb.commons.date.DateHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class BaseApp extends BaseClass {

    private static final int STACK_TRACE_ELEMENT_INDEX = 4;

    public static void startBaseApp() throws BaseAppException {

        final BaseAppInfo appInfo = BaseAppInfo.builder()
                .appClassName(getExtendingClass())
                .startDate(new Date())
                .build();


        printInfo(appInfo);
        addShutdownHook(appInfo);

    }

    private static void addShutdownHook(final BaseAppInfo appInfo) throws BaseAppException {

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

    private static void printInfo(final BaseAppInfo appInfo) throws BaseAppException {

        if(appInfo==null){
            throw new BaseAppException("Can not print AppInfo - appInfo is NULL");
        }

        log.info("Started: " + appInfo.getAppClassName()+" - "+appInfo.getStartDate());

    }

    private static String getExtendingClass() throws BaseAppException{

        final StackTraceElement[] stackTraceElements =  Thread.currentThread().getStackTrace();

        if(stackTraceElements.length<(STACK_TRACE_ELEMENT_INDEX+1)){
            throw new BaseAppException("Invalid StackTrace element "+STACK_TRACE_ELEMENT_INDEX+ " to get class name");
        }

        return stackTraceElements[STACK_TRACE_ELEMENT_INDEX].getClassName();
    }



}
