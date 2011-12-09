package ee.kovmen.entities;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import java.util.Set;

public class Teenus implements Serializable{
	
	 @Id
	 @GeneratedValue(strategy=GenerationType.AUTO)
	 private Long id;
	 public Long getId() {
	        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }
    
	private String name;	
	private String descrition;
	
	@ManyToMany
	private Set<Oigusakt> oigusaktid;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescrition() {
		return descrition;
	}
	public void setDescrition(String descrition) {
		this.descrition = descrition;
	}

	public void setOigusaktid(Set<Oigusakt> oigusaktid) {
		this.oigusaktid = oigusaktid;
	}

	public Set<Oigusakt> getOigusaktid() {
		return this.oigusaktid;
	}
	
}
