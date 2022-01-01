package com.bwgjoseph.springbootcsstack.services.advice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdviceService {
    /**
     * Given an object, find all fields marked with the given annotated class
     *
     * @param {Object} object
     * @param {Class<? extends Annotation>} annotation class to search
     * @return fields declared with annotated class
     */
    public List<Field> findFieldsAnnotatedWithAndDefine(Object object, Class<? extends Annotation> annotationKlass) {
        return this.findAll(object, annotationKlass, new ArrayList<>());
    }

    /**
     * Recursively find all object annotated with given annotation
     *
     * @param {Object} object
     * @param {Class<? extends Annotation>} annotation class to search
     * @param {List<Field>} stores all found annotated field
     * @return fields declared with annotated class
     */
    private List<Field> findAll(Object obj, Class<? extends Annotation> annotationKlass, List<Field> annotatedFields) {
        // if an object is mine (isThisMyObject) but not annoatated with `annotationKlass` (@Last) and not defined
        // then `obj` will be null
        // if this object is not declard with the required annotation, then it shouldn't be handled, and
        // just let it be

        // now there's another problem, if child is null (SOI), then it wouldn't even process and thus
        // won't set the LastUpdate object in child object (SOI)
        if (obj == null) return new ArrayList<>();
        // This will get us all the declared fields on the object including parent/grandparent classes
        Field[] allFields = FieldUtils.getAllFields(obj.getClass());

        LastUpdate lu = LastUpdate.builder()
                                    .at(LocalDateTime.now())
                                    .by("user")
                                    .build();

        for (Field field : allFields) {
            // Pick all the fields that is annotated with `@Last`
            if (field.isAnnotationPresent(annotationKlass)) {
                log.info("f {} has @last, adding it", field.getName());
                annotatedFields.add(field);

                try {
                    field.setAccessible(true);
                    field.set(obj, lu);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error("Unable to set object {}", obj, e);
                }
            }

            // Any field that is not annotated with `@Last` but is my pojo that I own
            // Traverse into the object, and see if we can find any field annotated with `@Last`
            // Note that this will dig recursively until the condition does not match
            if (!field.isAnnotationPresent(annotationKlass) && isThisMyObject(field)) {
                log.info("f {} does not has @last, but is my pojo, so let's dig in", field.getName());
                try {
                    field.setAccessible(true);
                    this.findAll(field.get(obj), annotationKlass, annotatedFields);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error(e.toString());
                }
            }
        }

        return annotatedFields;
    }

    // only dig into the class if it's my own pojo declaration
    private boolean isThisMyObject(Field f) {
        return f.getType().getName().startsWith("com.bwgjoseph.springbootcsstack");
    }

    /**
     * Wanted to take in a list of the `annotatedFields` and set the value in here
     * so that the process of getting the annotated fields, and setting it will be
     * separately but it seem that because the object hierachy is not known in `annotatedFields`
     * it makes it very difficult to set the value for child classes (SOI)
     *
     * @param annotatedFields
     * @param object
     */
    public void updateAuditInfo(List<Field> annotatedFields, Object object) {
        for (Field field : annotatedFields) {
            log.info("class {}, getDeclaringClass {}, name {}, type {}", field.getClass(), field.getDeclaringClass(), field.getName(), field.getType());

            LastUpdate lu = LastUpdate.builder()
                                        .at(LocalDateTime.now())
                                        .by("user")
                                        .build();

            try {
                field.setAccessible(true);
                // need to somehow figure out if it's the child object
                // and call itself and pass in the child object
                // this.updateAuditInfo(List.of(field), field.get(object))
                // field.get(object) is not working, and it's not getting any object back
                field.set(object, lu);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                System.out.println("cant set");
            }
        }
    }
}
