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

import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author mgubaidullin
 * @author lauri - added lane support, added process datatypes support
 * 
 */
public class Process {

	private String id;
	
    private String name;
    private String state;
    private int x = 40;
    private int y = 40;
    private int width = 0;
    private int height = 0;
    private HashMap<String, ProcessDataType> dataTypes = new HashMap<String, ProcessDataType>();
    private HashMap<String, Step> steps = new HashMap<String, Step>();
    private HashMap<String, ProcessData> data=new HashMap<String, ProcessData>();
	private String laneName;
	private String laneId;

    public Process(String name) {
        this.name = name;
    }
    
    public void registerProcessDataType(String key, ProcessDataType data){
    	if(!dataTypes.containsKey(key)){
    		dataTypes.put(key, data);
    	}    	
    }
    
    public void registerProcessData(String key, ProcessData data){
    	if(!this.data.containsKey(key)){
    		this.data.put(key, data);
    	}    	
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addStep(String key, Step step) {
        this.steps.put(key, step);
    }

    public HashMap<String, Step> getSteps() {
        return steps;
    }

    public void setSteps(HashMap<String, Step> steps) {
        this.steps = steps;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

	public void setLane(String laneId, String laneName) {
		this.setLaneId(laneId);
		this.setLaneName(laneName);
		// TODO Auto-generated method stub
		
	}

	public void setLaneId(String laneId) {
		this.laneId = laneId;
	}

	public String getLaneId() {
		return laneId;
	}

	public void setLaneName(String laneName) {
		this.laneName = laneName;
	}

	public String getLaneName() {
		return laneName;
	}
	
	public ProcessDataType getProcessDataType(String dataTypeId) {
		return dataTypes.get(dataTypeId);		
	}

	public ProcessDataType getDataType(String dataTypeId) {
		for (Entry<String, ProcessData> d : data.entrySet()) {
			if(dataTypeId.equals(d.getValue().getName())){
				return d.getValue().getDataType();
			}
		}
		return null;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
