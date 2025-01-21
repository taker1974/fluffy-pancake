package ru.hogwarts.school.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.hogwarts.school.exception.student.BadStudentAgeException;
import ru.hogwarts.school.exception.student.BadStudentNameException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.tools.StringEx;

@Aspect
@Component
public class CheckStudentInterceptor {

    public static final int MIN_NAME_LENGTH = 1;
    public static final int MAX_NAME_LENGTH = 1024;
    public static final int MIN_AGE = 6;

    @Before(value = "@annotation(CheckStudent)")
    public void checkStudent(ProceedingJoinPoint joinPoint)
            throws Throwable {

        Object[] args = joinPoint.getArgs();
        if (args.length == 1 && args[0] instanceof Student student) {

            if (StringEx.getMeaningful(student.getName(), MIN_NAME_LENGTH, MAX_NAME_LENGTH).isEmpty()) {
                throw new BadStudentNameException(MIN_NAME_LENGTH, MAX_NAME_LENGTH);
            }

            if (student.getAge() < MIN_AGE) {
                throw new BadStudentAgeException(MIN_AGE);
            }
        }

        joinPoint.proceed();
    }
}
