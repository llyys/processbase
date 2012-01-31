/**
 * Copyright (C) 2010 PROCESSBASE
 * PROCESSBASE Ltd, Almaty, Kazakhstan
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.ui.core.bonita.diagram;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;
import org.ow2.bonita.light.LightActivityInstance;

/**
 *
 * @author mgubaidullin
 */
public class Diagram  {
	BufferedImage processImage = null;
    private static HashMap<String, BufferedImage> images =null;
    Set<LightActivityInstance> activityInstances;
	private final Process process;

    public Diagram(byte[] imageBytes, Process process, Set<LightActivityInstance> activityInstances) {
        this.process = process;
		try {
            this.activityInstances = getLastActivities(activityInstances);
            this.processImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if(images!=null)
            	return;
            
            images= new HashMap<String, BufferedImage>();
            images.put("READY", ImageIO.read(getClass().getResource("/icons/start.png")));
            images.put("EXECUTING", ImageIO.read(getClass().getResource("/icons/document.png")));
            images.put("FINISHED", ImageIO.read(getClass().getResource("/icons/accept.png")));
            images.put("SUSPENDED", ImageIO.read(getClass().getResource("/icons/pause.png")));
            images.put("INITIAL", ImageIO.read(getClass().getResource("/icons/start.png")));
            images.put("ABORTED", ImageIO.read(getClass().getResource("/icons/document-delete.png")));
            images.put("CANCELLED", ImageIO.read(getClass().getResource("/icons/cancel.png")));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    protected Set<LightActivityInstance> getLastActivities(Set<LightActivityInstance> activities) {
	    HashMap<String, LightActivityInstance> m0 = new HashMap<String, LightActivityInstance>();
	    for (LightActivityInstance lai : activities) {
	        LightActivityInstance newLai = m0.get(lai.getActivityName());
	        if (newLai != null) {
	            if (newLai.getReadyDate().compareTo(lai.getReadyDate()) < 0) {
	                m0.put(lai.getActivityName(), lai);
	            }
	        } else {
	            m0.put(lai.getActivityName(), lai);
	        }
	    }
	    return new HashSet<LightActivityInstance>(m0.values());
	}

    private static BufferedImage getImage(String type) {
        return images.get(type);
    }
    protected String getStepState(String name) {
	    for (Iterator iter = activityInstances.iterator(); iter.hasNext();) {
	        LightActivityInstance ai = (LightActivityInstance) iter.next();
	        if (ai.getActivityName().equals(name)) {
	            return ai.getState().toString();
	        }
	    }
	    return null;
	}

    public byte[] getImage() throws IOException {
            
            float processWidth = (float)process.getWidth();
			float coefficientX = (1-processWidth/processImage.getWidth());
			
            int processHeigh = process.getHeight();
            float coefficientY = (1-processHeigh/processImage.getHeight());
			
            // well there is a bug in bonita software. Rendered image and task positions are totally off. But when process designer minimizes the diagram into right propotions then positions are ok.
            if(process.getWidth()==0 || coefficientX==0)
            	coefficientX=1.0F;
            if(process.getHeight()==0 || coefficientY==0)
            	coefficientY=1.0F;

            for (String skey : process.getSteps().keySet()) {
                Step step = process.getSteps().get(skey);
                String stepState = getStepState(step.getName());
				if (stepState != null) {
                    BufferedImage ind = Diagram.getImage(stepState);
                    String type = step.getType();
					if (type.equals("process:Task") || type.equals("process:Activity") || type.equals("process:SubProcess")) {
                        int x = (int) ((step.getX() * coefficientX)  + ((step.getWidth() * coefficientX) / 2));
						int y = (int) ((step.getY() * coefficientY ) + ((step.getHeight() * coefficientY ) / 2));
						processImage.createGraphics().drawImage(ind,x,y,null);
                    } else {
                        //System.out.println(" DEBUG: " + step.getName() + (step.getX() * coefficientX / 100 + (step.getWidth() * coefficientX / 100) / 2) + " " + (step.getY() * coefficientY / 100 + (step.getHeight() * coefficientY / 100) / 2));
                        int x = (int) (step.getX() * coefficientX);
						int y = (int) (step.getY() * coefficientY);
						processImage.createGraphics().drawImage(ind,x,y,null);
                    }
                }
            }
        
        processImage.createGraphics().dispose();
        ByteArrayOutputStream ios = new ByteArrayOutputStream();
        ImageIO.write(processImage, "PNG", ios);
        ios.close();
        return ios.toByteArray();
    }
}
