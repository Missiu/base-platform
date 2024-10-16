package com.example.template.config.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;

import java.io.IOException;

/**
 * 自定义大数字序列化器。
 * 该序列化器会检查数字是否在 JavaScript 的安全范围内，
 * 如果超出该范围，将数字作为字符串进行序列化，以防止精度丢失。
 *
 * @author hzh
 */
@JacksonStdImpl
public class BigNumberSerializer extends NumberSerializer {

    /**
     * JavaScript 中的最大安全整数值。
     */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;

    /**
     * JavaScript 中的最小安全整数值。
     */
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    /**
     * 序列化器的单例实例。
     */
    public static final BigNumberSerializer INSTANCE = new BigNumberSerializer(Number.class);

    /**
     * 构造一个新的 {@code BigNumberSerializer} 实例。
     *
     * @param rawType 要序列化的数字类型
     */
    public BigNumberSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    /**
     * 重写序列化方法。如果数字在安全范围内，正常序列化；否则，将数字序列化为字符串。
     *
     * @param value    要序列化的数字
     * @param gen      JSON 生成器
     * @param provider 序列化器提供者
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        long longValue = value.longValue();

        if (longValue > MIN_SAFE_INTEGER && longValue < MAX_SAFE_INTEGER) {
            // 如果在安全范围内，使用默认的数字序列化方式
            super.serialize(value, gen, provider);
        } else {
            // 如果超出安全范围，将其作为字符串进行序列化
            gen.writeString(value.toString());
        }
    }
}
