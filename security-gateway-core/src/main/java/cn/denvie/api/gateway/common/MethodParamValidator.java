package cn.denvie.api.gateway.common;

import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.util.HashSet;
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

    /**
     * 验证方法中的Bean参数。
     */
    public Set<ConstraintViolation<Object>> validate(Object object) {
        try {
            Set<ConstraintViolation<Object>> violationSet = validator.validate(object);
            /*for (ConstraintViolation<Object> constraintViolation : violationSet) {
                System.err.println("violation :: " + constraintViolation.getMessage());
            }*/
            return violationSet;
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    /**
     * 验证方法参数。
     */
    public Set<ConstraintViolation<Object>> validateParameters(Object object,
                                                               Method method,
                                                               Object[] parameterValues) {
        try {
            LocalValidatorFactoryBean validatorFactoryBean = (LocalValidatorFactoryBean) validator;
            ValidatorImpl validatorImpl = (ValidatorImpl) validatorFactoryBean.getValidator();
            Set<ConstraintViolation<Object>> constraintViolations = validatorImpl.validateParameters(
                    object, method, parameterValues);
            return constraintViolations;
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

}
