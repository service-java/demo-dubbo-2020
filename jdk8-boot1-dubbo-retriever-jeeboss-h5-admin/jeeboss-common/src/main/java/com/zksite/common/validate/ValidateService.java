package com.zksite.common.validate;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zksite.common.constant.ErrorCode;
import com.zksite.common.exception.BizException;

@Component
public class ValidateService {

    @Autowired
    private Validator validator;

    /**
     * @param object
     * @throws BizException
     */
    public void validate(Object object) throws BizException {
        Set<ConstraintViolation<Object>> all = validator.validate(object);
        StringBuilder sb = new StringBuilder();
        if (all.size() > 0) {
            for (ConstraintViolation<Object> constraintViolation : all) {
                sb.append(constraintViolation.getMessage() + " ");
            }
            throw new BizException(ErrorCode.INVALID_PARAMETER.getErrcode(), sb.toString());
        }
    }
}
