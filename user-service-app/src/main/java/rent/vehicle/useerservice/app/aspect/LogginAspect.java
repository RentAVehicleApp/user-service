package rent.vehicle.useerservice.app.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import rent.vehicle.common.CustomPage;
import rent.vehicle.dto.response.CustomerResponse;

import java.util.*;

@Component
@Aspect
@Slf4j

public class LogginAspect {
    private static final int HEAD_LIMIT = 20;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    @Pointcut("execution(public * rent.vehicle.useerservice.app.controller.*.*(..))")
    public void controllerLog(){

    }
    @Pointcut("execution(public * rent.vehicle.useerservice.app.service.*.*(..))")
    public void serviceLog(){

    }
    @Before("controllerLog()")
    public void doBeforeController(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        if (request != null) {
            log.info("NEW REQUEST: IP: {}, URL: {}, HTTP_METHOD: {}, CONTROLLER_METHOD: {},{}",
                    request.getRemoteAddr(),
                    request.getRequestURL(),
                    request.getMethod(),
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());
        }
    }
    @Before("serviceLog()")
    public void doBeforeService(JoinPoint joinPoint){
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();
        String stringArgs = args.length > 0 ? Arrays.toString(args) : "METHOD HAS NO ARGUMENT";

        log.info("RUN SERVICE: SERVICE_METHOD: {},{}\nMETHOD_ARGS: [{}]",
                className,
                methodName,
                stringArgs);
    }
    @AfterReturning(returning = "returnObject", pointcut = "controllerLog()")
    public void doAfterReturning(Object returnObject){
        try{
            if(returnObject != null){
                if(returnObject instanceof CustomerResponse){
                    log.info("-- returned object = {}", MAPPER.writeValueAsString(safeView(returnObject)));
                }
                if(returnObject instanceof CustomPage<?> col){
                    var head = col.getContent().stream().limit(HEAD_LIMIT).map(this::safeView).toList();
                    log.info("-- returned list size = {}, head = {}",col.getContent().size(),MAPPER.writeValueAsString(head));
                }
            }
        }catch (Exception e) {
            // На случай проблем сериализации (ленивые связи и т.п.)
            log.info("← returned (fallback) {}", String.valueOf(returnObject));
    }
}

    private Object safeView(Object returnObject) {
        if (returnObject == null) {
            return Collections.emptyMap();
        }
        if(returnObject instanceof CustomerResponse){
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id",((CustomerResponse) returnObject).getId());
            map.put("firstName",((CustomerResponse) returnObject).getFirstName());
            map.put("lastName",((CustomerResponse) returnObject).getLastName());
            map.put("email",maskData(((CustomerResponse) returnObject).getEmail()));
            map.put("phoneNumber",maskData(((CustomerResponse) returnObject).getPhoneNumber()));
            map.put("birthDate",((CustomerResponse) returnObject).getBirthDate());
            map.put("licenseType",((CustomerResponse) returnObject).getLicenseType());
            map.put("drivingLicenseNumber",maskData(((CustomerResponse) returnObject).getDrivingLicenseNumber()));
            map.put("createdAt",((CustomerResponse) returnObject).getCreatedAt());
            map.put("updatedAt",((CustomerResponse) returnObject).getUpdatedAt());
            map.put("status",((CustomerResponse) returnObject).getStatus());
            return map;
        }
         return returnObject;
    }

    private Object maskData(String data) {
        if(data.contains("@")){
            int at = data.indexOf('@');
            String local = data.substring(0, at);

            // маскируем только локальную часть
            String maskedLocal;
            if (local.length() <= 2) {
                maskedLocal = "*".repeat(local.length());
            } else {
                maskedLocal = local.charAt(0) + "*".repeat(local.length() - 2) + local.charAt(local.length() - 1);
            }

            // сохраняем @ и домен как есть
            return maskedLocal + data.substring(at);
             }
        if(data.matches("^\\+?\\d[\\d\\s\\-()]{7,}$")){
            data=data.substring(0,3)+"*".repeat(data.length()-4)+data.substring(data.length()-4);
            return data;
        }
        if(data.matches("^[A-Za-z0-9\\\\-]{6,}$"))
        {
            data=data.substring(0,3)+"*".repeat(data.length()-6);
            return data;
        }
    return null;
        //TODO сделать более универсальный и менее хрупкий maskData()
    }
    @Around("controllerLog()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime  = System.currentTimeMillis() - start;

        log.info("EXECUTION METHOD: {},{}\n EXECUTION TIME: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                executionTime);
        return proceed;
    }
    @AfterThrowing(throwing = "ex",pointcut = "controllerLog()")
    public void throwsException(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.error("EXCEPTION IN: {},{} WITH ARGUMENTS:{},EXCEPTION MESSAGE: {}",
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs()),
                ex.getMessage());
    }
}


