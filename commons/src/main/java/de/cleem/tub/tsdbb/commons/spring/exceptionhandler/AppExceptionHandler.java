package de.cleem.tub.tsdbb.commons.spring.exceptionhandler;

import de.cleem.tub.tsdbb.commons.exception.BaseException;
import de.cleem.tub.tsdbb.commons.spring.base.component.BaseSpringComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class AppExceptionHandler extends BaseSpringComponent {

    @Value("${application.errors.logStackTrace:true}")
    private boolean logStackTrace;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationException(Exception exception, WebRequest request) {

        logException(exception,request);

        return ResponseEntity.badRequest().body(exception.getMessage());

    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> baseException(Exception exception, WebRequest request) {

        logException(exception,request);

        return ResponseEntity.badRequest().body(exception.getMessage());

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception exception, WebRequest request) {

        logException(exception,request);

        return ResponseEntity.internalServerError().body(exception.getMessage());

    }

    private void logException(final Exception e, final WebRequest request){

        log.error(extractInformation(e,request));

        if(logStackTrace){
            e.printStackTrace();
        }

    }

    private String extractInformation(final Exception exception, final WebRequest request){

        final StringBuilder builder = new StringBuilder();

        builder.append(exception.getClass().getSimpleName());

        if(request instanceof ServletWebRequest servletWebRequest){

            builder.append(" for ");
            builder.append(servletWebRequest.getRequest().getMethod());
            builder.append(" Request from IP: ");
            builder.append(servletWebRequest.getRequest().getRemoteAddr());
            builder.append(" to URL: ");
            builder.append(servletWebRequest.getRequest().getRequestURI());

        }

        builder.append(" - ");
        builder.append(exception.getMessage());

        return builder.toString();

    }

}