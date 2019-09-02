package com.fqserver.core.json;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fqserver.core.json.JsonUtil.JsonCodec;

public class CborUtil {

    public static final ICodec INSTANCE = new CborCodec();

    public static <T> T jsonToObject(byte[] json) throws JsonParseException, JsonMappingException,
            IOException {
        return INSTANCE.decode(json);
    }

    public static <T> T jsonToObject(String json) throws JsonParseException, JsonMappingException,
            IOException {
        return INSTANCE.decode(json);
    }

    public static <T> T jsonToObject(InputStream in) throws JsonParseException,
            JsonMappingException, IOException {
        return INSTANCE.decode(in);
    }

    public static String objectToJson(Object value) throws JsonProcessingException {
        return INSTANCE.encode(value);
    }

    public static byte[] objectToJsonByte(Object value) throws JsonProcessingException {
        return INSTANCE.encodeByte(value);
    }

    public static class CborCodec extends JsonCodec {
        @Override
        protected ObjectMapper initMapper() {
            return new ObjectMapper(new CBORFactory());
        }
    }
}
