/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.processbase.temp;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author mgubaidullin
 */
public class DumbJob implements Job{

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("JOB EXECUTED !");
    }

}
