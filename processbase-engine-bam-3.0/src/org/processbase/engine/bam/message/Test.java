/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.engine.bam.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;


/**
 *
 * @author marat
 */
public class Test {

    public static void main(String[] args) throws DatatypeConfigurationException, JAXBException {

        DimensionType dim1 = new DimensionType();
        dim1.setCode("D00001");
        dim1.setValue("HELLO");
        dim1.setType("java.lang.String");

        DimensionType dim2 = new DimensionType();
        dim2.setCode("D00002");
        dim2.setValue(Integer.parseInt("123"));
        dim2.setType("int");


        DimensionType dim3 = new DimensionType();
        dim3.setCode("D00003");
        dim3.setValue(Long.parseLong("1234567890"));
        dim3.setType("long");

        FactType fact = new FactType();
        fact.setCode("F00001");
        fact.setValue(new BigDecimal("120200.2342"));

        Kpi kpi = new Kpi();
        kpi.setCode("K00001");
        Timestamp curr = new Timestamp(System.currentTimeMillis());
//        curr.setNanos(123456789);
        kpi.setTimeStamp(curr);
        kpi.setEventID("Event");
        kpi.setServerID("serverid");
        kpi.getFacts().add(fact);
        kpi.getDims().add(dim1);
        kpi.getDims().add(dim2);
        kpi.getDims().add(dim3);

        JAXBContext context = JAXBContext.newInstance(Kpi.class);
        Marshaller marshaller = context.createMarshaller();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        marshaller.marshal(kpi, baos);
        String z = new String(baos.toByteArray());
        System.out.println(z);
//        System.out.println(z.length());
//
//        Unmarshaller unmarshaller = context.createUnmarshaller();
//        Kpi res =  (Kpi) unmarshaller.unmarshal(new ByteArrayInputStream(z.getBytes()));
//        System.out.println(res.getKpi().getTimeStamp().toString());
//        System.out.println(res.getFact().get(0).getValue());


        GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSZ");
        gb.registerTypeAdapter(Timestamp.class, new sqlTimestampConverter());

        Gson gson = gb.create();
        String x = gson.toJson(kpi);
        System.out.println(x);
//        System.out.println(x.length());

        Kpi zzz = gson.fromJson(x, Kpi.class);
//        System.out.println(zzz.getDims().get(0).getCode());
//        System.out.println(zzz.getTimeStamp().toString());
    }

    static private class sqlTimestampConverter implements JsonSerializer<Timestamp> {
        static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSZ");

        public JsonElement serialize(Timestamp t, Type type, JsonSerializationContext jsc) {
            return new JsonPrimitive(sdf.format(t));
        }
    }
}
