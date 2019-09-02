package com.fqserver.core.json;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    public static final ICodec INSTANCE = new JsonCodec();

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

    public static class JsonCodec implements ICodec {
        private final ObjectMapper mapper = initMapper();

        public JsonCodec() {
            init(this.mapper);
        }

        public ObjectMapper getMapper() {
            return this.mapper;
        }

        protected ObjectMapper initMapper() {
            return new ObjectMapper();
        }

        private void init(ObjectMapper mapper) {

            try {
                Class<?> c = Class.forName("com.fasterxml.jackson.module.afterburner.AfterburnerModule");
                if (c != null) {
                    this.mapper.registerModule((Module) c.newInstance());
                }
            }
            catch (ClassNotFoundException e) {
                System.out.println("ClassNotFoundException:" + e);
            }
            catch (InstantiationException e) {
                System.out.println("InstantiationException:" + e);
            }
            catch (IllegalAccessException e) {
                System.out.println("IllegalAccessException:" + e);
            }
            // 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
            this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            this.mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
            this.mapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

            this.mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
            this.mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
            this.mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

            // // 让jackson支持jaxb注解的配置
            // this.mapper.registerModule(new JaxbAnnotationModule());
            //
            // // 为null的属性值不映射
            // this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);//
            // @JsonInclude(Include.NON_NULL)
            //
            // // 排序
            // this.mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            //
            // // Feature that determines whether Map entries with null values
            // are
            // to
            // // be serialized (true) or not (false)
            // this.mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.sdk.core.json.ICodec#decode(byte[])
         */
        @Override
        public <T> T decode(byte[] json) throws JsonParseException, JsonMappingException,
                IOException {
            return this.mapper.readValue(json, new TypeReference<T>() {});
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.sdk.core.json.ICodec#decode(java.lang.String)
         */
        @Override
        public <T> T decode(String json) throws JsonParseException, JsonMappingException,
                IOException {
            return this.mapper.readValue(json, new TypeReference<T>() {});
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.sdk.core.json.ICodec#decode(java.io.InputStream)
         */
        @Override
        public <T> T decode(InputStream in) throws JsonParseException, JsonMappingException,
                IOException {
            return this.mapper.readValue(in, new TypeReference<T>() {});
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.sdk.core.json.ICodec#encode(java.lang.Object)
         */
        @Override
        public String encode(Object value) throws JsonProcessingException {
            return this.mapper.writeValueAsString(value);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.sdk.core.json.ICodec#encodeByte(java.lang.Object)
         */
        @Override
        public byte[] encodeByte(Object value) throws JsonProcessingException {
            return this.mapper.writeValueAsBytes(value);
        }
    }
}
