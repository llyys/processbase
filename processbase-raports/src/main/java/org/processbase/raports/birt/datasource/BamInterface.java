package org.processbase.raports.birt.datasource;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bonitasoft.console.client.reporting.TimeUnit;
import org.bonitasoft.console.server.bam.birt.datasource.BamUtil;
import org.ow2.bonita.facade.BAMAPI;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition.Type;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.util.AccessorUtil;

public class BamInterface {

	public BamInterface(){
		
	}
	
	public SortedMap<Date, Long> getProcessInstancesDuration(Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getProcessInstancesDuration(new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getProcessInstancesDuration(ProcessDefinitionUUID processUUID,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getProcessInstancesDuration(processUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getActivityInstancesDuration(Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getActivityInstancesDuration(new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	
	public SortedMap<Date, Long> getActivityInstancesDuration(ActivityDefinitionUUID activityUUID,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;

		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getActivityInstancesDuration(activityUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getActivityInstancesDuration(ProcessDefinitionUUID processUUID,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getActivityInstancesDuration(processUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getActivityInstancesDurationByActivityType(Type type,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getActivityInstancesDurationByActivityType(type,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getActivityInstancesDurationByActivityType(Type type, ProcessDefinitionUUID processUUID,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getActivityInstancesDurationByActivityType(type,processUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	

	public SortedMap<Date, Long> getActivityInstancesExecutionTime(Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getActivityInstancesExecutionTime(new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getActivityInstancesExecutionTime(ProcessDefinitionUUID processUUID,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getActivityInstancesExecutionTime(processUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getActivityInstancesExecutionTime(ActivityDefinitionUUID activityUUID,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getActivityInstancesExecutionTime(activityUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getNumberOfCreatedProcessInstances(Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			long nb  = api.getNumberOfCreatedProcessInstances(new Date(s), new Date(s+interval)) ;
			result.put(new Date(s), nb) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getNumberOfCreatedProcessInstances(ProcessDefinitionUUID processUUID,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			long nb  = api.getNumberOfCreatedProcessInstances(processUUID,new Date(s), new Date(s+interval)) ;
			result.put(new Date(s), nb) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getTaskInstancesWaitingTime(Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getTaskInstancesWaitingTime(new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getTaskInstancesWaitingTime(ProcessDefinitionUUID processUUID,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getTaskInstancesWaitingTime(processUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getTaskInstancesWaitingTime(ActivityDefinitionUUID activityUUID,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getTaskInstancesWaitingTime(activityUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getTaskInstancesWaitingTimeOfUser(String user,Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getTaskInstancesWaitingTimeOfUser(user,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getTaskInstancesWaitingTimeOfUser(String user, ActivityDefinitionUUID taskUUID, Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getTaskInstancesWaitingTimeOfUser(user,taskUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
	
	public SortedMap<Date, Long> getTaskInstancesWaitingTimeOfUser(String user, ProcessDefinitionUUID processUUID, Date start,Date end,TimeUnit timeUnit) {
		BAMAPI api = AccessorUtil.getBAMAPI() ;
		long s = start.getTime() ;
		long e = end.getTime() ;
		long interval = BamUtil.getTimeUnitInMillisecond(timeUnit) ;
	
		if(interval == 0)
			return null ;

		SortedMap<Date,Long> result  = new TreeMap<Date, Long>();
		while(s+interval <= e){
			List<Long> durations = api.getTaskInstancesWaitingTimeOfUser(user,processUUID,new Date(s), new Date(s+interval)) ;
			long avg = BamUtil.getAverageDuration(durations) ;
			result.put(new Date(s), avg) ;
			s = s+interval ;
		}

		return result;
	}
}

