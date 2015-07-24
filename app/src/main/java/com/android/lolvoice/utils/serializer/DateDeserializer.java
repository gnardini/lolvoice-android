package com.android.lolvoice.utils.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2)
            throws JsonParseException {
        String originalDate = element.toString();
        char[] dateChar = originalDate.toCharArray();
        char[] dateCharConverted = new char[dateChar.length - 2];
        for (int i = 1; i < dateChar.length - 1; i++) {
            dateCharConverted[i - 1] = dateChar[i];
        }
        String date = new String(dateCharConverted);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return formatter.parse(date);
        } catch (ParseException e3) {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                originalDate = originalDate.substring(1, originalDate.length() - 2);
                return formatter.parse(originalDate);
            } catch (ParseException e4) {
                throw new JsonParseException(e4);
            }
        }
    }
}