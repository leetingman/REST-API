package me.liting.restapiwithspring.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator jen, SerializerProvider serializerProvider) throws IOException {
        jen.writeStartArray();
        errors.getFieldErrors().forEach(e->{
            try {
                jen.writeStartObject();
                jen.writeStringField("field",e.getField());
                jen.writeStringField("objectName",e.getObjectName());
                jen.writeStringField("code",e.getCode());
                jen.writeStringField("defaultMessage",e.getDefaultMessage());
                Object rejectedValue=e.getRejectedValue();
                if(rejectedValue!=null){
                    jen.writeStringField("rejectValue",rejectedValue.toString());
                }

                jen.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        errors.getGlobalErrors().forEach(e->{
            try {
                jen.writeStartObject();
                jen.writeStringField("objectName",e.getObjectName());
                jen.writeStringField("code",e.getCode());
                jen.writeStringField("defaultMessage",e.getDefaultMessage());
                jen.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        jen.writeEndArray();
    }
}
