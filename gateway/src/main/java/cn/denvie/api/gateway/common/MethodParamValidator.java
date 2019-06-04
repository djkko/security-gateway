package cn.denvie.api.gateway.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 方法参数校验工具。
 *
 * @author DengZhaoyong
 * @version 1.2.2
 */
@Component
public class MethodParamValidator {

    // org.hibernate.validator.internal.engine.ValidatorImpl
    @Autowired
    private Validator validator;
    /*@Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;*/

    public Set<ConstraintViolation<Object>> validate(Object object) {
        Set<ConstraintViolation<Object>> violationSet = validator.validate(object);
        /*for (ConstraintViolation<Object> constraintViolation : violationSet) {
            System.err.println("violation :: " + constraintViolation.getMessage());
        }*/
        return violationSet;
    }

}
