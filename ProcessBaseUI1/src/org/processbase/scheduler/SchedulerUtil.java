/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.scheduler;

import java.util.ArrayList;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

/**
 *
 * @author mgubaidullin
 */
public class SchedulerUtil {

    SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();

    public SchedulerFactory getSchedulerFactory() {
        return schedulerFactory;
    }

    public void addJob(String jobName, String jobGroup, Class jobClass, boolean isDurable) throws SchedulerException {
        JobDetail job = new JobDetail(jobName, jobGroup, jobClass);
        schedulerFactory.getScheduler().addJob(job, isDurable);
    }

    public ArrayList<JobDetail> getJobList() throws SchedulerException {
        String[] jobGroups;
        String[] jobsNames;
        ArrayList<JobDetail> result = new ArrayList<JobDetail>();
        Scheduler scheduler = schedulerFactory.getScheduler();
        jobGroups = scheduler.getJobGroupNames();
        for (int i = 0; i < jobGroups.length; i++) {
            jobsNames = scheduler.getJobNames(jobGroups[i]);
            for (int j = 0; j < jobsNames.length; j++) {
                result.add(scheduler.getJobDetail(jobsNames[j], jobGroups[i]));
            }
        }
        return result;
    }
}
