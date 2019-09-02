package com.fqserver.core.json;

import java.io.IOException;
import java.io.InputStream;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fqserver.core.json.JsonUtil.JsonCodec;

public class MsgPackUtil {

    public static final ICodec INSTANCE = new MsgPackCodec();

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

    public static class MsgPackCodec extends JsonCodec {
        @Override
        protected ObjectMapper initMapper() {
            return new ObjectMapper(new MessagePackFactory());
        }
    }
}
