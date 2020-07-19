package com.wxl.dyttcrawler.web.dto.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.wxl.dyttcrawler.web.dto.AggOneResult;

import java.io.IOException;

/**
 * Create by wuxingle on 2020/7/16
 * 聚合结果序列化
 */
public class AggResultSerializer extends JsonSerializer<AggOneResult> {


    @Override
    public void serialize(AggOneResult value,
                          JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.getData());
    }


}
