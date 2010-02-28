/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.processbase.bam.mdb;

import java.math.BigDecimal;
import java.util.Date;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.processbase.bam.message.KPIMessage;

/**
 *
 * @author mgubaidullin
 */
@MessageDriven(mappedName = "jms/processbaseBAM_DR", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "BAMEventMessageBean"),
    @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "BAMEventMessageBean")
})
public class BAMEventMessageBean implements MessageListener {

    public BAMEventMessageBean() {
    }

    public void onMessage(Message message) {
        try {
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            System.out.println("BAMEventMessageBean");
            KPIMessage kpiMessage = (KPIMessage) ((ObjectMessage) message).getObject();
            PbEvent event = new PbEvent();
            event.setEventId(kpiMessage.getEventId());
            event.setTimeStamp(kpiMessage.getTimeStamp());
            event.setServerId(kpiMessage.getServerId());
            event.setCurrentState(kpiMessage.getCurrentState());
            event.setProcessDefinitionId(kpiMessage.getProcessDefinitionId());
            event.setProcessInstanceId(kpiMessage.getProcessInstanceId());
            event.setProcessName(kpiMessage.getProcessName());
            event.setActivityDefinitionId(kpiMessage.getActivityDefinitionId());
            event.setActivityInstanceId(kpiMessage.getActivityInstanceId());
            event.setActivityInstanceIter(kpiMessage.getActivityInstanceIter());
            event.setKpiCode(kpiMessage.getKpiCode());
            event.setKpiName(kpiMessage.getKpiName());
            if (kpiMessage.getFactValue() instanceof Date) {
                event.setFactValueTimestamp((Date) kpiMessage.getFactValue());
            } else if (kpiMessage.getFactValue() instanceof BigDecimal) {
                event.setFactValueNumeric((BigDecimal) kpiMessage.getFactValue());
            } else if (kpiMessage.getFactValue() instanceof Integer) {
                event.setFactValueNumeric(new BigDecimal(((Integer) kpiMessage.getFactValue()).intValue()));
            } else if (kpiMessage.getFactValue() instanceof Long) {
                event.setFactValueNumeric(new BigDecimal(((Long) kpiMessage.getFactValue()).longValue()));
            } else if (kpiMessage.getFactValue() instanceof Number) {
                event.setFactValueNumeric(new BigDecimal(((Number) kpiMessage.getFactValue()).longValue()));
            } else {
                event.setFactValueString(kpiMessage.getFactValue().toString());
            }
            if (kpiMessage.getDimensions().size() >= 1) {
                String dimName = (String) kpiMessage.getDimensions().get(0).get(0);
                Object dimValue = (String) kpiMessage.getDimensions().get(0).get(1);
                event.setDim1Name(dimName);
                if (dimValue instanceof Date) {
                    event.setDim1ValueTimestamp((Date) dimValue);
                } else if (dimValue instanceof BigDecimal) {
                    event.setDim1ValueNumeric((BigDecimal) dimValue);
                } else if (dimValue instanceof Integer) {
                    event.setDim1ValueNumeric(new BigDecimal(((Integer) dimValue).intValue()));
                } else if (dimValue instanceof Long) {
                    event.setDim1ValueNumeric(new BigDecimal(((Long) dimValue).longValue()));
                } else if (dimValue instanceof Number) {
                    event.setDim1ValueNumeric(new BigDecimal(((Number) dimValue).longValue()));
                } else {
                    event.setDim1ValueString(dimValue.toString());
                }
            }
            if (kpiMessage.getDimensions().size() >= 2) {
                String dimName = (String) kpiMessage.getDimensions().get(1).get(0);
                Object dimValue = (String) kpiMessage.getDimensions().get(1).get(1);
                event.setDim2Name(dimName);
                if (dimValue instanceof Date) {
                    event.setDim2ValueTimestamp((Date) dimValue);
                } else if (dimValue instanceof BigDecimal) {
                    event.setDim2ValueNumeric((BigDecimal) dimValue);
                } else if (dimValue instanceof Integer) {
                    event.setDim2ValueNumeric(new BigDecimal(((Integer) dimValue).intValue()));
                } else if (dimValue instanceof Long) {
                    event.setDim2ValueNumeric(new BigDecimal(((Long) dimValue).longValue()));
                } else if (dimValue instanceof Number) {
                    event.setDim2ValueNumeric(new BigDecimal(((Number) dimValue).longValue()));
                } else {
                    event.setDim2ValueString(dimValue.toString());
                }
            }
            if (kpiMessage.getDimensions().size() >= 3) {
                String dimName = (String) kpiMessage.getDimensions().get(2).get(0);
                Object dimValue = (String) kpiMessage.getDimensions().get(2).get(1);
                event.setDim3Name(dimName);
                if (dimValue instanceof Date) {
                    event.setDim3ValueTimestamp((Date) dimValue);
                } else if (dimValue instanceof BigDecimal) {
                    event.setDim3ValueNumeric((BigDecimal) dimValue);
                } else if (dimValue instanceof Integer) {
                    event.setDim3ValueNumeric(new BigDecimal(((Integer) dimValue).intValue()));
                } else if (dimValue instanceof Long) {
                    event.setDim3ValueNumeric(new BigDecimal(((Long) dimValue).longValue()));
                } else if (dimValue instanceof Number) {
                    event.setDim3ValueNumeric(new BigDecimal(((Number) dimValue).longValue()));
                } else {
                    event.setDim3ValueString(dimValue.toString());
                }
            }
            if (kpiMessage.getDimensions().size() >= 4) {
                String dimName = (String) kpiMessage.getDimensions().get(3).get(0);
                Object dimValue = (String) kpiMessage.getDimensions().get(3).get(1);
                event.setDim4Name(dimName);
                if (dimValue instanceof Date) {
                    event.setDim4ValueTimestamp((Date) dimValue);
                } else if (dimValue instanceof BigDecimal) {
                    event.setDim4ValueNumeric((BigDecimal) dimValue);
                } else if (dimValue instanceof Integer) {
                    event.setDim4ValueNumeric(new BigDecimal(((Integer) dimValue).intValue()));
                } else if (dimValue instanceof Long) {
                    event.setDim4ValueNumeric(new BigDecimal(((Long) dimValue).longValue()));
                } else if (dimValue instanceof Number) {
                    event.setDim4ValueNumeric(new BigDecimal(((Number) dimValue).longValue()));
                } else {
                    event.setDim4ValueString(dimValue.toString());
                }
            }
            if (kpiMessage.getDimensions().size() >= 5) {
                String dimName = (String) kpiMessage.getDimensions().get(4).get(0);
                Object dimValue = (String) kpiMessage.getDimensions().get(4).get(1);
                event.setDim5Name(dimName);
                if (dimValue instanceof Date) {
                    event.setDim5ValueTimestamp((Date) dimValue);
                } else if (dimValue instanceof BigDecimal) {
                    event.setDim5ValueNumeric((BigDecimal) dimValue);
                } else if (dimValue instanceof Integer) {
                    event.setDim5ValueNumeric(new BigDecimal(((Integer) dimValue).intValue()));
                } else if (dimValue instanceof Long) {
                    event.setDim5ValueNumeric(new BigDecimal(((Long) dimValue).longValue()));
                } else if (dimValue instanceof Number) {
                    event.setDim5ValueNumeric(new BigDecimal(((Number) dimValue).longValue()));
                } else {
                    event.setDim5ValueString(dimValue.toString());
                }
            }
            if (kpiMessage.getDimensions().size() >= 6) {
                String dimName = (String) kpiMessage.getDimensions().get(5).get(0);
                Object dimValue = (String) kpiMessage.getDimensions().get(5).get(1);
                event.setDim6Name(dimName);
                if (dimValue instanceof Date) {
                    event.setDim6ValueTimestamp((Date) dimValue);
                } else if (dimValue instanceof BigDecimal) {
                    event.setDim6ValueNumeric((BigDecimal) dimValue);
                } else if (dimValue instanceof Integer) {
                    event.setDim6ValueNumeric(new BigDecimal(((Integer) dimValue).intValue()));
                } else if (dimValue instanceof Long) {
                    event.setDim6ValueNumeric(new BigDecimal(((Long) dimValue).longValue()));
                } else if (dimValue instanceof Number) {
                    event.setDim6ValueNumeric(new BigDecimal(((Number) dimValue).longValue()));
                } else {
                    event.setDim6ValueString(dimValue.toString());
                }
            }
            if (kpiMessage.getDimensions().size() == 7) {
            }
            if (kpiMessage.getDimensions().size() == 8) {
            }
            if (kpiMessage.getDimensions().size() == 9) {
            }
            if (kpiMessage.getDimensions().size() == 10) {
            }

            BAMHibernateUtil hutil = new BAMHibernateUtil();
            hutil.saveEvent(event);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
