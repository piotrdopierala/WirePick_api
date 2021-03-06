package pl.dopierala.wirepickapi.exceptions.definitions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.DateParseException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemByDeviceIdNotFoundException;
import pl.dopierala.wirepickapi.exceptions.definitions.Stock.StockItemIdNotFoundException;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionAOPHandler {

    @ExceptionHandler(DateParseException.class)
    public ResponseEntity hireDateParseExceptionHandler(final DateParseException e, final HttpServletResponse response){
        Map<String,Object> body = new HashMap<>();
        body.put("date:", LocalDateTime.now());
        body.put("exception:",e.getClass().getSimpleName());
        body.put("message:",e.getMessage());
        body.put("date tried to parse:", e.getDateTriedToParse());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(StockItemIdNotFoundException.class)
    public ResponseEntity stockItemIdNotFoundExceptionHandler(final StockItemIdNotFoundException e, final HttpServletResponse response){
        return prepareResponseEntity(e.getClass().getSimpleName(), e.getMessage(), e);
    }

    @ExceptionHandler(StockItemByDeviceIdNotFoundException.class)
    public ResponseEntity stockItemByDeviceIdNotFoundExceptionHandler(final StockItemByDeviceIdNotFoundException e, final HttpServletResponse response){
        return prepareResponseEntity(e.getClass().getSimpleName(), e.getMessage(), e);
    }

    @ExceptionHandler(DeviceNotAvailableAlreadyBookedException.class)
    public ResponseEntity deviceAlreadyHiredExceptionHandler(final DeviceNotAvailableAlreadyBookedException e, final HttpServletResponse response){
        return prepareResponseEntity(e.getClass().getSimpleName(),e.getMessage(),e);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity userNotFoundHandler(final UserNotFoundException e, final HttpServletResponse response){
        return prepareResponseEntity(e.getClass().getSimpleName(),e.getMessage(),e);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity methodArgumentTypeMismatchExceptionHandler(final MethodArgumentTypeMismatchException e, final HttpServletResponse response){
        return prepareResponseEntity(e.getClass().getSimpleName(),e.getMessage(),e);
    }

    private ResponseEntity prepareResponseEntity(String simpleName, String message, RuntimeException e) {
        Map<String,Object> body = new HashMap<>();
        body.put("date:", LocalDateTime.now());
        body.put("exception:", simpleName);
        body.put("message:", message);
        return ResponseEntity.badRequest().body(body);
    }

}
